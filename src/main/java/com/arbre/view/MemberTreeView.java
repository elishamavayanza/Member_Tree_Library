package com.arbre.view;

import com.arbre.controller.MemberController;
import com.arbre.model.Member;
import com.arbre.layout.TreeLayoutCalculator;
import com.arbre.layout.TreeLayoutCalculator.Connection;
import com.arbre.layout.TreeLayoutCalculator.NodeBounds;
import com.arbre.layout.TreeLayoutCalculator.TreeLayout;
import com.arbre.layout.TreeLayoutCalculator.TextMeasurer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.List;

public class MemberTreeView extends JPanel {
    private final MemberController controller;
    private Member rootMember;
    private boolean darkMode = true;

    private LayoutDirection layoutDirection = LayoutDirection.VERTICAL;
    private final TreeLayoutCalculator calculator;
    private final Stack<Member> backStack = new Stack<>();
    private final Stack<Member> forwardStack = new Stack<>();
    private Member hoveredMember = null;
    private Member selectedMember = null;

    public MemberTreeView(MemberController controller) {
        this.controller = controller;
        TextMeasurer measurer = (text, minWidth) -> {
            Font font = new Font("Segoe UI", Font.PLAIN, 14);
            FontMetrics fm = getFontMetrics(font);
            return Math.max(fm.stringWidth(text), minWidth);
        };
        this.calculator = new TreeLayoutCalculator(measurer, 40, 100, 80, 30,10, 0);

        setBackground(darkMode ? new Color(10, 10, 25) : Color.WHITE);
        ToolTipManager.sharedInstance().registerComponent(this);

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoveredMember = findMemberAt(e.getPoint()); repaint(); }
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredMember = null; repaint(); }
            @Override
            public void mouseClicked(MouseEvent e) {
                Member m = findMemberAt(e.getPoint());
                if (m != null) {
                    if (e.getClickCount() == 2) {
                        if (m.getChildren().isEmpty())
                            m.getChildren().addAll(controller.getSubMembers(m));
                        navigateTo(m);
                    } else {
                        selectedMember = m; repaint();
                    }
                }
            }
        };
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private Member findMemberAt(Point p) {
        if (rootMember == null) return null;
        TreeLayout layout = calculator.computeLayout(rootMember, getWidth(), getHeight(), layoutDirection);
        Map<String, Member> idMap = new HashMap<>();
        buildIdMap(rootMember, idMap);
        for (NodeBounds nb : layout.getNodes()) {
            Rectangle r = new Rectangle(nb.x, nb.y, nb.width, nb.height);
            if (r.contains(p)) return idMap.get(nb.id);
        }
        return null;
    }

    private void buildIdMap(Member m, Map<String, Member> map) {
        map.put(m.getId(), m);
        for (Member child : m.getChildren()) buildIdMap(child, map);
    }

    private void navigateTo(Member m) {
        backStack.push(rootMember); forwardStack.clear(); rootMember = m; selectedMember = null; repaint();
    }

    public void goBack() {
        if (!backStack.isEmpty()) {
            forwardStack.push(rootMember);
            rootMember = backStack.pop(); repaint();
        }
    }

    public void goForward() {
        if (!forwardStack.isEmpty()) {
            backStack.push(rootMember);
            rootMember = forwardStack.pop(); repaint();
        }
    }

    public void setLayoutDirection(LayoutDirection dir) { this.layoutDirection = dir; repaint(); }
    public void setRootMember(Member root) { this.rootMember = root; backStack.clear(); forwardStack.clear(); repaint(); }
    public void setDarkMode(boolean dark) { this.darkMode = dark; setBackground(dark ? new Color(10,10,25):Color.WHITE); repaint(); }

    @Override
    public String getToolTipText(MouseEvent e) {
        Member m = findMemberAt(e.getPoint());
        if (m != null) return String.format("<html><b>Nom :</b> %s<br><b>ID :</b> %s<br><b>Enfants :</b> %d</html>",
                m.getName(), m.getId(), m.getChildren().size());
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (rootMember == null) return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        TreeLayout layout = calculator.computeLayout(rootMember, getWidth(), getHeight(), layoutDirection);
        // draw connections
        for (Connection conn : layout.getConnections()) {
            List<TreeLayoutCalculator.Point> pts = conn.controlPoints;
            CubicCurve2D.Float curve = new CubicCurve2D.Float(
                    pts.get(0).x, pts.get(0).y,
                    pts.get(1).x, pts.get(1).y,
                    pts.get(2).x, pts.get(2).y,
                    pts.get(3).x, pts.get(3).y
            );
            g2.setColor(darkMode ? Color.WHITE : Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(1.5f)); g2.draw(curve);
        }
        // draw nodes
        for (NodeBounds nb : layout.getNodes()) {
            g2.setColor(darkMode ? Color.LIGHT_GRAY : Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(nb.x, nb.y, nb.width, nb.height, 15, 15);
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(nb.name);
            int tx = nb.x + (nb.width - textW)/2;
            int ty = nb.y + (nb.height + fm.getAscent())/2 - 2;
            g2.drawString(nb.name, tx, ty);
            if (selectedMember != null && selectedMember.getId().equals(nb.id)) {
                g2.setColor(Color.YELLOW); g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(nb.x, nb.y, nb.width, nb.height, 15, 15);
            }
            if (hoveredMember != null && hoveredMember.getId().equals(nb.id)) {
                g2.setColor(new Color(255,255,255,100));
                g2.fillRoundRect(nb.x, nb.y, nb.width, nb.height, 15, 15);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (rootMember == null) return new Dimension(400, 300); // Valeur par défaut
        TreeLayout layout = calculator.computeLayout(rootMember, getWidth(), getHeight(), layoutDirection);

        int maxX = 0;
        int maxY = 0;
        for (NodeBounds node : layout.getNodes()) {
            maxX = Math.max(maxX, node.x + node.width);
            maxY = Math.max(maxY, node.y + node.height);
        }

        // Ajoute un peu de marge
        return new Dimension(maxX + 50, maxY + 50);
    }

}

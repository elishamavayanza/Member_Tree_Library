// src/main/java/com/arbre/view/MemberTreeView.java
package com.arbre.view;

import com.arbre.calculator.MemberTreeCalculator;
import com.arbre.controller.MemberController;
import com.arbre.model.Member;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Classe responsable du dessin de l'arbre des membres.
 * Gère les interactions : hover pour info, clic simple pour sélection,
 * et double-clic pour navigation dans l'arborescence.
 */
public class MemberTreeView extends JPanel {
    private final MemberController controller;
    private Member rootMember;
    private boolean darkMode = true;

    private final Map<Member, Rectangle> memberBounds = new HashMap<>();
    private int maxX = 0;
    private int maxY = 0;
    private LayoutDirection layoutDirection = LayoutDirection.VERTICAL;
    private final Stack<Member> navigationStack = new Stack<>();
    private final MemberTreeCalculator calculator = new MemberTreeCalculator();

    // Pour la sélection et l'affichage d'info
    private Member hoveredMember = null;
    private Member selectedMember = null;


    private final Stack<Member> forwardStack = new Stack<>();

    public MemberTreeView(MemberController controller) {
        this.controller = controller;
        setBackground(darkMode ? new Color(10, 10, 25) : Color.WHITE);
        ToolTipManager.sharedInstance().registerComponent(this);

        MouseAdapter mouseHandler = new MouseAdapter() {
            private long lastClickTime;
            @Override
            public void mouseMoved(MouseEvent e) {
                Member m = findMemberAt(e.getPoint());
                if (m != hoveredMember) {
                    hoveredMember = m;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoveredMember = null;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                Member m = findMemberAt(e.getPoint());
                if (m != null) {
                    if (e.getClickCount() == 2) {
                        if (m.getChildren().isEmpty()) {
                            m.getChildren().addAll(controller.getSubMembers(m));
                        }
                        setRootMember(m, true);
                    } else if (e.getClickCount() == 1) {
                        selectedMember = m;
                        repaint();
                    }
                }
            }
        };
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private Member findMemberAt(Point p) {
        for (Map.Entry<Member, Rectangle> entry : memberBounds.entrySet()) {
            if (entry.getValue().contains(p)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void setLayoutDirection(LayoutDirection layoutDirection) {
        this.layoutDirection = layoutDirection;
        repaint();
    }

    public void setRootMember(Member root, boolean trackHistory) {
        if (trackHistory && this.rootMember != null) {
            navigationStack.push(this.rootMember);
            forwardStack.clear(); // On vide la pile forward si on change la branche (nouvelle navigation)
        }
        this.rootMember = root;
        selectedMember = null;
        repaint();
    }

    public void setRootMember(Member root) {
        setRootMember(root, true);
    }

    public void goBack() {
        if (!navigationStack.isEmpty()) {
            forwardStack.push(this.rootMember);  // Sauvegarde l'état courant dans forward
            setRootMember(navigationStack.pop(), false);
        }
    }

    public void setDarkMode(boolean dark) {
        this.darkMode = dark;
        setBackground(darkMode ? new Color(10, 10, 25) : Color.WHITE);
        repaint();
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (rootMember == null) return;

        memberBounds.clear();
        maxX = maxY = 0;
        Graphics2D g2 = calculator.prepareGraphics((Graphics2D) g);
        int padding = 20;
        int rootWidth = calculator.computeNodeWidth(g2, rootMember, padding);
        Point start = calculator.computeStartPoint(getWidth(), getHeight(), layoutDirection, rootWidth);
        calculator.drawTree(this, g2, rootMember, start.x, start.y, layoutDirection, memberBounds);

        if (selectedMember != null) {
            Rectangle r = memberBounds.get(selectedMember);
            if (r != null) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(r);
            }
        }

        if (hoveredMember != null) {
            Rectangle r = memberBounds.get(hoveredMember);
            if (r != null) {
                g2.setColor(new Color(255, 255, 255, 100));
                g2.fill(r);
            }
        }

        setPreferredSize(new Dimension(maxX + 200, maxY + 200));
        revalidate();
    }

    // Méthode publique pour le calculator
    public void updateMax(int x, int y) {
        maxX = Math.max(maxX, x);
        maxY = Math.max(maxY, y);
    }



    @Override
    public String getToolTipText(MouseEvent e) {
        Member m = findMemberAt(e.getPoint());
        if (m != null) {
            return "<html><b>Nom :</b> " + m.getName() +
                    "<br><b>ID :</b> " + m.getId() +
                    "<br><b>Enfants :</b> " + m.getChildren().size() + "</html>";
        }
        return null;
    }

    public void goForward() {
        if (!forwardStack.isEmpty()) {
            navigationStack.push(this.rootMember);  // Sauvegarde dans back stack
            setRootMember(forwardStack.pop(), false);
        }
    }



}

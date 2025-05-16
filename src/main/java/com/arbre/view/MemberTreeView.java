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
 * Délègue les calculs de layout à MemberTreeCalculator.
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

    public MemberTreeView(MemberController controller) {
        this.controller = controller;
        setBackground(darkMode ? new Color(10, 10, 25) : Color.WHITE);
        addMouseListener(new MouseAdapter() {
            private long lastClickTime;
            @Override
            public void mouseClicked(MouseEvent e) {
                long now = System.currentTimeMillis();
                boolean dbl = now - lastClickTime < 400;
                lastClickTime = now;
                for (Map.Entry<Member, Rectangle> entry : memberBounds.entrySet()) {
                    if (entry.getValue().contains(e.getPoint())) {
                        if (dbl) {
                            Member m = entry.getKey();
                            if (m.getChildren().isEmpty()) {
                                m.getChildren().addAll(controller.getSubMembers(m));
                            }
                            setRootMember(m, true);
                        }
                        break;
                    }
                }
            }
        });
    }

    public void setLayoutDirection(LayoutDirection layoutDirection) {
        this.layoutDirection = layoutDirection;
        repaint();
    }

    public void setRootMember(Member root, boolean trackHistory) {
        if (trackHistory && this.rootMember != null) navigationStack.push(this.rootMember);
        this.rootMember = root;
        repaint();
    }

    public void setRootMember(Member root) {
        setRootMember(root, true);
    }

    public void goBack() {
        if (!navigationStack.isEmpty()) {
            setRootMember(navigationStack.pop(), false);
        }
    }

    public void setDarkMode(boolean dark) {
        this.darkMode = dark;
        setBackground(darkMode ? new Color(10, 10, 25) : Color.WHITE);
        repaint();
    }

    /**
     * Getter pour l'état sombre, utilisé par le calculator.
     */
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
        setPreferredSize(new Dimension(maxX + 200, maxY + 200));
        revalidate();
    }

    // Accesseurs pour maj des extents
    public void updateMax(int x, int y) {
        maxX = Math.max(maxX, x);
        maxY = Math.max(maxY, y);
    }
}


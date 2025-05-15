package com.arbre.view;

import com.arbre.controller.MemberController;
import com.arbre.model.Member;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.CubicCurve2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Stack;

public class MemberTreeView extends JPanel {
    private final MemberController controller;
    private Member rootMember;
    private boolean darkMode = true;

    private final Map<Member, Rectangle> memberBounds = new HashMap<>();

    private int maxX = 0;
    private int maxY = 0;

    private final Stack<Member> navigationStack = new Stack<>();

    public MemberTreeView(MemberController controller) {
        this.controller = controller;
        setBackground(darkMode ? new Color(10, 10, 25) : Color.WHITE);

        addMouseListener(new MouseAdapter() {
            private long lastClickTime = 0;

            @Override
            public void mouseClicked(MouseEvent e) {
                long currentTime = System.currentTimeMillis();
                boolean isDoubleClick = currentTime - lastClickTime < 400;
                lastClickTime = currentTime;

                for (Map.Entry<Member, Rectangle> entry : memberBounds.entrySet()) {
                    Member member = entry.getKey();
                    Rectangle bounds = entry.getValue();
                    if (bounds.contains(e.getPoint())) {
                        if (isDoubleClick) {
                            // Charger enfants si vide
                            if (member.getChildren().isEmpty()) {
                                List<Member> subMembers = controller.getSubMembers(member);
                                member.getChildren().addAll(subMembers);
                            }
                            // Naviguer en mettant ce membre en tête
                            setRootMember(member, true);
                        }
                        break;
                    }
                }
            }
        });

    }

    public void setRootMember(Member rootMember, boolean trackHistory) {
        if (trackHistory && this.rootMember != null) {
            navigationStack.push(this.rootMember); // Sauvegarde le membre courant
        }
        this.rootMember = rootMember;
        repaint();
    }


    public void setRootMember(Member rootMember) {
        setRootMember(rootMember, true);
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (rootMember != null) {
            memberBounds.clear();
            maxX = 0;
            maxY = 0;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            drawMemberTree(g2, rootMember, getWidth() / 2, 40, 200);

            // Mettre à jour la taille préférée dynamiquement
            setPreferredSize(new Dimension(maxX + 200, maxY + 200));
            revalidate(); // Forcer le JScrollPane à mettre à jour les scrollbars
        }
    }

    private int getSubtreeWidth(Member member, int offsetX) {
        if (member.getChildren().isEmpty()) return offsetX;
        int totalWidth = 0;
        for (Member child : member.getChildren()) {
            totalWidth += getSubtreeWidth(child, offsetX);
        }
        return totalWidth;
    }

    private void drawMemberTree(Graphics2D g, Member member, int x, int y, int offsetX) {
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(member.getName());
        int textHeight = fm.getAscent();

        int padding = 20; // marge à gauche et droite (total 20px = 10 de chaque côté)
        int width = textWidth + padding;
        int height = 30;
        int arc = 15;
        int rectX = x - width / 2;
        int rectY = y - height / 2;

        // Suivre la zone maximale atteinte
        maxX = Math.max(maxX, x + width / 2);
        maxY = Math.max(maxY, y + height / 2);

        // Couleur bordure selon mode
        Color borderColor = darkMode ? Color.LIGHT_GRAY : Color.DARK_GRAY;
        g.setColor(borderColor);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(rectX, rectY, width, height, arc, arc);

        // Texte centré horizontalement
        g.setColor(borderColor);
        g.drawString(member.getName(), x - textWidth / 2, y + textHeight / 4);

        // Zone cliquable
        memberBounds.put(member, new Rectangle(rectX, rectY, width, height));

        // Dessiner enfants récursivement
        List<Member> children = member.getChildren();
        if (!children.isEmpty()) {
            int subtreeWidth = getSubtreeWidth(member, offsetX);
            int startX = x - subtreeWidth / 2;

            for (Member child : children) {
                int childSubtreeWidth = getSubtreeWidth(child, offsetX);
                int childX = startX + childSubtreeWidth / 2;
                int childY = y + 100;

                // Lien parent-enfant
                drawConnection(g, x, y + height / 2, childX, childY - height / 2);

                drawMemberTree(g, child, childX, childY, offsetX);
                startX += childSubtreeWidth;
            }
        }
    }


    private void drawConnection(Graphics2D g, int parentX, int parentY, int childX, int childY) {
        Color connectionColor = darkMode ? Color.WHITE : Color.DARK_GRAY;
        g.setColor(connectionColor);
        g.setStroke(new BasicStroke(1.5f));

        CubicCurve2D.Float curve = new CubicCurve2D.Float();
        int ctrl1X = parentX;
        int ctrl1Y = parentY + 40;
        int ctrl2X = childX;
        int ctrl2Y = childY - 40;
        curve.setCurve(parentX, parentY, ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, childX, childY);
        g.draw(curve);
    }


    public void goBack() {
        if (!navigationStack.isEmpty()) {
            Member previous = navigationStack.pop();
            setRootMember(previous, false); // Ne pas empiler à nouveau
        }
    }


    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
        setBackground(darkMode ? new Color(10, 10, 25) : Color.WHITE);
        repaint();
    }
}

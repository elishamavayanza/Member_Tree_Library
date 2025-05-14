package com.arbre.view;

import com.arbre.controller.MemberController;
import com.arbre.model.Member;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.awt.geom.Arc2D;

import java.awt.geom.QuadCurve2D;


public class MemberTreeView extends JPanel {
    private final MemberController controller;
    private Member rootMember;
    private boolean darkMode = true;

    // Map pour enregistrer la position de chaque membre
    private final Map<Member, Rectangle> memberBounds = new HashMap<>();

    public MemberTreeView(MemberController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(1600, 1000));
        setBackground(darkMode ? FuturisticTheme.DARK_BG : Color.WHITE);
        setBackground(new Color(10, 10, 25));


        // Écouteur de clic pour gérer le double-clic
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
                            if (member.getChildren().isEmpty()) {
                                List<Member> subMembers = controller.getSubMembers(member);
                                member.getChildren().addAll(subMembers);
                                repaint(); // Redessiner l’arbre
                            }
                        }
                        break;
                    }
                }
            }
        });
    }

    public void setRootMember(Member rootMember) {
        this.rootMember = rootMember;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (rootMember != null) {
            memberBounds.clear(); // Réinitialiser la map à chaque repaint
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawMemberTree(g2, rootMember, getWidth() / 2, 40, 300);
        }
    }

    private void drawMemberTree(Graphics2D g, Member member, int x, int y, int offsetX) {
        int width = 100;
        int height = 40;

        int rectX = x - width / 2;
        int rectY = y - height / 2;

        // Détecter automatiquement si c’est le mode sombre (basé sur le fond du panel)
        boolean isDarkMode = getBackground().getRed() < 100 && getBackground().getGreen() < 100 && getBackground().getBlue() < 100;

        // Ombre
        g.setColor(new Color(0, 0, 0, 80));
        g.fillRoundRect(rectX + 4, rectY + 4, width, height, 20, 20);

        // Dégradé futuriste
        GradientPaint gradient = new GradientPaint(
                rectX, rectY,
                new Color(0, 204, 255),
                rectX + width, rectY + height,
                new Color(0, 102, 204)
        );
        g.setPaint(gradient);
        g.fillRoundRect(rectX, rectY, width, height, 20, 20);

        // Bordure
        g.setColor(isDarkMode ? Color.WHITE : Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(rectX, rectY, width, height, 20, 20);

        // Texte
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g.setColor(isDarkMode ? Color.WHITE : Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(member.getName());
        g.drawString(member.getName(), x - textWidth / 2, y + 5);

        // Enregistrer zone cliquable
        Rectangle bounds = new Rectangle(rectX, rectY, width, height);
        memberBounds.put(member, bounds);

        // Enfants
        List<Member> children = member.getChildren();
        int totalWidth = (children.size() - 1) * offsetX;

        for (int i = 0; i < children.size(); i++) {
            Member child = children.get(i);
            int childX = x - totalWidth / 2 + i * offsetX;
            int childY = y + 120;

            // Couleur et épaisseur de la ligne
            g.setColor(isDarkMode ? Color.WHITE : Color.BLACK);
            g.setStroke(new BasicStroke(2.2f));

            // Point de départ (bas du parent)
            int startX = x;
            int startY = y + height / 2;

            // Point d’arrivée (haut de l’enfant)
            int endX = childX;
            int endY = childY - height / 2;

            // Ligne verticale initiale
            g.drawLine(startX, y + height / 2, startX, startY + 20);

            // Courbe de liaison
            CubicCurve2D.Float curve = new CubicCurve2D.Float();
            int ctrl1X = startX;
            int ctrl1Y = startY + 60;
            int ctrl2X = endX;
            int ctrl2Y = endY - 60;

            curve.setCurve(startX, startY + 20, ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, endX, endY);
            g.draw(curve);

            drawMemberTree(g, child, childX, childY, offsetX / 2);
        }
    }



    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
        setBackground(darkMode ? FuturisticTheme.DARK_BG : Color.WHITE);
        repaint();
    }
}

package com.arbre.calculator;

import com.arbre.model.Member;
import com.arbre.view.LayoutDirection;
import com.arbre.view.MemberTreeView;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.util.List;
import java.util.Map;

/**
 * Classe responsable de tous les calculs de width/height et du tracé des connexions.
 */
public class MemberTreeCalculator {
    public Graphics2D prepareGraphics(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return g2;
    }

    public int computeNodeWidth(Graphics2D g2, Member m, int padding) {
        FontMetrics fm = g2.getFontMetrics();
        int w = Math.max(fm.stringWidth(m.getName()) + padding, 80);
        return w;
    }

    public Point computeStartPoint(int panelW, int panelH, LayoutDirection dir, int rootWidth) {
        if (dir == LayoutDirection.VERTICAL) {
            return new Point(panelW / 2, 40);
        } else {
            return new Point(40 + rootWidth / 2, panelH / 2);
        }
    }

    public void drawTree(MemberTreeView view, Graphics2D g2,
                         Member node, int x, int y,
                         LayoutDirection dir,
                         Map<Member, Rectangle> boundsMap) {
        int padding = 20;
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(node.getName());
        int nodeW = Math.max(textW + padding, 80);
        int nodeH = 30;
        int rectX = x - nodeW/2;
        int rectY = y - nodeH/2;
        view.updateMax(x + nodeW/2, y + nodeH/2);

        // Dessin du noeud
        g2.setColor(view.isDarkMode() ? Color.LIGHT_GRAY : Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(rectX, rectY, nodeW, nodeH, 15, 15);
        g2.drawString(node.getName(), x - textW/2, y + fm.getAscent()/4);
        boundsMap.put(node, new Rectangle(rectX, rectY, nodeW, nodeH));

        List<Member> kids = node.getChildren();
        if (kids.isEmpty()) return;

        int spacing = 40;
        if (dir == LayoutDirection.VERTICAL) {
            int totalW = computeSubtreeWidth(g2, node, padding);
            int startX = x - totalW/2;
            for (Member c: kids) {
                int childW = computeSubtreeWidth(g2, c, padding);
                int cx = startX + childW/2;
                int cy = y + 100;
                drawConnection(g2, x, y + nodeH/2, cx, cy - nodeH/2, view.isDarkMode());
                drawTree(view, g2, c, cx, cy, dir, boundsMap);
                startX += childW + spacing;
            }
        } else {
            int parentW = nodeW;
            int startY = y - computeSubtreeHeight(g2, node, padding)/2;
            for (Member c: kids) {
                int cw = computeNodeWidth(g2, c, padding);
                int childX = x + parentW/2 + spacing + cw/2;
                int childY = startY + computeSubtreeHeight(g2, c, padding)/2;
                drawConnectionHorizontal(g2, x + parentW/2, y, childX - cw/2, childY);
                drawTree(view, g2, c, childX, childY, dir, boundsMap);
                startY += computeSubtreeHeight(g2, c, padding) + spacing;
            }
        }
    }

    private int computeSubtreeWidth(Graphics2D g2, Member m, int pad) {
        List<Member> kids = m.getChildren();
        int nodeW = computeNodeWidth(g2, m, pad);
        if (kids.isEmpty()) return nodeW;
        int sum = 0;
        for (Member c: kids) sum += computeSubtreeWidth(g2, c, pad) + 40;
        return Math.max(sum - 40, nodeW);
    }

    private int computeSubtreeHeight(Graphics2D g2, Member m, int pad) {
        List<Member> kids = m.getChildren();
        int nodeH = Math.max(g2.getFontMetrics().getHeight() + pad, 30);
        if (kids.isEmpty()) return nodeH;
        int sum = 0;
        for (Member c: kids) sum += computeSubtreeHeight(g2, c, pad) + 40;
        return Math.max(sum - 40, nodeH);
    }

    private void drawConnection(Graphics2D g2, int x1, int y1, int x2, int y2, boolean darkMode) {
        g2.setColor(darkMode ? Color.WHITE : Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(1.5f));
        CubicCurve2D.Float curve = new CubicCurve2D.Float(
                x1, y1, x1, y1+40, x2, y2-40, x2, y2
        );
        g2.draw(curve);
    }

    private void drawConnectionHorizontal(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.setColor(Color.GRAY);
        g2.setStroke(new BasicStroke(1.5f));
        int cx = (x1 + x2)/2;
        CubicCurve2D.Float curve = new CubicCurve2D.Float(
                x1, y1, cx, y1, cx, y2, x2, y2
        );
        g2.draw(curve);
    }
}
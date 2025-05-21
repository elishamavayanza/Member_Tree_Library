package com.arbre.layout;

import com.arbre.model.IMember;
import com.arbre.view.LayoutDirection;

import java.util.*;

/**
 * Pure layout engine: computes node positions and control points without UI frameworks.
 */
public class TreeLayoutCalculator {
    private final TextMeasurer measurer;
    private final int horizontalSpacing;
    private final int verticalSpacing;
    private final int minNodeWidth;
    private final int nodeHeight;
    private final int padX, padY;

    public TreeLayoutCalculator (TextMeasurer measurer, int horizontalSpacing, int verticalSpacing, int minNodeWidth, int nodeHeight, int padX, int padY) {
        this.measurer = measurer;
        this.horizontalSpacing = horizontalSpacing;
        this.verticalSpacing = verticalSpacing;
        this.minNodeWidth = minNodeWidth;
        this.nodeHeight = nodeHeight;
        this.padX = padX;
        this.padY = padY;
    }

    /**
     * Computes layout for the given tree root.
     */
    public TreeLayout computeLayout(IMember root, int panelWidth, int panelHeight, LayoutDirection dir) {
        Map<IMember, NodeBounds> boundsMap = new LinkedHashMap<>();
        int rootW = measurer.measureWidth(root.getName(), minNodeWidth);
        Point start = computeStartPoint(panelWidth, panelHeight, dir, rootW);
        layoutNode(root, start.x, start.y, dir, boundsMap);
        List<Connection> connections = buildConnections(boundsMap, dir);
        return new TreeLayout(new ArrayList<>(boundsMap.values()), connections);
    }


    private void layoutNode(IMember node, int x, int y, LayoutDirection dir, Map<IMember, NodeBounds> boundsMap) {
        int textw = measurer.measureWidth(node.getName(), minNodeWidth);
        int w = textw + 2 * padX;
        int h = nodeHeight + 2 * padY;

        boundsMap.put(node, new NodeBounds(node.getId(), node.getName(), x - w / 2, y - h / 2, w, h));
        List<? extends IMember> children = node.getChildren();
        if (children.isEmpty()) return;

        if (dir == LayoutDirection.VERTICAL) {
            int totalW = computeSubtreeWidth(node);
            int curX = x - totalW / 2;
            for (IMember c : children) {
                int cw = computeSubtreeWidth(c);
                int cx = curX + cw / 2;
                int cy = y + h + verticalSpacing;
                layoutNode(c, cx, cy, dir, boundsMap);
                curX += cw + horizontalSpacing;
            }
        } else {
            // Horizontal layout: ensure spacing between generations accounts for dynamic widths
            int totalH = computeSubtreeHeight(node);
            int curY = y - totalH / 2;
            for (IMember c : children) {
                int subtreeH = computeSubtreeHeight(c);
                int cy = curY + subtreeH / 2;
                int cw = measurer.measureWidth(c.getName(), minNodeWidth);
                // Compute parent right edge
                int parentRight = x + w / 2;
                // Place child so its left edge is at parentRight + horizontalSpacing
                int cx = parentRight + horizontalSpacing + cw / 2;
                layoutNode(c, cx, cy, dir, boundsMap);
                curY += subtreeH + verticalSpacing;
            }
        }

    }

    private int computeSubtreeWidth(IMember node) {
        List<? extends IMember> children = node.getChildren();
        int textW = measurer.measureWidth(node.getName(), minNodeWidth);
        int w = textW + 2 * padX;
        if (children.isEmpty()) return w;
        int sum = 0;
        for (IMember c : children) sum += computeSubtreeWidth(c) + horizontalSpacing;
        return Math.max(sum - horizontalSpacing, w);
    }

    private int computeSubtreeHeight(IMember node) {
        List<? extends IMember> children = node.getChildren();
        int h = nodeHeight + 2 * padY;
        if (children.isEmpty()) return h;
        int sum = 0;
        for (IMember c : children) sum += computeSubtreeHeight(c) + verticalSpacing;
        return Math.max(sum - verticalSpacing, h);
    }

    private Point computeStartPoint(int w, int h, LayoutDirection dir, int rootWidth) {
        if (dir == LayoutDirection.VERTICAL) {
            return new Point(w / 2, verticalSpacing);
        }
        return new Point(horizontalSpacing + rootWidth / 2, h / 2);
    }

    private List<Connection> buildConnections(Map<IMember, NodeBounds> map, LayoutDirection dir) {
        List<Connection> conns = new ArrayList<>();
        for (Map.Entry<IMember, NodeBounds> e : map.entrySet()) {
            for (IMember c : e.getKey().getChildren()) {
                NodeBounds pb = e.getValue();
                NodeBounds cb = map.get(c);
                if (cb != null) conns.add(Connection.of(pb, cb, dir));
            }
        }
        return conns;
    }

    /**
     * Layout result.
     */
    public static class TreeLayout {
        private final List<NodeBounds> nodes;
        private final List<Connection> connections;

        public TreeLayout(List<NodeBounds> nodes, List<Connection> connections) {
            this.nodes = Collections.unmodifiableList(nodes);
            this.connections = Collections.unmodifiableList(connections);
        }

        public List<NodeBounds> getNodes() {
            return nodes;
        }

        public List<Connection> getConnections() {
            return connections;
        }
    }


    /**
     * Node geometry.
     */
    public static class NodeBounds {
        public final String id, name;
        public final int x, y, width, height;

        public NodeBounds(String id, String name, int x, int y, int width, int height) {
            this.id = id;
            this.name = name;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }


    /**
     * Connection control points.
     */
    public static class Connection {
        public final String fromId, toId;
        public final List<Point> controlPoints;

        private Connection(String f, String t, List<Point> pts) {
            this.fromId = f;
            this.toId = t;
            this.controlPoints = Collections.unmodifiableList(pts);
        }

        public static Connection of(NodeBounds p, NodeBounds c, LayoutDirection dir) {
            List<Point> pts = new ArrayList<>();
            int x1 = p.x + p.width / 2, y1 = p.y + p.height;
            int x2 = c.x + c.width / 2, y2 = c.y;
            if (dir == LayoutDirection.VERTICAL) {
                pts.add(new Point(x1, y1));
                pts.add(new Point(x1, y1 + (y2 - y1) / 2));
                pts.add(new Point(x2, y2 - (y2 - y1) / 2));
                pts.add(new Point(x2, y2));
            } else {
                int x11 = p.x + p.width;
                int y11 = p.y + p.height / 2;
                int x22 = c.x;
                int y22 = c.y + c.height / 2;
                pts.add(new Point(x11, y11));
                pts.add(new Point(x11 + (x22 - x11) / 2,y11));
                pts.add(new Point(x11 + (x22 - x11) / 2, y22));
                pts.add(new Point(x22, y22));

            }
            return new Connection(p.id, c.id, pts);
        }
    }


    /**
     * Immutable point.
     */
    public static class Point {
        public final int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }


    /**
     * Text width measurement.
     */
    public interface TextMeasurer {
        int measureWidth(String text, int minWidth);
    }
}





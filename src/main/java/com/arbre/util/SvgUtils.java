package com.arbre.util;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;

public class SvgUtils {
    public static ImageIcon loadSvgIcon(String resourcePath, int width, int height) {
        try {
            SVGUniverse universe = new SVGUniverse();
            InputStream stream = SvgUtils.class.getResourceAsStream(resourcePath);
            URI uri = universe.loadSVG(stream, resourcePath);
            SVGDiagram diagram = universe.getDiagram(uri);

            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            diagram.setIgnoringClipHeuristic(true);
            diagram.render(g2);

            return new ImageIcon(img);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

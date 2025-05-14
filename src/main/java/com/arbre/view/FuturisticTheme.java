package com.arbre.view;

import javax.swing.*;
import java.awt.*;

public class FuturisticTheme {

    // Couleurs pour le mode sombre
    public static final Color DARK_BG = new Color(30, 30, 30); // Fond sombre
    public static final Color DARK_TEXT = new Color(220, 220, 220); // Texte clair pour le mode sombre
    public static final Color DARK_ACCENT = new Color(0, 204, 255); // Accent clair futuriste (par exemple pour les boutons)

    // Couleurs pour le mode clair
    public static final Color LIGHT_BG = Color.WHITE; // Fond clair
    public static final Color LIGHT_TEXT = Color.BLACK; // Texte noir pour le mode clair
    public static final Color LIGHT_ACCENT = new Color(0, 204, 255); // Accent clair futuriste (par exemple pour les boutons)

    // Méthode pour appliquer le thème sombre
    public static void applyDarkTheme(JFrame frame) {
        frame.getContentPane().setBackground(DARK_BG);
        UIManager.put("Button.background", DARK_ACCENT);
        UIManager.put("Button.foreground", DARK_TEXT);
        UIManager.put("Label.foreground", DARK_TEXT);
        UIManager.put("TextField.background", DARK_BG);
        UIManager.put("TextField.foreground", DARK_TEXT);
        UIManager.put("TextField.border", BorderFactory.createLineBorder(DARK_ACCENT));
        UIManager.put("Panel.background", DARK_BG);
        UIManager.put("ScrollPane.background", DARK_BG);
        UIManager.put("Table.background", DARK_BG);
        UIManager.put("Table.foreground", DARK_TEXT);
    }

    // Méthode pour appliquer le thème clair
    public static void applyLightTheme(JFrame frame) {
        frame.getContentPane().setBackground(LIGHT_BG);
        UIManager.put("Button.background", LIGHT_ACCENT);
        UIManager.put("Button.foreground", LIGHT_TEXT);
        UIManager.put("Label.foreground", LIGHT_TEXT);
        UIManager.put("TextField.background", LIGHT_BG);
        UIManager.put("TextField.foreground", LIGHT_TEXT);
        UIManager.put("TextField.border", BorderFactory.createLineBorder(LIGHT_ACCENT));
        UIManager.put("Panel.background", LIGHT_BG);
        UIManager.put("ScrollPane.background", LIGHT_BG);
        UIManager.put("Table.background", LIGHT_BG);
        UIManager.put("Table.foreground", LIGHT_TEXT);
    }
}

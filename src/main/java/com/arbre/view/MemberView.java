package com.arbre.view;

import com.arbre.controller.MemberController;
import com.arbre.util.SvgUtils;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class MemberView extends JFrame {
    private final MemberController controller;
    private final MemberTreeView treeView;
    private boolean darkMode = true;

    private final ImageIcon iconOn;
    private final ImageIcon iconOff;
    private final JToggleButton toggleThemeButton;

    public MemberView(MemberController controller) {
        this.controller = controller;
        this.treeView = new MemberTreeView(controller);

        // Charger les icônes SVG
        iconOn = SvgUtils.loadSvgIcon("/icons/toggle-on.svg", 24, 24);
        iconOff = SvgUtils.loadSvgIcon("/icons/toggle-off.svg", 24, 24);

        setTitle("Arbre de Membres - Futuriste");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(treeView);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Bouton de bascule de thème
        toggleThemeButton = new JToggleButton(iconOn);
        toggleThemeButton.setPreferredSize(new Dimension(50, 35));
        toggleThemeButton.setSelected(true);
        toggleThemeButton.setToolTipText("Changer le thème");
        toggleThemeButton.addActionListener(e -> {
            darkMode = !darkMode;
            updateTheme();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(toggleThemeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Appliquer le thème initial
        updateTheme();
    }

    public MemberTreeView getTreeView() {
        return treeView;
    }

    private void updateTheme() {
        try {
            if (darkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                toggleThemeButton.setIcon(iconOn);
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
                toggleThemeButton.setIcon(iconOff);
            }

            SwingUtilities.updateComponentTreeUI(this);
            treeView.setDarkMode(darkMode);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
}

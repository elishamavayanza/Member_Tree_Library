package com.arbre.view;

import com.arbre.controller.MemberController;
import com.arbre.model.Member;
import com.arbre.util.SvgUtils;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MemberView extends JFrame {
    private final MemberController controller;
    private final MemberTreeView treeView;
    private boolean darkMode = true;

    private final ImageIcon iconOnDark;
    private final ImageIcon iconOnLight;
    private final ImageIcon iconOffDark;
    private final ImageIcon iconOffLight;
    private final JToggleButton toggleThemeButton;

    // Stocker la racine initiale pour pouvoir y revenir
    private final Member initialRoot;

    public MemberView(MemberController controller) {
        this.controller = controller;
        this.treeView = new MemberTreeView(controller);

        // Récupérer la racine initiale depuis le controller
        this.initialRoot = controller.getRootMember();
        treeView.setRootMember(initialRoot); // Afficher la racine initiale

        // Charger les icônes SVG
        iconOnDark = SvgUtils.loadSvgIcon("/icons/toggle-on-dark.svg", 24, 24);
        iconOnLight = SvgUtils.loadSvgIcon("/icons/toggle-on-light.svg", 24, 24);
        iconOffDark = SvgUtils.loadSvgIcon("/icons/toggle-off-dark.svg", 24, 24);
        iconOffLight = SvgUtils.loadSvgIcon("/icons/toggle-off-light.svg", 24, 24);


        setTitle("Arbre de Membres - Futuriste");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(treeView);
        scrollPane.setPreferredSize(new Dimension(1000, 700));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        toggleThemeButton = new JToggleButton(iconOnDark);
        toggleThemeButton.setPreferredSize(new Dimension(50, 35));
        toggleThemeButton.setSelected(true);
        toggleThemeButton.setToolTipText("Changer le thème");
        toggleThemeButton.addActionListener(e -> {
            darkMode = !darkMode;
            updateTheme();
        });
        toggleThemeButton.addChangeListener(e -> updateToggleIcon());


        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(toggleThemeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Gestion du raccourci clavier : Backspace déclenche goBack()
        treeView.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    treeView.goBack();
                    treeView.repaint();
                    treeView.requestFocusInWindow();
                }
            }
        });

        treeView.setFocusable(true);
        treeView.requestFocusInWindow();

        updateTheme();
    }

    public MemberTreeView getTreeView() {
        return treeView;
    }

    private void updateTheme() {
        try {
            if (darkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }

            // Mettre à jour l’interface
            SwingUtilities.updateComponentTreeUI(this);
            treeView.setDarkMode(darkMode);

            // Re-appliquer l’icône du bouton
            updateToggleIcon();

            // 🔥 Redonner le focus au treeView pour garder les raccourcis clavier fonctionnels
            SwingUtilities.invokeLater(() -> treeView.requestFocusInWindow());

        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }


    private void updateToggleIcon() {
        if (toggleThemeButton.isSelected()) {
            // Montrer l'icône du thème opposé à celui en cours
            toggleThemeButton.setIcon(darkMode ? iconOnLight : iconOnDark);
        } else {
            toggleThemeButton.setIcon(darkMode ? iconOffLight : iconOffDark);
        }
    }



}

package com.arbre.view;

import com.arbre.controller.MemberController;
import com.arbre.model.Member;
import com.arbre.util.SvgUtils;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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
    private final JToggleButton toggleLayoutButton;

    // Stocker la racine initiale pour pouvoir y revenir
    private final Member initialRoot;

    /**
     * Constructeur de la fenêtre principale de l'application.
     * Initialise l'arbre des membres, les icônes, le thème, les listeners, et la barre de défilement.
     *
     * @param controller Le contrôleur chargé de la logique métier.
     */
    public MemberView(MemberController controller) {
        this.controller = controller;
        this.treeView = new MemberTreeView(controller);

        // Récupérer la racine initiale depuis le controller
        this.initialRoot = controller.getRootMember();
        treeView.setRootMember(initialRoot); // Afficher la racine initiale

        // Charger les icônes SVG
        iconOnDark  = SvgUtils.loadSvgIcon("/icons/toggle-on-dark.svg",  24, 24);
        iconOnLight = SvgUtils.loadSvgIcon("/icons/toggle-on-light.svg", 24, 24);
        iconOffDark = SvgUtils.loadSvgIcon("/icons/toggle-off-dark.svg", 24, 24);
        iconOffLight= SvgUtils.loadSvgIcon("/icons/toggle-off-light.svg",24, 24);

        setTitle("Arbre de Membres - Futuriste");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(treeView);
        scrollPane.setPreferredSize(new Dimension(1000, 700));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Bouton pour changer le thème
        toggleThemeButton = new JToggleButton(iconOnDark);
        toggleThemeButton.setPreferredSize(new Dimension(50, 35));
        toggleThemeButton.setSelected(true);
        toggleThemeButton.setToolTipText("Changer le thème");
        toggleThemeButton.addActionListener(e -> {
            darkMode = !darkMode;
            updateTheme();
        });
        toggleThemeButton.addChangeListener(e -> updateToggleIcon());

        // Bouton pour changer la disposition de l'arbre (verticale/horizontale)
        toggleLayoutButton = new JToggleButton("Vertical");
        toggleLayoutButton.setPreferredSize(new Dimension(120, 35));
        toggleLayoutButton.setToolTipText("Changer la disposition (Vertical / Horizontal)");
        toggleLayoutButton.setSelected(false); // false = vertical par défaut
        toggleLayoutButton.addActionListener(e -> {
            if (toggleLayoutButton.isSelected()) {
                toggleLayoutButton.setText("Horizontal");
                treeView.setLayoutDirection(LayoutDirection.HORIZONTAL);
            } else {
                toggleLayoutButton.setText("Vertical");
                treeView.setLayoutDirection(LayoutDirection.VERTICAL);
            }
            treeView.repaint();
            SwingUtilities.invokeLater(() -> treeView.requestFocusInWindow());
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(toggleLayoutButton);
        bottomPanel.add(toggleThemeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Key Binding pour Backspace sur toute la fenêtre ---
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "goBack");
        am.put("goBack", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                treeView.goBack();
                treeView.repaint();
            }
        });

        // Donner le focus initial au treeView
        SwingUtilities.invokeLater(() -> treeView.requestFocusInWindow());

        updateTheme();
    }

    /**
     * Retourne la vue graphique de l’arbre des membres.
     *
     * @return l'objet MemberTreeView affichant l’arborescence.
     */
    public MemberTreeView getTreeView() {
        return treeView;
    }

    /**
     * Met à jour le thème (sombre ou clair) de l’interface :
     * - Change le Look & Feel (FlatLaf)
     * - Met à jour tous les composants Swing
     * - Applique le thème au MemberTreeView
     * - Met à jour l’icône du bouton de bascule
     */
    private void updateTheme() {
        try {
            if (darkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            SwingUtilities.updateComponentTreeUI(this);
            treeView.setDarkMode(darkMode);
            updateToggleIcon();
            SwingUtilities.invokeLater(() -> treeView.requestFocusInWindow());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Met à jour l’icône du bouton de bascule de thème selon l’état actuel.
     */
    private void updateToggleIcon() {
        if (toggleThemeButton.isSelected()) {
            toggleThemeButton.setIcon(darkMode ? iconOnLight : iconOnDark);
        } else {
            toggleThemeButton.setIcon(darkMode ? iconOffLight : iconOffDark);
        }
    }
}

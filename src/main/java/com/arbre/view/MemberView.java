package com.arbre.view;

import com.arbre.controller.MemberController;
import com.arbre.layout.TreeLayoutCalculator;
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

    // Icônes thème
    private final ImageIcon iconOnDark;
    private final ImageIcon iconOnLight;
    private final ImageIcon iconOffDark;
    private final ImageIcon iconOffLight;

    // Icônes layout
    private final ImageIcon iconVertDark;
    private final ImageIcon iconVertLight;
    private final ImageIcon iconHorizDark;
    private final ImageIcon iconHorizLight;

    private final JToggleButton toggleThemeButton;
    private final JToggleButton toggleLayoutButton;

    private final Member initialRoot;

    private final JScrollPane scrollPane;

    public MemberView(MemberController controller) {
        this.controller = controller;
        this.treeView = new MemberTreeView(controller);
        this.initialRoot = controller.getRootMember();
        this.scrollPane = new JScrollPane(treeView);
        treeView.setRootMember(initialRoot);

        // Chargement des icônes thème
        iconOnDark   = SvgUtils.loadSvgIcon("/icons/toggle-on-dark.svg", 24, 24);
        iconOnLight  = SvgUtils.loadSvgIcon("/icons/toggle-on-light.svg", 24, 24);
        iconOffDark  = SvgUtils.loadSvgIcon("/icons/toggle-off-dark.svg", 24, 24);
        iconOffLight = SvgUtils.loadSvgIcon("/icons/toggle-off-light.svg", 24, 24);

        // Chargement des icônes layout
        iconVertDark   = SvgUtils.loadSvgIcon("/icons/swap-vertical-circle-dark.svg", 24, 24);
        iconVertLight  = SvgUtils.loadSvgIcon("/icons/swap-vertical-circle-light.svg", 24, 24);
        iconHorizDark  = SvgUtils.loadSvgIcon("/icons/swap-horizontal-circle-dark.svg", 24, 24);
        iconHorizLight = SvgUtils.loadSvgIcon("/icons/swap-horizontal-circle-light.svg", 24, 24);

        setTitle("Arbre de Membres - Futuriste");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        scrollPane.setPreferredSize(new Dimension(1000, 700));
        add(scrollPane, BorderLayout.CENTER);

        // Bouton thème
        toggleThemeButton = new JToggleButton(iconOnDark);
        toggleThemeButton.setPreferredSize(new Dimension(50, 35));
        toggleThemeButton.setSelected(true);
        toggleThemeButton.setToolTipText("Changer le thème");
        toggleThemeButton.addActionListener(e -> {
            darkMode = !darkMode;
            updateTheme();
        });
        toggleThemeButton.addChangeListener(e -> updateToggleIcon());

        // Bouton layout
        toggleLayoutButton = new JToggleButton();
        toggleLayoutButton.setPreferredSize(new Dimension(50, 35));
        toggleLayoutButton.setToolTipText("Basculer Vertical / Horizontal");
        toggleLayoutButton.setSelected(false); // false = vertical
        toggleLayoutButton.addActionListener(e -> {
            treeView.setLayoutDirection(
                    toggleLayoutButton.isSelected() ? LayoutDirection.HORIZONTAL : LayoutDirection.VERTICAL
            );
            updateLayoutIcon();
            treeView.revalidate();
            scrollPane.revalidate();
            scrollPane.repaint();
            SwingUtilities.invokeLater(() -> treeView.requestFocusInWindow());
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(toggleLayoutButton);
        bottomPanel.add(toggleThemeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Key Binding Backspace
        // Key Binding Backspace
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "goBack");
        am.put("goBack", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                treeView.goBack();
                treeView.repaint();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.SHIFT_DOWN_MASK), "goForward");  // Shift+R
        am.put("goForward", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                treeView.goForward();
                treeView.repaint();
            }
        });


        SwingUtilities.invokeLater(() -> treeView.requestFocusInWindow());
        updateTheme();
        updateLayoutIcon();
    }



    /**
     * Permet à l'utilisateur (ex : Main) de récupérer directement la vue de l'arbre.
     */
    public MemberTreeView getTreeView() {
        return treeView;
    }

    private void updateLayoutIcon() {
        boolean horizontal = toggleLayoutButton.isSelected();
        toggleLayoutButton.setIcon(
                horizontal
                        ? (darkMode ? iconHorizLight : iconHorizDark)
                        : (darkMode ? iconVertLight  : iconVertDark)
        );
    }

    private void updateTheme() {
        try {
            UIManager.setLookAndFeel(darkMode ? new FlatDarkLaf() : new FlatLightLaf());
            SwingUtilities.updateComponentTreeUI(this);
            treeView.setDarkMode(darkMode);
            // Mise à jour des icônes thème et layout immédiatement
            updateToggleIcon();
            updateLayoutIcon();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private void updateToggleIcon() {
        if (toggleThemeButton.isSelected()) {
            toggleThemeButton.setIcon(darkMode ? iconOnLight : iconOnDark);
        } else {
            toggleThemeButton.setIcon(darkMode ? iconOffLight : iconOffDark);
        }
    }
}

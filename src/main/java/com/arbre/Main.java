package com.arbre;

import com.arbre.controller.MemberController;
import com.arbre.model.Member;
import com.arbre.view.MemberView;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // S'assurer que l'interface graphique s'exécute sur le thread Swing
        SwingUtilities.invokeLater(() -> {
            // Créer le contrôleur
            MemberController controller = new MemberController();

            // Créer un membre racine (par exemple un administrateur principal)
            Member rootMember = new Member("Admin Principal");

            // Définir le membre racine dans le contrôleur (s'il faut l'afficher dans la vue)
            controller.createMember(rootMember.getName(), null);  // L'ajouter au contrôleur
            controller.createMember("Membre 1", rootMember);
            controller.createMember("Membre 2", rootMember);
            controller.createMember("Membre 3", rootMember);

            // Créer la vue avec le contrôleur
            MemberView view = new MemberView(controller);

            // Définir la vue avec le membre racine
            view.getTreeView().setRootMember(rootMember);

            // Afficher la fenêtre de l'application
            view.setVisible(true);
        });
    }
}

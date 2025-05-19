package com.arbre;

import com.arbre.controller.MemberController;
import com.arbre.model.Member;
import com.arbre.view.MemberView;

import javax.swing.*;
import java.security.SecureRandom;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        // S'assurer que l'interface graphique s'exécute sur le thread Swing
        SwingUtilities.invokeLater(() -> {
            // Créer le contrôleur
            MemberController controller = new MemberController();

            // Créer un membre racine (par exemple un administrateur principal)
            Member rootMember = new Member("ROOTeeeeeeeeeeeeeeeeeeeeeeeeeeee");

            // Définir le membre racine dans le contrôleur (s'il faut l'afficher dans la vue)
            controller.createMember(rootMember.getName(), null);  // L'ajouter au contrôleur
            controller.createMember("Aoooooooooooooo", rootMember);
            controller.createMember("Bllllllllllllllllll", rootMember);
            controller.createMember("Clllllllllllllllll", rootMember);
            controller.createMember("Dllllllllllllllllll", rootMember);
            controller.createMember("Elllllllllllllllll", rootMember);
            controller.createMember("Fllllllllllllllllll", rootMember);
            controller.createMember("Glllllllllllllllll", rootMember);
            controller.createMember("H000000000000000000", rootMember);


            for (int i = 0; i <7; i++) {
                Member member = rootMember.getChildren().get(i);

                int count  = Double.valueOf(Math.round(Math.random() * 10)).intValue();

                for (int j = 0; j < count; j++) {
                    controller.createMember(member+"TRUC OK MACHINE OF ELISHAMA VAYANZA"+j, member);
                }
            }



            // Créer la vue avec le contrôleur
            MemberView view = new MemberView(controller);

            // Définir la vue avec le membre racine
            view.getTreeView().setRootMember(rootMember);

            // Afficher la fenêtre de l'application
            view.setVisible(true);
        });
    }
}

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
            Member rootMember = new Member("ROOT");

            // Définir le membre racine dans le contrôleur (s'il faut l'afficher dans la vue)
            controller.createMember(rootMember.getName(), null);  // L'ajouter au contrôleur
            controller.createMember("A", rootMember);
            controller.createMember("B", rootMember);
            controller.createMember("C", rootMember);
            controller.createMember("D", rootMember);
            controller.createMember("E", rootMember);
            controller.createMember("F", rootMember);
            controller.createMember("G", rootMember);
            controller.createMember("H", rootMember);


            for (int i = 0; i <7; i++) {
                Member member = rootMember.getChildren().get(i);

                int count  = Double.valueOf(Math.round(Math.random() * 10)).intValue();

                for (int j = 0; j < count; j++) {
                    controller.createMember(member+" TRUC OK MACHINE OF ELISHAMA VAYANZA"+j, member);
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

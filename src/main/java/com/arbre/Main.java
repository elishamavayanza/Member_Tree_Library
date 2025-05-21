package com.arbre;

import com.arbre.controller.MemberController;
import com.arbre.model.Member;
import com.arbre.view.MemberView;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        // S'assurer que l'interface graphique s'exécute sur le thread Swing
        SwingUtilities.invokeLater(() -> {
            // Créer le contrôleur
            MemberController controller = new MemberController();

            // Créer un membre racine (par exemple un administrateur principal)
            Member rootMember = new Member("ROOT");

            // Définir le membre racine dans le contrôleur
            controller.createMember(rootMember.getName(), null);
            controller.createMember("A", rootMember);
            controller.createMember("B", rootMember);
            controller.createMember("C", rootMember);
            controller.createMember("D", rootMember);
            controller.createMember("E", rootMember);
            controller.createMember("F", rootMember);
            controller.createMember("G", rootMember);
            controller.createMember("H", rootMember);

            // Ajouter des enfants aléatoires
            for (Member member : rootMember.getChildren()) {
                int count = (int) Math.round(Math.random() * 10);
                for (int j = 0; j < count; j++) {
                    controller.createMember(member.getName() + "-TRUC-" + j, member);
                }
            }

            // Création de la fenêtre principale
            JFrame frame = new JFrame("Arbre de Membres");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);
            frame.setLayout(new BorderLayout());

            // On y met notre panel réutilisable
            MemberView panel = new MemberView(controller);
            // On peut redéfinir la racine si besoin
            panel.getTreeView().setRootMember(rootMember);

            frame.add(panel, BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

package com.arbre.view;

import com.arbre.controller.MemberController;
import com.arbre.model.Member;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.CubicCurve2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Stack;

/**
 * Constructeur de la vue.
 * Initialise le contrôleur et le fond selon le mode sombre.
 * Ajoute un écouteur de souris qui détecte les double-clics sur un membre :
 * Si les enfants du membre sont vides, il les charge depuis le contrôleur.
 * Puis il met à jour la racine de l’arbre pour naviguer vers ce membre.
 */
public class MemberTreeView extends JPanel {
    private final MemberController controller;
    private Member rootMember;
    private boolean darkMode = true;

    private final Map<Member, Rectangle> memberBounds = new HashMap<>();

    private int maxX = 0;
    private int maxY = 0;

    private final Stack<Member> navigationStack = new Stack<>();

    /**
     * Change le membre racine de l’arbre affiché.
     * Si trackHistory est vrai, ajoute le membre courant à une pile de navigation pour permettre un retour arrière.
     * Puis, définit la nouvelle racine et redessine la vue.
     * @param controller
     */
    public MemberTreeView(MemberController controller) {
        this.controller = controller;
        setBackground(darkMode ? new Color(10, 10, 25) : Color.WHITE);

        addMouseListener(new MouseAdapter() {
            private long lastClickTime = 0;

            @Override
            public void mouseClicked(MouseEvent e) {
                long currentTime = System.currentTimeMillis();
                boolean isDoubleClick = currentTime - lastClickTime < 400;
                lastClickTime = currentTime;

                for (Map.Entry<Member, Rectangle> entry : memberBounds.entrySet()) {
                    Member member = entry.getKey();
                    Rectangle bounds = entry.getValue();
                    if (bounds.contains(e.getPoint())) {
                        if (isDoubleClick) {
                            // Charger enfants si vide
                            if (member.getChildren().isEmpty()) {
                                List<Member> subMembers = controller.getSubMembers(member);
                                member.getChildren().addAll(subMembers);
                            }
                            // Naviguer en mettant ce membre en tête
                            setRootMember(member, true);
                        }
                        break;
                    }
                }
            }
        });

    }

    /**
     * Surcharge pratique qui appelle la précédente avec trackHistory = true
     * @param rootMember
     * @param trackHistory
     */
    public void setRootMember(Member rootMember, boolean trackHistory) {
        if (trackHistory && this.rootMember != null) {
            navigationStack.push(this.rootMember); // Sauvegarde le membre courant
        }
        this.rootMember = rootMember;
        repaint();
    }


    public void setRootMember(Member rootMember) {
        setRootMember(rootMember, true);
    }

    /**
     * Méthode principale de dessin de l’arbre.
     * Efface le dessin précédent et, si la racine existe :
     * Vide les coordonnées enregistrées,
     * Active le rendu antialiasé,
     * Appelle la méthode récursive drawMemberTree pour dessiner tout l’arbre,
     * Met à jour dynamiquement la taille préférée du panneau (setPreferredSize),
     * Appelle revalidate() pour forcer la mise à jour des barres de défilement si le panneau est dans un JScrollPane.
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (rootMember != null) {
            memberBounds.clear();
            maxX = 0;
            maxY = 0;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            drawMemberTree(g2, rootMember, getWidth() / 2, 40, 200);

            // Mettre à jour la taille préférée dynamiquement
            setPreferredSize(new Dimension(maxX + 200, maxY + 200));
            revalidate(); // Forcer le JScrollPane à mettre à jour les scrollbars
        }
    }

    /**
     * Calcule récursivement la largeur nécessaire pour dessiner tous les descendants du member.
     * Retourne un total basé sur la largeur cumulée des sous-arbres des enfants.
     * @param member
     * @param offsetX
     * @return
     */
    private int getSubtreeWidth(Member member, int offsetX) {
        if (member.getChildren().isEmpty()) return offsetX;
        int totalWidth = 0;
        for (Member child : member.getChildren()) {
            totalWidth += getSubtreeWidth(child, offsetX);
        }
        return totalWidth;
    }

    /**
     * Méthode récursive pour dessiner un membre et tous ses enfants.
     * Calcule les dimensions du bloc de texte, dessine un rectangle avec bords arrondis.
     * Centre le nom du membre à l’intérieur.
     * Enregistre la zone cliquable pour détecter les clics.
     * Si le membre a des enfants :
     * Calcule la largeur de sous-arbre et leur position horizontale.
     * Appelle drawConnection pour dessiner une ligne entre parent et enfant.
     * Récursivement dessine chaque enfant.
     * @param g
     * @param member
     * @param x
     * @param y
     * @param offsetX
     */
    private void drawMemberTree(Graphics2D g, Member member, int x, int y, int offsetX) {
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(member.getName());
        int textHeight = fm.getAscent();

        int padding = 20; // marge à gauche et droite (total 20px = 10 de chaque côté)
        int width = textWidth + padding;
        int height = 30;
        int arc = 15;
        int rectX = x - width / 2;
        int rectY = y - height / 2;

        // Suivre la zone maximale atteinte
        maxX = Math.max(maxX, x + width / 2);
        maxY = Math.max(maxY, y + height / 2);

        // Couleur bordure selon mode
        Color borderColor = darkMode ? Color.LIGHT_GRAY : Color.DARK_GRAY;
        g.setColor(borderColor);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(rectX, rectY, width, height, arc, arc);

        // Texte centré horizontalement
        g.setColor(borderColor);
        g.drawString(member.getName(), x - textWidth / 2, y + textHeight / 4);

        // Zone cliquable
        memberBounds.put(member, new Rectangle(rectX, rectY, width, height));

        // Dessiner enfants récursivement
        List<Member> children = member.getChildren();
        if (!children.isEmpty()) {
            int subtreeWidth = getSubtreeWidth(member, offsetX);
            int startX = x - subtreeWidth / 2;

            for (Member child : children) {
                int childSubtreeWidth = getSubtreeWidth(child, offsetX);
                int childX = startX + childSubtreeWidth / 2;
                int childY = y + 100;

                // Lien parent-enfant
                drawConnection(g, x, y + height / 2, childX, childY - height / 2);

                drawMemberTree(g, child, childX, childY, offsetX);
                startX += childSubtreeWidth;
            }
        }
    }

    /**
     * Dessine une courbe de Bézier entre un parent et un enfant.
     * Crée un lien visuel doux entre les nœuds en courbe, avec des couleurs adaptables au thème sombre.
     * @param g
     * @param parentX
     * @param parentY
     * @param childX
     * @param childY
     */
    private void drawConnection(Graphics2D g, int parentX, int parentY, int childX, int childY) {
        Color connectionColor = darkMode ? Color.WHITE : Color.DARK_GRAY;
        g.setColor(connectionColor);
        g.setStroke(new BasicStroke(1.5f));

        CubicCurve2D.Float curve = new CubicCurve2D.Float();
        int ctrl1X = parentX;
        int ctrl1Y = parentY + 40;
        int ctrl2X = childX;
        int ctrl2Y = childY - 40;
        curve.setCurve(parentX, parentY, ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, childX, childY);
        g.draw(curve);
    }

    /**
     * Revenir à l’état précédent de navigation.
     *
     * Retire le dernier membre de la pile et le définit comme racine, sans re-remplir la pile.
     */
    public void goBack() {
        if (!navigationStack.isEmpty()) {
            Member previous = navigationStack.pop();
            setRootMember(previous, false); // Ne pas empiler à nouveau
        }
    }

    /**
     * Active ou désactive le mode sombre.
     *
     * Change la couleur de fond et redessine la vue.
     * @param darkMode
     */
    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
        setBackground(darkMode ? new Color(10, 10, 25) : Color.WHITE);
        repaint();
    }
}

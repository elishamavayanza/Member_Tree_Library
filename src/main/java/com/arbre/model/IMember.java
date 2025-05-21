// src/main/java/com/arbre/model/IMember.java
package com.arbre.model;

import java.util.List;

public interface IMember {
    /** Identifiant unique (UUID ou autre) */
    String getId();
    void setId(String id);

    /** Nom ou étiquette du membre */
    String getName();
    void setName(String name);

    /** Position */
    String getPosition();
    void setPosition(String position);

    /** Enfants directs dans la hiérarchie */
    List<? extends IMember> getChildren();

    /** Ajoute un enfant à ce membre */
    boolean addChild(IMember child);
}

// src/main/java/com/arbre/model/Member.java
package com.arbre.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Member implements IMember {
    private String id;
    private String name;
    private final List<Member> children;

    /** Génère un nouveau Member avec un UUID aléatoire */
    public Member(String name) {
        this(UUID.randomUUID().toString(), name);
    }

    /** Construit un Member à partir d’un id existant */
    public Member(String id, String name) {
        this.id = id;
        this.name = name;
        this.children = new ArrayList<>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<Member> getChildren() {
        return children;
    }

    @Override
    public boolean addChild(IMember child) {
        // On peut vérifier que child est bien un Member sinon adapter ici
        if (child instanceof Member) {
            return children.add((Member) child);
        }
        throw new IllegalArgumentException("Child must be a Member");
    }

    @Override
    public String toString() {
        return name;
    }
}

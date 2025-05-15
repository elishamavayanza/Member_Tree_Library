package com.arbre.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Member {
    private String id; // UUID
    private String name;
    private List<Member> children;

    public Member(String name) {
        this.id = UUID.randomUUID().toString(); // Générer un UUID par défaut
        this.name = name;
        this.children = new ArrayList<>();
    }

    // Nouveau constructeur pour charger avec un id donné (ex : depuis la BDD)
    public Member(String id, String name) {
        this.id = id;
        this.name = name;
        this.children = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    // Setter ajouté pour id (utile pour mapResultSetToMember)
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public List<Member> getChildren() {
        return children;
    }

    public boolean addChild(Member child) {
        children.add(child);
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}

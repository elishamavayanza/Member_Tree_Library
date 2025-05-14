package com.arbre.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Member {
    private String id; // Identifiant unique
    private String name;
    private List<Member> children;

    public Member(String name) {
        this.id = UUID.randomUUID().toString(); // Générer un identifiant unique pour chaque membre
        this.name = name;
        this.children = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Member> getChildren() {
        return children;
    }

    public boolean addChild(Member child) {
        if (children.size() < 3) {
            children.add(child);
            return true;
        }
        return false; // Ne peut pas avoir plus de 3 enfants
    }
}

package com.arbre.controller;

import com.arbre.model.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberController {
    private final List<Member> members;
    private final Map<String, List<Member>> memberMap; // Mappe l'ID d'un membre à ses enfants

    // La racine de l'arbre (membre initial)
    private final Member initialRoot;

    public MemberController() {
        members = new ArrayList<>();
        memberMap = new HashMap<>();

        // Initialiser la racine ici
        initialRoot = new Member("Racine");

        // Optionnel : Ajouter la racine dans la liste des membres (si tu veux)
        members.add(initialRoot);
    }

    // Méthode pour récupérer la racine de l'arbre
    public Member getRootMember() {
        return initialRoot;
    }

    // Créer un membre et l'ajouter à son parent (ou à la liste principale)
    public boolean createMember(String name, Member parent) {
        Member newMember = new Member(name);
        if (parent != null) {
            if (parent.addChild(newMember)) {
                memberMap.computeIfAbsent(parent.getId(), k -> new ArrayList<>()).add(newMember); // Ajouter l'enfant au map du parent
                return true;
            }
        } else {
            members.add(newMember); // Ajouter à la liste des membres si aucun parent
        }
        return false;
    }

    // Récupérer tous les membres
    public List<Member> getAllMembers() {
        return members;
    }

    // Trouver un membre par son nom
    public Member findMemberByName(String name) {
        for (Member member : members) {
            if (member.getName().equals(name)) {
                return member;
            }
        }
        return null;
    }

    // Récupérer les sous-membres d'un parent
    public List<Member> getSubMembers(Member parent) {
        return memberMap.getOrDefault(parent.getId(), new ArrayList<>());
    }
}

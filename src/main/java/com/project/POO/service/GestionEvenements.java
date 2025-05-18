package com.project.POO.service;


import com.project.POO.model.Evenement;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Implémentation du pattern Singleton pour la gestion centrale des événements
 * Cette classe est responsable de maintenir une référence de tous les événements du système
 */
@Service
public class GestionEvenements {

    private static GestionEvenements instance;
    private final Map<String, Evenement> evenements;

    /**
     * Constructeur privé pour empêcher l'instanciation directe (Pattern Singleton)
     */
    private GestionEvenements() {
        this.evenements = new HashMap<>();
    }

    /**
     * Méthode pour obtenir l'instance unique de GestionEvenements
     * @return L'instance de GestionEvenements
     */
    public static synchronized GestionEvenements getInstance() {
        if (instance == null) {
            instance = new GestionEvenements();
        }
        return instance;
    }

    /**
     * Ajoute un événement à la gestion
     * @param evenement L'événement à ajouter
     */
    public void ajouterEvenement(Evenement evenement) {
        evenements.put(evenement.getId(), evenement);
    }

    /**
     * Supprime un événement de la gestion
     * @param id L'identifiant de l'événement à supprimer
     * @return true si l'événement a été supprimé, false sinon
     */
    public boolean supprimerEvenement(String id) {
        return evenements.remove(id) != null;
    }

    /**
     * Recherche un événement par son identifiant
     * @param id L'identifiant de l'événement à rechercher
     * @return L'événement trouvé ou null
     */
    public Optional<Evenement> rechercherEvenement(String id) {
        return Optional.ofNullable(evenements.get(id));
    }

    /**
     * Renvoie la liste des événements
     * @return Une copie non modifiable de la liste des événements
     */
    public Map<String, Evenement> getEvenements() {
        return Collections.unmodifiableMap(evenements);
    }
}
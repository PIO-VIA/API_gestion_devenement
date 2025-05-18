package com.project.POO.model;

import com.project.POO.observer.ParticipantObserver;
import com.project.POO.observer.EvenementObservable;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Evenement implements EvenementObservable {

    @Id
    private String id;

    private String nom;
    private LocalDateTime date;
    private String lieu;
    private int capaciteMax;
    private boolean annule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisateur_id")
    private Organisateur organisateur;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "evenement_participant",
            joinColumns = @JoinColumn(name = "evenement_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private List<Participant> participants = new ArrayList<>();

    @Transient
    private List<ParticipantObserver> observers = new ArrayList<>();

    protected Evenement() {
        this.id = UUID.randomUUID().toString();
    }

    public Evenement(String nom, LocalDateTime date, String lieu, int capaciteMax) {
        this();
        this.nom = nom;
        this.date = date;
        this.lieu = lieu;
        this.capaciteMax = capaciteMax;
        this.annule = false;
    }

    public boolean ajouterParticipant(Participant participant) throws Exception {
        if (participants.size() >= capaciteMax) {
            throw new Exception("Capacité maximale atteinte pour cet événement.");
        }

        if (participants.contains(participant)) {
            return false;
        }

        participants.add(participant);
        // Si le participant est aussi un observer, l'ajouter à la liste des observers
        if (participant instanceof ParticipantObserver) {
            this.subscribe((ParticipantObserver) participant);
        }
        return true;
    }

    public boolean supprimerParticipant(Participant participant) {
        boolean removed = participants.remove(participant);
        if (removed && participant instanceof ParticipantObserver) {
            this.unsubscribe((ParticipantObserver) participant);
        }
        return removed;
    }

    public void annuler() {
        this.annule = true;
        notifyObservers("L'événement " + nom + " a été annulé.");
    }

    public abstract String afficherDetails();

    // Implémentation du pattern Observer
    @Override
    public void subscribe(ParticipantObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void unsubscribe(ParticipantObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (ParticipantObserver observer : observers) {
            observer.update(message);
        }
    }
}
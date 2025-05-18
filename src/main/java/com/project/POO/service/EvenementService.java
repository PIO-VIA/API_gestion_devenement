package com.project.POO.service;


import com.project.POO.exception.CapaciteMaxAtteinteException;
import com.project.POO.exception.EvenementDejaExistantException;
import com.project.POO.exception.EvenementNotFoundException;
import com.project.POO.model.Evenement;
import com.project.POO.model.Participant;
import com.project.POO.repository.EvenementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvenementService {

    private final EvenementRepository evenementRepository;
    private final NotificationService notificationService;
    private final GestionEvenements gestionEvenements;

    @Transactional
    public Evenement creerEvenement(Evenement evenement) throws EvenementDejaExistantException {
        // Vérifier si un événement avec le même nom et date existe déjà
        if (evenementRepository.existsByNomAndDate(evenement.getNom(), evenement.getDate())) {
            throw new EvenementDejaExistantException("Un événement avec le même nom et date existe déjà");
        }

        // Enregistrer dans le repository
        Evenement savedEvenement = evenementRepository.save(evenement);

        // Ajouter à la gestion des événements (Singleton)
        gestionEvenements.ajouterEvenement(savedEvenement);

        return savedEvenement;
    }

    @Transactional(readOnly = true)
    public List<Evenement> getAllEvenements() {
        return evenementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Evenement getEvenementById(String id) throws EvenementNotFoundException {
        return evenementRepository.findById(id)
                .orElseThrow(() -> new EvenementNotFoundException("Événement non trouvé avec l'id: " + id));
    }

    @Transactional
    public Evenement updateEvenement(String id, Evenement evenementDetails) throws EvenementNotFoundException {
        Evenement evenement = getEvenementById(id);

        // Mettre à jour les propriétés
        evenement.setNom(evenementDetails.getNom());
        evenement.setDate(evenementDetails.getDate());
        evenement.setLieu(evenementDetails.getLieu());
        evenement.setCapaciteMax(evenementDetails.getCapaciteMax());

        // Notifier les participants du changement
        String message = "L'événement " + evenement.getNom() + " a été mis à jour.";
        evenement.notifyObservers(message);

        // Notification asynchrone complémentaire par email
        envoyerNotificationsAsync(evenement.getParticipants(), message);

        return evenementRepository.save(evenement);
    }

    @Transactional
    public void deleteEvenement(String id) throws EvenementNotFoundException {
        Evenement evenement = getEvenementById(id);

        // Notifier les participants de la suppression
        String message = "L'événement " + evenement.getNom() + " a été supprimé.";
        evenement.notifyObservers(message);

        // Supprimer de la gestion des événements (Singleton)
        gestionEvenements.supprimerEvenement(id);

        // Supprimer du repository
        evenementRepository.delete(evenement);

        // Notification asynchrone complémentaire par email
        envoyerNotificationsAsync(evenement.getParticipants(), message);
    }

    @Transactional
    public void annulerEvenement(String id) throws EvenementNotFoundException {
        Evenement evenement = getEvenementById(id);
        evenement.annuler();

        // Notification asynchrone complémentaire par email
        String message = "L'événement " + evenement.getNom() + " a été annulé.";
        envoyerNotificationsAsync(evenement.getParticipants(), message);

        evenementRepository.save(evenement);
    }

    @Transactional
    public void ajouterParticipant(String evenementId, Participant participant)
            throws EvenementNotFoundException, CapaciteMaxAtteinteException {
        Evenement evenement = getEvenementById(evenementId);

        try {
            if (evenement.ajouterParticipant(participant)) {
                evenementRepository.save(evenement);

                // Notification asynchrone
                String message = "Vous êtes inscrit à l'événement: " + evenement.getNom();
                CompletableFuture.runAsync(() -> notificationService.envoyerNotification(participant.getEmail(), message));
            }
        } catch (Exception e) {
            if (e.getMessage().contains("Capacité maximale")) {
                throw new CapaciteMaxAtteinteException("La capacité maximale de l'événement est atteinte");
            }
            throw new RuntimeException("Erreur lors de l'ajout du participant", e);
        }
    }

    @Transactional
    public void supprimerParticipant(String evenementId, String participantId)
            throws EvenementNotFoundException {
        Evenement evenement = getEvenementById(evenementId);

        evenement.getParticipants().stream()
                .filter(p -> p.getId().equals(participantId))
                .findFirst()
                .ifPresent(participant -> {
                    evenement.supprimerParticipant(participant);
                    evenementRepository.save(evenement);

                    // Notification asynchrone
                    String message = "Vous avez été désinscrit de l'événement: " + evenement.getNom();
                    CompletableFuture.runAsync(() ->
                            notificationService.envoyerNotification(participant.getEmail(), message));
                });
    }

    // Méthode utilitaire pour envoyer des notifications de manière asynchrone
    private void envoyerNotificationsAsync(List<Participant> participants, String message) {
        CompletableFuture.runAsync(() -> {
            participants.forEach(participant ->
                    notificationService.envoyerNotification(participant.getEmail(), message));
        });
    }

    // Méthodes utilisant les Streams et lambdas (Java 8+)
    public List<Evenement> rechercherParLieu(String lieu) {
        return evenementRepository.findAll().stream()
                .filter(e -> e.getLieu().toLowerCase().contains(lieu.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Evenement> evenementsDisponibles() {
        return evenementRepository.findAll().stream()
                .filter(e -> !e.isAnnule() && e.getParticipants().size() < e.getCapaciteMax())
                .collect(Collectors.toList());
    }
}
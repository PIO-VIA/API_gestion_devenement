// ParticipantService.java
package com.project.POO.service;

import com.project.POO.exception.ParticipantNotFoundException;
import com.project.POO.model.Participant;
import com.project.POO.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final NotificationService notificationService;

    @Transactional
    public Participant creerParticipant(Participant participant) {
        return participantRepository.save(participant);
    }

    @Transactional(readOnly = true)
    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Participant getParticipantById(String id) throws ParticipantNotFoundException {
        return participantRepository.findById(id)
                .orElseThrow(() -> new ParticipantNotFoundException("Participant non trouvé avec l'id: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Participant> getParticipantByEmail(String email) {
        return participantRepository.findByEmail(email);
    }

    @Transactional
    public Participant updateParticipant(String id, Participant participantDetails) throws ParticipantNotFoundException {
        Participant participant = getParticipantById(id);

        participant.setNom(participantDetails.getNom());
        participant.setEmail(participantDetails.getEmail());

        return participantRepository.save(participant);
    }

    @Transactional
    public void deleteParticipant(String id) throws ParticipantNotFoundException {
        Participant participant = getParticipantById(id);
        participantRepository.delete(participant);
    }

    @Transactional(readOnly = true)
    public List<Participant> rechercherParNom(String nom) {
        return participantRepository.findByNomContainingIgnoreCase(nom);
    }
}
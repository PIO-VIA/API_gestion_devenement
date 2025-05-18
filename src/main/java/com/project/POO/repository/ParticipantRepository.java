// ParticipantRepository.java (corrigé le nom de fichier)
package com.project.POO.repository;

import com.project.POO.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, String> {

    Optional<Participant> findByEmail(String email);

    List<Participant> findByNomContainingIgnoreCase(String nom);

    boolean existsByEmail(String email);
}
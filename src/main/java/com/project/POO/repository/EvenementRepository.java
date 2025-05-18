// EvenementRepository.java
package com.project.POO.repository;

import com.project.POO.model.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EvenementRepository extends JpaRepository<Evenement, String> {

    boolean existsByNomAndDate(String nom, LocalDateTime date);

    List<Evenement> findByLieuContainingIgnoreCase(String lieu);

    List<Evenement> findByAnnuleFalseAndCapaciteMaxGreaterThan(int nombreParticipants);

    List<Evenement> findByDateAfter(LocalDateTime date);
}
package com.project.POO.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class EvenementDto {
    private String id;
    private String nom;
    private LocalDateTime date;
    private String lieu;
    private int capaciteMax;
    private boolean annule;

    // Champs spécifiques par type d'événement
    private String type; // "CONFERENCE" ou "CONCERT"

    // Pour Conference
    private String theme;
    private List<String> intervenants;

    // Pour Concert
    private String artiste;
    private String genreMusical;

    // Statistiques
    private int nombreParticipants;
}
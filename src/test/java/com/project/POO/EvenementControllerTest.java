package com.project.POO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.POO.controller.EvenementController;
import com.project.POO.dto.EvenementDto;
import com.project.POO.exception.CapaciteMaxAtteinteException;
import com.project.POO.exception.EvenementNotFoundException;
import com.project.POO.model.Concert;
import com.project.POO.model.Conference;
import com.project.POO.model.Evenement;
import com.project.POO.model.Participant;
import com.project.POO.service.EvenementService;
import com.project.POO.service.ParticipantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EvenementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EvenementService evenementService;

    @Mock
    private ParticipantService participantService;

    @InjectMocks
    private EvenementController evenementController;

    private ObjectMapper objectMapper;
    private Conference conference;
    private Concert concert;
    private Participant participant;
    private EvenementDto conferenceDto;
    private EvenementDto concertDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(evenementController)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Pour gérer LocalDateTime

        // Créer des objets de test
        conference = new Conference("TechConf", LocalDateTime.now().plusDays(10), "Salle A", 100, "Nouvelles technologies");
        conference.setId("conf-1");

        concert = new Concert("LiveMusic", LocalDateTime.now().plusDays(20), "Stade", 1000, "Artiste Test", "Pop");
        concert.setId("concert-1");

        participant = new Participant("John Doe", "john@example.com");
        participant.setId("part-1");

        // Préparer les DTOs
        conferenceDto = new EvenementDto();
        conferenceDto.setId(conference.getId());
        conferenceDto.setNom(conference.getNom());
        conferenceDto.setDate(conference.getDate());
        conferenceDto.setLieu(conference.getLieu());
        conferenceDto.setCapaciteMax(conference.getCapaciteMax());
        conferenceDto.setAnnule(conference.isAnnule());
        conferenceDto.setType("CONFERENCE");
        conferenceDto.setTheme(((Conference) conference).getTheme());

        concertDto = new EvenementDto();
        concertDto.setId(concert.getId());
        concertDto.setNom(concert.getNom());
        concertDto.setDate(concert.getDate());
        concertDto.setLieu(concert.getLieu());
        concertDto.setCapaciteMax(concert.getCapaciteMax());
        concertDto.setAnnule(concert.isAnnule());
        concertDto.setType("CONCERT");
        concertDto.setArtiste(((Concert) concert).getArtiste());
        concertDto.setGenreMusical(((Concert) concert).getGenreMusical());
    }

    @Test
    @DisplayName("GET /api/evenements - Récupérer tous les événements")
    void getAllEvenements_ReturnsEventsList() throws Exception {
        // Arrange
        List<Evenement> evenements = Arrays.asList(conference, concert);
        when(evenementService.getAllEvenements()).thenReturn(evenements);

        // Act & Assert
        mockMvc.perform(get("/api/evenements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(conference.getId())))
                .andExpect(jsonPath("$[1].id", is(concert.getId())));
    }

    @Test
    @DisplayName("GET /api/evenements/{id} - Récupérer un événement par ID")
    void getEvenementById_ReturnsEvent_WhenFound() throws Exception {
        // Arrange
        when(evenementService.getEvenementById(conference.getId())).thenReturn(conference);

        // Act & Assert
        mockMvc.perform(get("/api/evenements/{id}", conference.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(conference.getId())))
                .andExpect(jsonPath("$.nom", is(conference.getNom())));
    }

    @Test
    @DisplayName("GET /api/evenements/{id} - Retourne 404 quand l'événement n'est pas trouvé")
    void getEvenementById_Returns404_WhenNotFound() throws Exception {
        // Arrange
        when(evenementService.getEvenementById(anyString())).thenThrow(new EvenementNotFoundException("Événement non trouvé"));

        // Act & Assert
        mockMvc.perform(get("/api/evenements/{id}", "non-existing-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/evenements/conferences - Créer une conférence")
    void createConference_ReturnsCreatedConference() throws Exception {
        // Arrange
        when(evenementService.creerEvenement(any(Conference.class))).thenReturn(conference);

        // Act & Assert
        mockMvc.perform(post("/api/evenements/conferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conferenceDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(conference.getId())))
                .andExpect(jsonPath("$.type", is("CONFERENCE")));
    }

    @Test
    @DisplayName("POST /api/evenements/concerts - Créer un concert")
    void createConcert_ReturnsCreatedConcert() throws Exception {
        // Arrange
        when(evenementService.creerEvenement(any(Concert.class))).thenReturn(concert);

        // Act & Assert
        mockMvc.perform(post("/api/evenements/concerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(concertDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(concert.getId())))
                .andExpect(jsonPath("$.type", is("CONCERT")));
    }

    @Test
    @DisplayName("PUT /api/evenements/{id} - Mettre à jour un événement")
    void updateEvenement_ReturnsUpdatedEvent() throws Exception {
        // Arrange
        when(evenementService.updateEvenement(eq(conference.getId()), any(Evenement.class))).thenReturn(conference);

        // Act & Assert
        mockMvc.perform(put("/api/evenements/{id}", conference.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conferenceDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(conference.getId())));
    }

    @Test
    @DisplayName("DELETE /api/evenements/{id} - Supprimer un événement")
    void deleteEvenement_ReturnsNoContent() throws Exception {
        // Arrange
        doNothing().when(evenementService).deleteEvenement(anyString());

        // Act & Assert
        mockMvc.perform(delete("/api/evenements/{id}", conference.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PUT /api/evenements/{id}/annuler - Annuler un événement")
    void annulerEvenement_ReturnsOk() throws Exception {
        // Arrange
        doNothing().when(evenementService).annulerEvenement(anyString());

        // Act & Assert
        mockMvc.perform(put("/api/evenements/{id}/annuler", conference.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/evenements/{evenementId}/participants/{participantId} - Inscrire un participant")
    void inscrireParticipant_ReturnsOk() throws Exception {
        // Arrange
        when(participantService.getParticipantById(participant.getId())).thenReturn(participant);
        doNothing().when(evenementService).ajouterParticipant(anyString(), any(Participant.class));

        // Act & Assert
        mockMvc.perform(post("/api/evenements/{evenementId}/participants/{participantId}",
                        conference.getId(), participant.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/evenements/{evenementId}/participants/{participantId} - Retourne 400 quand capacité max atteinte")
    void inscrireParticipant_Returns400_WhenCapacityReached() throws Exception {
        // Arrange
        when(participantService.getParticipantById(participant.getId())).thenReturn(participant);
        doThrow(new CapaciteMaxAtteinteException("Capacité maximale atteinte"))
                .when(evenementService).ajouterParticipant(anyString(), any(Participant.class));

        // Act & Assert
        mockMvc.perform(post("/api/evenements/{evenementId}/participants/{participantId}",
                        conference.getId(), participant.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/evenements/{evenementId}/participants/{participantId} - Désinscrire un participant")
    void desinscrireParticipant_ReturnsOk() throws Exception {
        // Arrange
        doNothing().when(evenementService).supprimerParticipant(anyString(), anyString());

        // Act & Assert
        mockMvc.perform(delete("/api/evenements/{evenementId}/participants/{participantId}",
                        conference.getId(), participant.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/evenements/recherche - Rechercher des événements par lieu")
    void rechercherParLieu_ReturnsMatchingEvents() throws Exception {
        // Arrange
        List<Evenement> matchingEvents = Arrays.asList(conference);
        when(evenementService.rechercherParLieu(anyString())).thenReturn(matchingEvents);

        // Act & Assert
        mockMvc.perform(get("/api/evenements/recherche")
                        .param("lieu", "Salle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(conference.getId())));
    }

    @Test
    @DisplayName("GET /api/evenements/disponibles - Lister les événements disponibles")
    void evenementsDisponibles_ReturnsAvailableEvents() throws Exception {
        // Arrange
        List<Evenement> availableEvents = Arrays.asList(conference, concert);
        when(evenementService.evenementsDisponibles()).thenReturn(availableEvents);

        // Act & Assert
        mockMvc.perform(get("/api/evenements/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
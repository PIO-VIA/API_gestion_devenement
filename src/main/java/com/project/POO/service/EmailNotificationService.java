package com.project.POO.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Implémentation du service de notification par email
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService implements NotificationService {

    private final JavaMailSender mailSender;

    /**
     * Envoie une notification par email
     * Cette méthode est exécutée de manière asynchrone
     *
     * @param destinataire L'adresse email du destinataire
     * @param message Le contenu du message à envoyer
     */
    @Override
    @Async
    public void envoyerNotification(String destinataire, String message) {
        log.info("Envoi d'une notification à {}: {}", destinataire, message);

        try {
            // En environnement de production, on enverrait un véritable email
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(destinataire);
            mailMessage.setSubject("Notification - Système de Gestion d'Événements");
            mailMessage.setText(message);

            // Désactivé pour le développement pour éviter d'envoyer de vrais emails
            // mailSender.send(mailMessage);

            // Simuler un délai pour l'envoi asynchrone
            Thread.sleep(500);

            log.info("Notification envoyée avec succès à {}", destinataire);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification à {}: {}", destinataire, e.getMessage());
        }
    }

    /**
     * Version alternative utilisant CompletableFuture pour une meilleure gestion des résultats
     */
    public CompletableFuture<Boolean> envoyerNotificationAsync(String destinataire, String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                envoyerNotification(destinataire, message);
                return true;
            } catch (Exception e) {
                log.error("Erreur asynchrone: {}", e.getMessage());
                return false;
            }
        });
    }
}
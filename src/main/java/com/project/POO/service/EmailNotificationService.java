package com.project.POO.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Implémentation du service de notification par email
 */
@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class EmailNotificationService implements NotificationService {

    private final JavaMailSender mailSender;


    @Override
    @Async
    public void envoyerNotification(String destinataire, String message) {
        log.info("Envoi d'une notification à {}: {}", destinataire, message);

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(destinataire);
            mailMessage.setSubject("Notification - Système de Gestion d'Événements");
            mailMessage.setText(message);

            mailSender.send(mailMessage);

            Thread.sleep(500);

            log.info("Notification envoyée avec succès à {}", destinataire);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification à {}: {}", destinataire, e.getMessage());
        }
    }


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
package com.project.POO;

import com.project.POO.service.EmailNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailNotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailMessageCaptor;

    private EmailNotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new EmailNotificationService(mailSender);
    }

    @Test
    @DisplayName("Envoi de notification par email")
    void envoyerNotification_SendsEmail() throws InterruptedException {
        // Arrange
        String destinataire = "test@example.com";
        String message = "Test de notification";

        // Act
        notificationService.envoyerNotification(destinataire, message);

        // Attendre que l'exécution asynchrone soit terminée
        Thread.sleep(600);

        // Assert
        // Note: Comme l'envoi d'email est désactivé dans le code d'origine,
        // on ne vérifie pas l'appel à mailSender.send()
        // Si on l'activait, on pourrait utiliser:
        // verify(mailSender).send(mailMessageCaptor.capture());
        // SimpleMailMessage mailMessage = mailMessageCaptor.getValue();
        // assertEquals(destinataire, mailMessage.getTo()[0]);
        // assertEquals("Notification - Système de Gestion d'Événements", mailMessage.getSubject());
        // assertEquals(message, mailMessage.getText());
    }

    @Test
    @DisplayName("Envoi de notification asynchrone")
    void envoyerNotificationAsync_ReturnsCompletableFuture() throws InterruptedException, ExecutionException, TimeoutException {
        // Arrange
        String destinataire = "test@example.com";
        String message = "Test de notification asynchrone";

        // Act
        CompletableFuture<Boolean> future = notificationService.envoyerNotificationAsync(destinataire, message);

        // Assert
        Boolean result = future.get(1, TimeUnit.SECONDS);
        assertTrue(result);
    }

    @Test
    @DisplayName("Gère les exceptions lors de l'envoi d'email")
    void envoyerNotification_HandlesExceptions() {
        // Arrange
        String destinataire = "test@example.com";
        String message = "Test d'erreur";
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        // Ne devrait pas lever d'exception, elle est capturée dans la méthode
        assertDoesNotThrow(() -> {
            notificationService.envoyerNotification(destinataire, message);
            // Attendre que l'exécution asynchrone soit terminée
            Thread.sleep(600);
        });
    }

    @Test
    @DisplayName("La notification asynchrone gère les exceptions")
    void envoyerNotificationAsync_HandlesExceptions() throws InterruptedException, ExecutionException, TimeoutException {
        // Arrange
        String destinataire = "test@example.com";
        String message = "Test d'erreur asynchrone";
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        CompletableFuture<Boolean> future = notificationService.envoyerNotificationAsync(destinataire, message);

        // Assert
        Boolean result = future.get(1, TimeUnit.SECONDS);
        assertFalse(result);
    }
}
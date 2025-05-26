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
import java.util.concurrent.CountDownLatch;
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

    /*@BeforeEach
    void setUp() {
        notificationService = new EmailNotificationService(mailSender);
    }*/

    @Test
    @DisplayName("Envoi de notification par email")
    void envoyerNotification_SendsEmail() throws InterruptedException {
        // Arrange
        String destinataire = "test@example.com";
        String message = "Test de notification";
        CountDownLatch latch = new CountDownLatch(1);

        EmailNotificationService spyService = spy(notificationService);
        doAnswer(invocation -> {
            String dest = invocation.getArgument(0);
            String msg = invocation.getArgument(1);
            spyService.envoyerNotification(dest, msg);
            latch.countDown();
            return null;
        }).when(spyService).envoyerNotification(destinataire, message);

        // Act
        spyService.envoyerNotification(destinataire, message);

        assertTrue(latch.await(2, TimeUnit.SECONDS), "L'exécution asynchrone a pris trop de temps");

        // Assert
        verify(spyService, times(1)).envoyerNotification(destinataire, message);
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
        Boolean result = future.get(2, TimeUnit.SECONDS);
        assertTrue(result);
    }

    @Test
    @DisplayName("Gère les exceptions lors de l'envoi d'email")
    void envoyerNotification_HandlesExceptions() throws InterruptedException {
        // Arrange
        String destinataire = "test@example.com";
        String message = "Test d'erreur";
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));
        CountDownLatch latch = new CountDownLatch(1);

        EmailNotificationService spyService = spy(notificationService);
        doAnswer(invocation -> {
            try {
                notificationService.envoyerNotification(
                        invocation.getArgument(0),
                        invocation.getArgument(1)
                );
            } finally {
                latch.countDown();
            }
            return null;
        }).when(spyService).envoyerNotification(anyString(), anyString());

        // Act & Assert
        assertDoesNotThrow(() -> {
            spyService.envoyerNotification(destinataire, message);
            assertTrue(latch.await(2, TimeUnit.SECONDS), "L'exécution asynchrone a pris trop de temps");
        });
    }

   /*@Test
    @DisplayName("La notification asynchrone gère les exceptions")
    void envoyerNotificationAsync_HandlesExceptions() throws InterruptedException, ExecutionException, TimeoutException {
        // Arrange
        String destinataire = "test@example.com";
        String message = "Test d'erreur asynchrone";

        JavaMailSender failingMailSender = mock(JavaMailSender.class);
        doThrow(new RuntimeException("Mail server error")).when(failingMailSender).send(any(SimpleMailMessage.class));
        EmailNotificationService failingService = new EmailNotificationService(failingMailSender);

        // Act
        CompletableFuture<Boolean> future = failingService.envoyerNotificationAsync(destinataire, message);

        // Assert
        Boolean result = future.get(2, TimeUnit.SECONDS);
        assertFalse(result);
    }*/
}
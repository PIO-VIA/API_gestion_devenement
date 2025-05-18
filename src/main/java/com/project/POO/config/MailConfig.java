package com.project.POO.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuration pour l'envoi d'emails
 * Fournit un JavaMailSender par défaut pour le développement
 */
@Configuration
public class MailConfig {

    /**
     * Configuration d'un JavaMailSender "dummy" pour le développement
     * Ce bean sera utilisé uniquement si aucun autre bean JavaMailSender n'est défini
     */
    @Bean
    @ConditionalOnMissingBean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Configuration minimale (ne fonctionnera pas réellement pour envoyer des emails)
        mailSender.setHost("localhost");
        mailSender.setPort(25);

        // Définit un comportement "dummy" pour éviter les erreurs
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.debug", "false");

        return mailSender;
    }
}

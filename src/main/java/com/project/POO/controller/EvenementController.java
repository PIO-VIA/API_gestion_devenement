package com.project.POO.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class EvenementController {
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

}

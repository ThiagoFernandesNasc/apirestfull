package com.coingecko.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }
    
    @GetMapping("/favicon.ico")
    public void favicon(HttpServletResponse response) {
        // Retorna 204 (No Content) para evitar erro de favicon não encontrado
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }
}

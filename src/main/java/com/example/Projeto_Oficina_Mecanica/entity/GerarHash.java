package com.example.Projeto_Oficina_Mecanica.entity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GerarHash {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String senha = "Fel268108*";

        String hash = encoder.encode(senha);

        System.out.println("HASH GERADO:");
        System.out.println(hash);
    }
}
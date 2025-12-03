package com.foodly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class FoodlyApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodlyApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void applicationReady() {
        System.out.println("\n===========================================");
        System.out.println("✅ API está rodando!");
        System.out.println("🌐 Acesse: http://localhost:8080");
        System.out.println("===========================================\n");
    }
}

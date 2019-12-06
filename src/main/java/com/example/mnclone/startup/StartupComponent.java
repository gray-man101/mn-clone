package com.example.mnclone.startup;

import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("development")
public class StartupComponent {
    @EventListener
    public void handleContextStart(ContextRefreshedEvent cse) {
        System.out.println("Handling context started event.");
    }
}

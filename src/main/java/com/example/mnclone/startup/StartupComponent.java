package com.example.mnclone.startup;

import com.example.mnclone.entity.Ln;
import com.example.mnclone.repository.LnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("development")
public class StartupComponent {

    @Autowired
    private LnRepository lnRepository;

    @EventListener
    public void handleContextStart(ContextRefreshedEvent cse) {
        for (int i = 0; i < 20; i++) {
            Ln ln = new Ln();
            ln.setDbName(i + "Jon");
            ln.setAmount(BigDecimal.valueOf(i));
            ln.setStatus("new");
            lnRepository.save(ln);
        }
    }
}

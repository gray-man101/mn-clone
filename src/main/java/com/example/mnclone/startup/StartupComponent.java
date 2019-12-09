package com.example.mnclone.startup;

import com.example.mnclone.entity.Ln;
import com.example.mnclone.entity.LnStatus;
import com.example.mnclone.entity.User;
import com.example.mnclone.repository.LnRepository;
import com.example.mnclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Component
@Profile("development")
public class StartupComponent {

    @Autowired
    private LnRepository lnRepository;
    @Autowired
    private UserRepository userRepository;

    @EventListener
    public void handleContextStart(ContextRefreshedEvent cse) {
        User user = new User();
        user.setEmail("john@gmail.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBalance(BigDecimal.valueOf(1700));
        user.setRegistered(true);
        userRepository.save(user);

        for (int i = 0; i < 2; i++) {
            Ln ln = new Ln();
            ln.setDbName("Jon");
            ln.setAmount(BigDecimal.valueOf(i*500 + 1000));
            ln.setStatus(LnStatus.NEW);
            ln.setCreated(ZonedDateTime.now());
            lnRepository.save(ln);
        }
    }
}

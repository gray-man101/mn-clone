package com.example.mnclone.startup;

import com.example.mnclone.entity.Ivst;
import com.example.mnclone.entity.Ln;
import com.example.mnclone.entity.LnStatus;
import com.example.mnclone.entity.User;
import com.example.mnclone.repository.IvstRepository;
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
    @Autowired
    private IvstRepository ivstRepository;

    @EventListener
    public void handleContextStart(ContextRefreshedEvent cse) {
        User user = new User();
        user.setEmail("john@gmail.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBalance(BigDecimal.valueOf(1700));
        user.setRegistered(true);
        userRepository.save(user);

        Ln ln1 = createLn("John", BigDecimal.valueOf(1000));
        Ln ln2 = createLn("Steve", BigDecimal.valueOf(1500));
        ln2.setStatus(LnStatus.IN_PROGRESS);
        lnRepository.save(ln2);

        Ivst ivst = new Ivst();
        ivst.setLn(ln2);
        ivst.setUser(user);
        ivstRepository.save(ivst);
    }

    private Ln createLn(String name, BigDecimal amount) {
        Ln ln = new Ln();
        ln.setDbName(name);
        ln.setAmount(amount);
        ln.setStatus(LnStatus.NEW);
        ln.setCreated(ZonedDateTime.now());
        return lnRepository.save(ln);
    }
}

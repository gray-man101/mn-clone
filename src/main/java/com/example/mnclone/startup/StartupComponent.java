package com.example.mnclone.startup;

import com.example.mnclone.entity.*;
import com.example.mnclone.repository.IvstRepository;
import com.example.mnclone.repository.LnRepository;
import com.example.mnclone.repository.PmRepository;
import com.example.mnclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired
    private PmRepository pmRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener
    public void handleContextStart(ContextRefreshedEvent cre) {
        User user = createCustomer("customer@gmail.com");

        Ln ln1 = createLn("John", BigDecimal.valueOf(1000));
        Ln ln2 = createLn("Steve", BigDecimal.valueOf(1500));
        Ln ln3 = createLn("Pete", BigDecimal.valueOf(2000));
        ln1.setStatus(LnStatus.IN_PROGRESS);
        lnRepository.save(ln1);
        ln2.setStatus(LnStatus.IN_PROGRESS);
        lnRepository.save(ln2);

        createIvst(ln1, user);
        createIvst(ln2, user);

        createPm(BigDecimal.valueOf(100), ln2);
        createPm(BigDecimal.valueOf(110), ln2);
    }

    private User createCustomer(String email) {
        User customer = new User();
        customer.setEmail(email);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setBalance(BigDecimal.valueOf(1700));
        customer.setRegistered(true);
        customer.setPasswordHash(passwordEncoder.encode("SuPeRsEcUrEpWs0987654321"));
        customer.setRegistrationToken(passwordEncoder.encode("123" + email));
        return userRepository.save(customer);
    }

    private Ln createLn(String name, BigDecimal amount) {
        Ln ln = new Ln();
        ln.setDbName(name);
        ln.setAmount(amount);
        ln.setStatus(LnStatus.NEW);
        ln.setCreated(ZonedDateTime.now());
        return lnRepository.save(ln);
    }

    private Pm createPm(BigDecimal amount, Ln ln) {
        Pm pm = new Pm();
        pm.setLn(ln);
        pm.setAmount(amount);
        pm.setCreated(ZonedDateTime.now());
        return pmRepository.save(pm);
    }

    private Ivst createIvst(Ln ln, User ivstr) {
        Ivst ivst = new Ivst();
        ivst.setLn(ln);
        ivst.setIvstr(ivstr);
        return ivstRepository.save(ivst);
    }
}

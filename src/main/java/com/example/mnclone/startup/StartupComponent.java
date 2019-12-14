package com.example.mnclone.startup;

import com.example.mnclone.entity.*;
import com.example.mnclone.repository.InvestmentRepository;
import com.example.mnclone.repository.LoanRepository;
import com.example.mnclone.repository.PaymentRepository;
import com.example.mnclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.ZonedDateTime;

@Component
@Profile("development")
public class StartupComponent {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InvestmentRepository investmentRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener
    public void handleContextStart(ContextRefreshedEvent cre) {
        User user = createCustomer("cs@cs.lv");

        Loan loan1 = createLoan("John", BigDecimal.valueOf(1000));
        Loan loan2 = createLoan("Steve", BigDecimal.valueOf(1500));
        loan2.setStatus(LoanStatus.IN_PROGRESS);
        Loan loan3 = createLoan("Peter", BigDecimal.valueOf(2000));
        loanRepository.save(loan2);

//        createInvestment(loan1, user);
//        createInvestment(loan2, user);

        createPayment(BigDecimal.valueOf(100), loan2);
        createPayment(BigDecimal.valueOf(110), loan2);
    }

    private User createCustomer(String email) {
        User customer = new User();
        customer.setEmail(email);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setBalance(BigDecimal.valueOf(1700));
        customer.setRegistered(true);
        customer.setEncodedPassword(passwordEncoder.encode("SuPeRsEcUrEpWs0987654321"));
        customer.setRegistrationToken(passwordEncoder.encode("123" + email));
        return userRepository.save(customer);
    }

    private Loan createLoan(String name, BigDecimal amount) {
        Loan loan = new Loan();
        loan.setDebtorName(name);
        loan.setAmount(amount);
        loan.setAmountToReturn(amount.multiply(BigDecimal.valueOf(2)));
        loan.setInvestorInterest(BigDecimal.valueOf(11));
        loan.setStatus(LoanStatus.NEW);
        loan.setCreated(ZonedDateTime.now());
        return loanRepository.save(loan);
    }

    private Payment createPayment(BigDecimal amount, Loan loan) {
        Payment payment = new Payment();
        payment.setLoan(loan);
        payment.setAmount(amount);
        payment.setCreated(ZonedDateTime.now());
        return paymentRepository.save(payment);
    }

    private Investment createInvestment(Loan loan, User investor) {
        Investment investment = new Investment();
        investment.setLoan(loan);
        investment.setInvestor(investor);
        investment.setAmountToReceive(loan.getAmount()
                .multiply(loan.getInvestorInterest(), new MathContext(2, RoundingMode.DOWN)));
        return investmentRepository.save(investment);
    }
}

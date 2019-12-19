package com.swaperclone.common

import com.swaperclone.common.entity.Investment
import com.swaperclone.common.entity.Loan
import com.swaperclone.common.entity.Payment
import com.swaperclone.common.entity.User
import com.swaperclone.common.repository.InvestmentRepository
import com.swaperclone.common.repository.LoanRepository
import com.swaperclone.common.repository.PaymentRepository
import com.swaperclone.common.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Specification

import javax.persistence.EntityManager
import java.time.ZonedDateTime

//@SpringBootTest
@DataJpaTest
class RepositorySpec extends Specification {

    @Autowired
    InvestmentRepository investmentRepository
    @Autowired
    LoanRepository loanRepository
    @Autowired
    UserRepository userRepository
    @Autowired
    PaymentRepository paymentRepository
    @Autowired
    EntityManager em

    protected Loan prepareLoan(Map params) {
        return loanRepository.save(new Loan(debtorName: params.debtorName, amount: params.amount, investorInterest: params.investorInterest,
                amountToReturn: params.amountToReturn, status: params.status, created: params.created ?: ZonedDateTime.now()))
    }

    protected User prepareUser(Map params) {
        return userRepository.save(new User(email: params.email, balance: params.balance, firstName: params.firstName ?: 'John',
                lastName: params.lastName ?: 'Doe', registered: params.registered != null ? params.registered : true,
                registrationToken: params.registrationToken ?: '123', encodedPassword: params.encodedPassword ?: '123'))
    }

    protected Investment prepareInvestment(Map params) {
        return investmentRepository.save(new Investment(investor: params.investor, amountToReceive: params.amountToReceive, loan: params.loan))
    }

    protected Payment preparePayment(Map params) {
        return paymentRepository.save(new Payment(amount: params.amount, loan: params.loan, created: params.created ?: ZonedDateTime.now()))
    }

}

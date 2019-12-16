package com.swaperclone.company.service

import com.swaperclone.UnitTestSpec
import com.swaperclone.common.entity.*
import com.swaperclone.common.exception.NotFoundException
import com.swaperclone.common.repository.InvestmentRepository
import com.swaperclone.common.repository.LoanRepository
import com.swaperclone.common.repository.PaymentRepository
import com.swaperclone.common.repository.UserRepository
import com.swaperclone.company.dto.PaymentDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class PaymentServiceSpec extends UnitTestSpec {

    private PaymentRepository paymentRepository = Mock(PaymentRepository)
    private LoanRepository loanRepository = Mock(LoanRepository)
    private InvestmentRepository investmentRepository = Mock(InvestmentRepository)
    private UserRepository userRepository = Mock(UserRepository)
    PaymentService paymentService = new PaymentService(paymentRepository: paymentRepository, loanRepository: loanRepository,
            investmentRepository: investmentRepository, userRepository: userRepository)

    void "test findPayments"() {
        given:
        ZonedDateTime created = ZonedDateTime.of(LocalDate.of(2019, 12, 15), LocalTime.MIN, ZoneId.systemDefault())
        paymentRepository.findByLoanId(1L, _ as Pageable) >> new PageImpl<Payment>([
                preparePayment(id: 10L, amount: BigDecimal.valueOf(100L), created: created),
                preparePayment(id: 11L, amount: BigDecimal.valueOf(120L), created: created)
        ])

        when:
        Page<PaymentDTO> result = paymentService.findPayments(1L, Mock(Pageable))

        then:
        result.totalPages == 1
        result.content.size() == 2
        result.content[i].id == id
        result.content[i].amount == amount
        result.content[i].created == created

        where:
        i | id  | amount
        0 | 10L | BigDecimal.valueOf(100)
        1 | 11L | BigDecimal.valueOf(120)
    }

    void "test create"() {
        given:
        Payment payment
        Loan loan
        loanRepository.findLoanInProgress(1L) >> Optional.empty()
        loanRepository.findLoanInProgress(2L) >> Optional.of(
                prepareInProgressLoanModel(amount: BigDecimal.valueOf(1000L), amountToReturn: BigDecimal.valueOf(2000L),
                        investorInterest: BigDecimal.valueOf(11), investorId: 1L, paidAmount: BigDecimal.valueOf(300))
        )
        loanRepository.findLoanInProgress(3L) >> Optional.of(
                prepareInProgressLoanModel(amount: BigDecimal.valueOf(1000L), amountToReturn: BigDecimal.valueOf(2000L),
                        investorInterest: BigDecimal.valueOf(11), investorId: 1L, paidAmount: BigDecimal.valueOf(1800))
        )
        loanRepository.getOne(2L) >> new Loan()
        loanRepository.getOne(3L) >> new Loan(id: 14L)
        User investor = new User(balance: BigDecimal.valueOf(40))
        investmentRepository.findByLoanId(14L) >> Optional.of(new Investment(investor: investor, amountToReceive: BigDecimal.valueOf(1110)))

        when:
        paymentService.create(1L, new PaymentDTO())

        then:
        thrown(NotFoundException)

        when:
        paymentService.create(2L, new PaymentDTO(amount: BigDecimal.valueOf(50L)))

        then:
        1 * paymentRepository.save(_) >> { args -> payment = args[0] }
        payment.amount == BigDecimal.valueOf(50)
        0 * userRepository.save(_)
        0 * loanRepository.save(_)

        when:
        paymentService.create(3L, new PaymentDTO(amount: BigDecimal.valueOf(200L)))

        then:
        1 * paymentRepository.save(_) >> { args -> payment = args[0] }
        payment.amount == BigDecimal.valueOf(200)
        1 * userRepository.save(_) >> { args -> investor = args[0] }
        investor.getBalance() == BigDecimal.valueOf(40).add(BigDecimal.valueOf(1110))
        1 * loanRepository.save(_) >> { args -> loan = args[0] }
        loan.getStatus() == LoanStatus.COMPLETE
    }

    void "test update"() {
        given:
        paymentRepository.updatePaymentAmount(1L, 1L, _ as BigDecimal) >> 0
        paymentRepository.updatePaymentAmount(1L, 2L, _ as BigDecimal) >> 1

        when:
        paymentService.update(1L, 1L, new PaymentDTO(amount: BigDecimal.valueOf(100)))

        then:
        thrown(NotFoundException)

        when:
        paymentService.update(1L, 2L, new PaymentDTO(amount: BigDecimal.valueOf(100)))

        then:
        notThrown(Exception)
    }

    void "test delete"() {
        given:
        paymentRepository.deletePayment(1L, 1L) >> 0
        paymentRepository.deletePayment(1L, 2L) >> 1

        when:
        paymentService.delete(1L, 1L)

        then:
        thrown(NotFoundException)

        when:
        paymentService.delete(1L, 2L)

        then:
        notThrown(Exception)
    }
}

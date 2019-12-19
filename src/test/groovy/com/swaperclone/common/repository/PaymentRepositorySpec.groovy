package com.swaperclone.common.repository

import com.swaperclone.common.RepositorySpec
import com.swaperclone.common.entity.Loan
import com.swaperclone.common.entity.LoanStatus
import com.swaperclone.common.entity.Payment
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

class PaymentRepositorySpec extends RepositorySpec {

    void "test findByLoanId"() {
        given:
        Loan withoutPayments = prepareLoan(debtorName: 'John1', amount: BigDecimal.valueOf(1100), investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(2200), status: LoanStatus.IN_PROGRESS)
        Loan withPayments = prepareLoan(debtorName: 'John2', amount: BigDecimal.valueOf(1200), investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(2400), status: LoanStatus.IN_PROGRESS)
        preparePayment(amount: BigDecimal.valueOf(100), loan: withPayments)
        preparePayment(amount: BigDecimal.valueOf(110), loan: withPayments)

        when:
        Page<Payment> result1 = paymentRepository.findByLoanId(withoutPayments.id, PageRequest.of(0, 5))
        Page<Payment> result2 = paymentRepository.findByLoanId(withPayments.id, PageRequest.of(0, 5))

        then:
        result1.content.size() == 0
        result2.content.size() == 2
        result2.content[0].amount == BigDecimal.valueOf(100)
        result2.content[1].amount == BigDecimal.valueOf(110)
    }

    void "test deletePayment"() {
        given:
        Loan withPayments = prepareLoan(debtorName: 'John2', amount: BigDecimal.valueOf(1200), investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(2400), status: LoanStatus.IN_PROGRESS)
        Payment p = preparePayment(amount: BigDecimal.valueOf(100), loan: withPayments)

        when:
        int deletedObjects = paymentRepository.deletePayment(withPayments.id, p.id)
        em.clear()

        then:
        deletedObjects == 1
        paymentRepository.count() == 0
    }

    void "test updatePaymentAmount"() {
        given:
        Loan irrelevantLoan = prepareLoan(debtorName: 'John1', amount: BigDecimal.valueOf(1100), investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(2200), status: LoanStatus.IN_PROGRESS)
        Loan l1 = prepareLoan(debtorName: 'John2', amount: BigDecimal.valueOf(1200), investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(2400), status: LoanStatus.IN_PROGRESS)
        Payment p = preparePayment(amount: BigDecimal.valueOf(100), loan: l1)

        when:
        int result1 = paymentRepository.updatePaymentAmount(irrelevantLoan.id, p.id, BigDecimal.valueOf(200))
        int result2 = paymentRepository.updatePaymentAmount(l1.id, p.id, BigDecimal.valueOf(200))
        em.clear()

        then:
        result1 == 0
        result2 == 1
        paymentRepository.findById(p.id).get().amount == BigDecimal.valueOf(200)
    }

}

package com.swaperclone.common.repository

import com.swaperclone.common.RepositorySpec
import com.swaperclone.common.entity.Loan
import com.swaperclone.common.entity.LoanStatus
import com.swaperclone.common.entity.User
import com.swaperclone.company.model.InProgressLoanModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

class LoanRepositorySpec extends RepositorySpec {

    void "test findNewLoans"() {
        given:
        Loan l1 = prepareLoan(debtorName: 'John1', amount: BigDecimal.valueOf(1000), status: LoanStatus.NEW, investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(2000))
        prepareLoan(debtorName: 'John2', amount: BigDecimal.valueOf(1200), status: LoanStatus.FAILED, investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(2400))

        when:
        Page<Loan> result = loanRepository.findNewLoans(PageRequest.of(0, 5))

        then:
        result.content.size() == 1
        result.content[0].id == l1.id
        result.content[0].debtorName == 'John1'
        result.content[0].amount == BigDecimal.valueOf(1000)
        result.content[0].amountToReturn == BigDecimal.valueOf(2000)
        result.content[0].status == LoanStatus.NEW
        result.content[0].investorInterest == BigDecimal.valueOf(11)
    }

    void "test findNewLoan"() {
        given:
        Loan l1 = prepareLoan(debtorName: 'John2', amount: BigDecimal.valueOf(1000), status: LoanStatus.FAILED, investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(1200))
        Loan l2 = prepareLoan(debtorName: 'John1', amount: BigDecimal.valueOf(1200), status: LoanStatus.NEW, investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(2400))

        when:
        Optional<Loan> result1 = loanRepository.findNewLoan(l1.id)
        Optional<Loan> result2 = loanRepository.findNewLoan(l2.id)

        then:
        result1.empty
        result2.get().id == l2.id
        result2.get().debtorName == 'John1'
        result2.get().amount == BigDecimal.valueOf(1200)
        result2.get().amountToReturn == BigDecimal.valueOf(2400)
        result2.get().status == LoanStatus.NEW
        result2.get().investorInterest == BigDecimal.valueOf(11)
    }

    void "test findLoanInProgress"() {
        given:
        Loan newLoan = prepareLoan(debtorName: 'John', amount: BigDecimal.valueOf(1000), investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(2000), status: LoanStatus.NEW)
        Loan withoutPayments = prepareLoan(debtorName: 'John1', amount: BigDecimal.valueOf(1100), investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(2200), status: LoanStatus.IN_PROGRESS)
        Loan withPayments = prepareLoan(debtorName: 'John2', amount: BigDecimal.valueOf(1200), investorInterest: BigDecimal.valueOf(12),
                amountToReturn: BigDecimal.valueOf(2400), status: LoanStatus.IN_PROGRESS)
        User user1 = prepareUser(email: 'user1@abc.lv', balance: BigDecimal.ZERO)
        User user2 = prepareUser(email: 'user2@abc.lv', balance: BigDecimal.ZERO)
        prepareInvestment(investor: user1, amountToReceive: BigDecimal.valueOf(1210), loan: withoutPayments)
        prepareInvestment(investor: user2, amountToReceive: BigDecimal.valueOf(1320), loan: withPayments)
        preparePayment(amount: BigDecimal.valueOf(100), loan: withPayments)
        preparePayment(amount: BigDecimal.valueOf(110), loan: withPayments)

        when:
        Optional<InProgressLoanModel> result1 = loanRepository.findLoanInProgress(newLoan.id)
        Optional<InProgressLoanModel> result2 = loanRepository.findLoanInProgress(withoutPayments.id)
        Optional<InProgressLoanModel> result3 = loanRepository.findLoanInProgress(withPayments.id)

        then:
        result1.empty
        result2.get().amount == BigDecimal.valueOf(1100)
        result2.get().amountToReturn == BigDecimal.valueOf(2200)
        result2.get().investorInterest == BigDecimal.valueOf(11)
        result2.get().investorId == user1.id
        result2.get().paidAmount == BigDecimal.ZERO
        result3.get().amount == BigDecimal.valueOf(1200)
        result3.get().amountToReturn == BigDecimal.valueOf(2400)
        result3.get().investorInterest == BigDecimal.valueOf(12)
        result3.get().investorId == user2.id
        result3.get().paidAmount == BigDecimal.valueOf(210)
    }

    void "test deleteNewLoan"() {
        given:
        Loan newLoan = prepareLoan(debtorName: 'John', amount: BigDecimal.valueOf(1000), investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(2000), status: LoanStatus.NEW)
        Loan inProgressLoan = prepareLoan(debtorName: 'John1', amount: BigDecimal.valueOf(1100), investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(2200), status: LoanStatus.IN_PROGRESS)

        when:
        int deletedObjects = loanRepository.deleteNewLoan(inProgressLoan.id)
        em.clear()

        then:
        deletedObjects == 0
        !loanRepository.findById(inProgressLoan.id).empty

        when:
        deletedObjects = loanRepository.deleteNewLoan(newLoan.id)
        em.clear()

        then:
        deletedObjects == 1
        loanRepository.findById(newLoan.id).empty
    }

    void "test updateLoanStatusToFailed"() {
        given:
        Loan inProgressLoan = prepareLoan(debtorName: 'John1', amount: BigDecimal.valueOf(1100), investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(2200), status: LoanStatus.IN_PROGRESS)

        when:
        int res = loanRepository.updateLoanStatusToFailed(inProgressLoan.id)
        em.clear()

        then:
        res == 1
        loanRepository.findById(inProgressLoan.id).get().status == LoanStatus.FAILED
    }

}

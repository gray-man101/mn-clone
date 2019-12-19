package com.swaperclone.common.repository

import com.swaperclone.common.RepositorySpec
import com.swaperclone.common.entity.Investment
import com.swaperclone.common.entity.Loan
import com.swaperclone.common.entity.LoanStatus
import com.swaperclone.common.entity.User
import org.springframework.data.domain.PageRequest

class InvestmentRepositorySpec extends RepositorySpec {

    def "test findByLoanId"() {
        given:
        Loan loan = prepareLoan(debtorName: 'John', amount: BigDecimal.valueOf(1000), investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(2000), status: LoanStatus.NEW)
        User user = prepareUser(email: 'user1@abc.lv', balance: BigDecimal.ZERO)
        prepareInvestment(investor: user, amountToReceive: BigDecimal.valueOf(1100), loan: loan)

        when:
        Optional<Investment> i1 = investmentRepository.findByLoanId(-1L)
        Optional<Investment> i2 = investmentRepository.findByLoanId(loan.id)

        then:
        i1.empty
        i2.get().amountToReceive == BigDecimal.valueOf(1100)
        i2.get().investor == user
    }

    def "test findInvestmentStatuses"() {
        given:
        Loan newLoan = prepareLoan(debtorName: 'John', amount: BigDecimal.valueOf(1000), investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(2000), status: LoanStatus.NEW)
        Loan withoutPayments = prepareLoan(debtorName: 'John1', amount: BigDecimal.valueOf(1100), investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(2200), status: LoanStatus.IN_PROGRESS)
        Loan withPayments = prepareLoan(debtorName: 'John2', amount: BigDecimal.valueOf(1200), investorInterest: BigDecimal.valueOf(11),
                amountToReturn: BigDecimal.valueOf(2400), status: LoanStatus.IN_PROGRESS)
        User user = prepareUser(email: 'user2@abc.lv', balance: BigDecimal.ZERO)
        Investment i1 = prepareInvestment(investor: user, amountToReceive: BigDecimal.valueOf(1210), loan: withoutPayments)
        Investment i2 = prepareInvestment(investor: user, amountToReceive: BigDecimal.valueOf(1320), loan: withPayments)
        preparePayment(amount: BigDecimal.valueOf(100), loan: withPayments)
        preparePayment(amount: BigDecimal.valueOf(110), loan: withPayments)

        when:
        def result = investmentRepository.findInvestmentStatuses(user.id, PageRequest.of(0, 5))

        then:
        result.content.size() == 2
        result.content[0].getId() == i1.id
        result.content[0].getDebtorName() == 'John1'
        result.content[0].getAmount() == BigDecimal.valueOf(1100)
        result.content[0].getAmountToReceive() == BigDecimal.valueOf(1210)
        result.content[0].getPayments() == 0
        result.content[0].percentageComplete == BigDecimal.ZERO
        result.content[1].getId() == i2.id
        result.content[1].getDebtorName() == 'John2'
        result.content[1].getAmount() == BigDecimal.valueOf(1200)
        result.content[1].getAmountToReceive() == BigDecimal.valueOf(1320)
        result.content[1].getPayments() == 2
        result.content[1].percentageComplete == BigDecimal.valueOf(0.0875)
    }

}

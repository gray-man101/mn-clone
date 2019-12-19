package com.swaperclone


import com.swaperclone.common.entity.Loan
import com.swaperclone.common.entity.Payment
import com.swaperclone.common.entity.User
import com.swaperclone.company.dto.LoanDTO
import com.swaperclone.company.model.InProgressLoanModel
import com.swaperclone.customer.model.InvestmentStatusModel
import spock.lang.Specification

class UnitTestSpec extends Specification {

    protected Loan prepareLoan(Map params) {
        return new Loan(id: params.id, amount: params.amount, amountToReturn: params.amountToReturn,
                investorInterest: params.investorInterest, status: params.status, debtorName: params.debtorName)
    }

    protected LoanDTO prepareLoanDTO(Map params) {
        return new LoanDTO(amount: params.amount, amountToReturn: params.amountToReturn,
                investorInterest: params.investorInterest, status: params.status, debtorName: params.debtorName)
    }

    protected InProgressLoanModel prepareInProgressLoanModel(Map params) {
        InProgressLoanModel m = Mock(InProgressLoanModel)
        m.getAmount() >> params.amount
        m.getAmountToReturn() >> params.amountToReturn
        m.getInvestorInterest() >> params.investorInterest
        m.getInvestorId() >> params.investorId
        m.getPaidAmount() >> params.paidAmount
        return m
    }

    protected Payment preparePayment(Map params) {
        return new Payment(id: params.id, amount: params.amount, created: params.created)
    }

    protected User prepareUser(Map params) {
        return new User(id: params.id, email: params.email, firstName: params.firstName ?: 'John', lastName: params.lastName ?: 'Doe',
                balance: params.balance ?: BigDecimal.ZERO, registered: params.registered != null ? params.registered : true,
                registrationToken: params.registrationToken ?: '123')
    }

    protected InvestmentStatusModel prepareInvestmentStatusModel(Map params) {
        InvestmentStatusModel result = Mock(InvestmentStatusModel)
        result.getId() >> params.id
        result.getDebtorName() >> (params.debtorName != null ? params.debtorName : 'John')
        result.getAmount() >> params.amount
        result.getAmountToReceive() >> params.amountToReceive
        result.getPayments() >> params.payments
        result.getPercentageComplete() >> params.percentageComplete
        return result
    }
}

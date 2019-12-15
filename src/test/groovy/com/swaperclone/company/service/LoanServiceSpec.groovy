package com.swaperclone.company.service

import com.swaperclone.common.entity.Loan
import com.swaperclone.common.entity.LoanStatus
import com.swaperclone.common.entity.User
import com.swaperclone.common.exception.NotFoundException
import com.swaperclone.common.repository.LoanRepository
import com.swaperclone.common.repository.UserRepository
import com.swaperclone.company.dto.LoanDTO
import com.swaperclone.company.info.FailedLoanInfo
import com.swaperclone.company.model.InProgressLoanModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import spock.lang.Specification

class LoanServiceSpec extends Specification {

    LoanRepository loanRepository = Mock(LoanRepository)
    UserRepository userRepository = Mock(UserRepository)
    LoanService loanService = new LoanService(loanRepository: loanRepository, userRepository: userRepository)

    void "test getLoans()"() {
        given:
        loanRepository.findAll(_ as Pageable) >> new PageImpl<Loan>([
                prepareLoan(id: 1L, amount: BigDecimal.valueOf(1000), amountToReturn: BigDecimal.valueOf(2000),
                        investorInterest: BigDecimal.valueOf(11), status: LoanStatus.NEW, debtorName: 'John'),
                prepareLoan(id: 2L, amount: BigDecimal.valueOf(1500), amountToReturn: BigDecimal.valueOf(3000),
                        investorInterest: BigDecimal.valueOf(12), status: LoanStatus.IN_PROGRESS, debtorName: 'Steve')])

        when:
        Page<LoanDTO> result = loanService.getLoans(Mock(Pageable))

        then:
        result.totalPages == 1
        result.content.size() == 2
        result.content[i].id == id
        result.content[i].amount == amount
        result.content[i].amountToReturn == amountToReturn
        result.content[i].investorInterest == investorInterest
        result.content[i].status == status
        result.content[i].debtorName == debtorName

        where:
        i | id | amount                   | amountToReturn           | investorInterest       | status                 | debtorName
        0 | 1L | BigDecimal.valueOf(1000) | BigDecimal.valueOf(2000) | BigDecimal.valueOf(11) | LoanStatus.NEW         | 'John'
        1 | 2L | BigDecimal.valueOf(1500) | BigDecimal.valueOf(3000) | BigDecimal.valueOf(12) | LoanStatus.IN_PROGRESS | 'Steve'
    }

    void "test getAvailableLoans()"() {
        given:
        loanRepository.findNewLoans(_ as Pageable) >> new PageImpl<Loan>([
                prepareLoan(id: 1L, amount: BigDecimal.valueOf(1000), amountToReturn: BigDecimal.valueOf(2000),
                        investorInterest: BigDecimal.valueOf(11), status: LoanStatus.NEW, debtorName: 'John'),
                prepareLoan(id: 2L, amount: BigDecimal.valueOf(1500), amountToReturn: BigDecimal.valueOf(3000),
                        investorInterest: BigDecimal.valueOf(12), status: LoanStatus.NEW, debtorName: 'Steve')])
        when:
        def result = loanService.getAvailableLoans(Mock(Pageable))

        then:
        result.totalPages == 1
        result.content.size() == 2
        result.content[i].id == id
        result.content[i].amount == amount
        result.content[i].amountToReturn == null
        result.content[i].investorInterest == investorInterest
        result.content[i].status == status
        result.content[i].debtorName == debtorName

        where:
        i | id | amount                   | investorInterest       | status         | debtorName
        0 | 1L | BigDecimal.valueOf(1000) | BigDecimal.valueOf(11) | LoanStatus.NEW | 'John'
        1 | 2L | BigDecimal.valueOf(1500) | BigDecimal.valueOf(12) | LoanStatus.NEW | 'Steve'
    }

    void "test create"() {
        given:
        Loan loan
        LoanDTO loanDTO = prepareLoanDTO(amount: BigDecimal.valueOf(1000), amountToReturn: BigDecimal.valueOf(2000),
                investorInterest: BigDecimal.valueOf(11), status: LoanStatus.NEW, debtorName: 'John')

        when:
        loanService.create(loanDTO)

        then:
        1 * loanRepository.save(_) >> { args -> loan = args[0] }
        loan.amount == BigDecimal.valueOf(1000)
        loan.amountToReturn == BigDecimal.valueOf(2000)
        loan.investorInterest == BigDecimal.valueOf(11)
        loan.status == LoanStatus.NEW
        loan.debtorName == 'John'
    }

    void "test update"() {
        given:
        Loan loan = new Loan()
        loanRepository.findNewLoan(1L) >> Optional.of(loan)
        loanRepository.findNewLoan(2L) >> Optional.empty()
        LoanDTO loanDTO = prepareLoanDTO(amount: BigDecimal.valueOf(1000), amountToReturn: BigDecimal.valueOf(2000),
                investorInterest: BigDecimal.valueOf(11), status: LoanStatus.NEW, debtorName: 'John')

        when:
        loanService.update(2L, loanDTO)

        then:
        thrown(NotFoundException)

        when:
        loanService.update(1L, loanDTO)

        then:
        1 * loanRepository.save(_) >> { args -> loan = args[0] }
        loan.amount == BigDecimal.valueOf(1000)
        loan.amountToReturn == BigDecimal.valueOf(2000)
        loan.investorInterest == BigDecimal.valueOf(11)
        loan.status == null
        loan.debtorName == 'John'
    }

    void "test delete"() {
        given:
        loanRepository.deleteNewLoan(1L) >> 0
        loanRepository.deleteNewLoan(2L) >> 1

        when:
        loanService.delete(1L)

        then:
        thrown(NotFoundException)

        when:
        loanService.delete(2L)

        then:
        notThrown(Exception)
    }

    void "test setFailedStatus"() {
        given:
        loanRepository.findLoanInProgress(1L) >> Optional.empty()
        loanRepository.findLoanInProgress(2L) >> Optional.of(prepareInProgressLoanModel(amount: BigDecimal.valueOf(1000),
                amountToReturn: BigDecimal.valueOf(2000), investorInterest: BigDecimal.valueOf(11), investorId: 150L,
                paidAmount: BigDecimal.valueOf(200)))
        User user = new User(email: 'aa@bb.lv', balance: BigDecimal.valueOf(10))
        userRepository.getOne(150L) >> user

        when:
        loanService.setFailedStatus(1L)

        then:
        thrown(NotFoundException)

        when:
        FailedLoanInfo result = loanService.setFailedStatus(2L)

        then:
        result.investorEmail == 'aa@bb.lv'
        result.partialRefundAmount == BigDecimal.valueOf(111L)
        1 * userRepository.save(_) >> { args -> user = args[0] }
        user.getBalance() == BigDecimal.valueOf(121L)
    }

    private Loan prepareLoan(Map params) {
        return new Loan(id: params.id, amount: params.amount, amountToReturn: params.amountToReturn,
                investorInterest: params.investorInterest, status: params.status, debtorName: params.debtorName)
    }

    private LoanDTO prepareLoanDTO(Map params) {
        return new LoanDTO(amount: params.amount, amountToReturn: params.amountToReturn,
                investorInterest: params.investorInterest, status: params.status, debtorName: params.debtorName)
    }

    private InProgressLoanModel prepareInProgressLoanModel(Map params) {
        InProgressLoanModel m = Mock(InProgressLoanModel)
        m.getAmount() >> params.amount
        m.getAmountToReturn() >> params.amountToReturn
        m.getInvestorInterest() >> params.investorInterest
        m.getInvestorId() >> params.investorId
        m.getPaidAmount() >> params.paidAmount
        return m
    }
}

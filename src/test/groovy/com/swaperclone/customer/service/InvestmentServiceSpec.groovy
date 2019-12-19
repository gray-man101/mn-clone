package com.swaperclone.customer.service

import com.swaperclone.UnitTestSpec
import com.swaperclone.common.entity.Investment
import com.swaperclone.common.entity.Loan
import com.swaperclone.common.entity.LoanStatus
import com.swaperclone.common.entity.User
import com.swaperclone.common.exception.InsufficientFundsException
import com.swaperclone.common.exception.NotFoundException
import com.swaperclone.common.repository.InvestmentRepository
import com.swaperclone.common.repository.LoanRepository
import com.swaperclone.common.repository.UserRepository
import com.swaperclone.company.util.ReturnAmountCalculationUtils
import com.swaperclone.customer.dto.InvestmentDTO
import com.swaperclone.customer.model.InvestmentStatusModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

class InvestmentServiceSpec extends UnitTestSpec {

    InvestmentRepository investmentRepository = Mock(InvestmentRepository)
    UserRepository userRepository = Mock(UserRepository)
    LoanRepository loanRepository = Mock(LoanRepository)
    InvestmentService investmentService = new InvestmentService(investmentRepository: investmentRepository,
            userRepository: userRepository, loanRepository: loanRepository)

    void "test invest"() {
        given:
        User savedUser
        Loan savedLoan
        Investment savedInvestment
        User poorUser = prepareUser(id: 1L, email: 'aa@aa.lv', balance: BigDecimal.ZERO, firstName: 'Aa', lastName: 'Aa', registered: true)
        User richUser = prepareUser(id: 4L, email: 'bb@bb.lv', balance: BigDecimal.valueOf(10000), firstName: 'B1', lastName: 'B2', registered: true)
        Loan loan = prepareLoan(id: 1L, amount: BigDecimal.valueOf(1000), amountToReturn: BigDecimal.valueOf(2000),
                investorInterest: BigDecimal.valueOf(11), status: LoanStatus.NEW, debtorName: 'John')
        userRepository.findRegisteredById(1L) >> Optional.of(poorUser)
        userRepository.findRegisteredById(2L) >> Optional.empty()
        userRepository.findRegisteredById(4L) >> Optional.of(richUser)
        loanRepository.findNewLoan(1L) >> Optional.of(loan)
        loanRepository.findNewLoan(2L) >> Optional.empty()

        when:
        investmentService.invest(1L, 2L)

        then:
        thrown(NotFoundException)

        when:
        investmentService.invest(2L, 1L)

        then:
        thrown(NotFoundException)

        when:
        investmentService.invest(1L, 1L)

        then:
        thrown(InsufficientFundsException)

        when:
        investmentService.invest(4L, 1L)

        then:
        userRepository.save(_) >> { args -> savedUser = args[0] }
        savedUser.balance == BigDecimal.valueOf(9000)
        loanRepository.save(_) >> { args -> savedLoan = args[0] }
        savedLoan.status == LoanStatus.IN_PROGRESS
        investmentRepository.save(_) >> { args -> savedInvestment = args[0] }
        savedInvestment.investor == richUser
        savedInvestment.loan == loan
        savedInvestment.amountToReceive == ReturnAmountCalculationUtils.calculateInvestorReturnAmount(BigDecimal.valueOf(1000), BigDecimal.valueOf(11))
    }

    void "test getInvestments"() {
        given:
        investmentRepository.findInvestmentStatuses(1L, _ as Pageable) >> Page.empty()
        investmentRepository.findInvestmentStatuses(2L, _ as Pageable) >> new PageImpl<InvestmentStatusModel>([
                prepareInvestmentStatusModel(id: 1L, debtorName: 'Debtor1', amount: BigDecimal.valueOf(1000),
                        amountToReceive: BigDecimal.valueOf(1200), payments: 1, percentageComplete: BigDecimal.valueOf(0.05532243)),
                prepareInvestmentStatusModel(id: 2L, debtorName: 'Debtor2', amount: BigDecimal.valueOf(1100),
                        amountToReceive: BigDecimal.valueOf(1300), payments: 2, percentageComplete: BigDecimal.valueOf(0.654895864)),
        ])

        when:
        Page<InvestmentDTO> result1 = investmentService.getInvestments(1L, PageRequest.of(0, 5))
        Page<InvestmentDTO> result2 = investmentService.getInvestments(2L, PageRequest.of(0, 5))

        then:
        result1.content.empty
        result2.content[0].id == 1L
        result2.content[0].debtorName == 'Debtor1'
        result2.content[0].payments == 1
        result2.content[0].percentageComplete == BigDecimal.valueOf(5.53)
        result2.content[0].overallAmount == BigDecimal.valueOf(1000)
        result2.content[0].amountToReceive == BigDecimal.valueOf(1200)
        result2.content[1].id == 2L
        result2.content[1].debtorName == 'Debtor2'
        result2.content[1].payments == 2
        result2.content[1].percentageComplete == BigDecimal.valueOf(65.48)
        result2.content[1].overallAmount == BigDecimal.valueOf(1100)
        result2.content[1].amountToReceive == BigDecimal.valueOf(1300)
    }

}

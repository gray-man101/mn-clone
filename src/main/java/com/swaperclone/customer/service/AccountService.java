package com.swaperclone.customer.service;

import com.swaperclone.common.entity.User;
import com.swaperclone.common.exception.InsufficientFundsException;
import com.swaperclone.common.exception.NotFoundException;
import com.swaperclone.common.repository.UserRepository;
import com.swaperclone.customer.dto.AccountInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    @Autowired
    private UserRepository userRepository;

    public AccountInfoDTO getAccountInfo(Long customerId) {
        return userRepository.findRegisteredById(customerId)
                .map(customer -> {
                    AccountInfoDTO dto = new AccountInfoDTO();
                    dto.setFirstName(customer.getFirstName());
                    dto.setLastName(customer.getLastName());
                    dto.setEmail(customer.getEmail());
                    dto.setBalance(customer.getBalance());
                    return dto;
                }).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public void topUp(Long customerId, BigDecimal amount) {
        User user = userRepository.findRegisteredById(customerId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);
    }

    public void withdraw(Long customerId, BigDecimal amount) {
        User user = userRepository.findRegisteredById(customerId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        BigDecimal newBalance = user.getBalance().subtract(amount);
        if (BigDecimal.ZERO.compareTo(newBalance) > 0) {
            throw new InsufficientFundsException("Not enough funds to withdraw");
        }
        user.setBalance(newBalance);
        userRepository.save(user);
    }

}

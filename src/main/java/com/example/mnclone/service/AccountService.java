package com.example.mnclone.service;

import com.example.mnclone.CtxInfo;
import com.example.mnclone.dto.AccountInfoDTO;
import com.example.mnclone.entity.User;
import com.example.mnclone.exception.InsufficientFundsException;
import com.example.mnclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    @Autowired
    private UserRepository userRepository;

    public AccountInfoDTO getAccountInfo() {
        return userRepository.findRegisteredById(CtxInfo.USER_ID).map(user -> {
            AccountInfoDTO dto = new AccountInfoDTO();
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
            dto.setBalance(user.getBalance());
            return dto;
        }).orElseThrow(RuntimeException::new);
    }

    public void topUp(BigDecimal amount) {
        User user = userRepository.findRegisteredById(CtxInfo.USER_ID).orElseThrow(RuntimeException::new);
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);
    }

    public void withdraw(BigDecimal amount) {
        User user = userRepository.findRegisteredById(CtxInfo.USER_ID).orElseThrow(RuntimeException::new);
        BigDecimal newBalance = user.getBalance().subtract(amount);
        if (BigDecimal.ZERO.compareTo(newBalance) > 0) {
            throw new InsufficientFundsException();
        }
        user.setBalance(newBalance);
        userRepository.save(user);
    }

}

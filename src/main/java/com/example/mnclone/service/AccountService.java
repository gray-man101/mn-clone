package com.example.mnclone.service;

import com.example.mnclone.dto.AccountInfoDTO;
import com.example.mnclone.exception.NotFoundException;
import com.example.mnclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    //TODO get from ctx
    private static final Long USER_ID = 1L;

    @Autowired
    private UserRepository userRepository;

    public AccountInfoDTO getAccountInfo() {
        return userRepository.findRegisteredById(USER_ID).map(user -> {
            AccountInfoDTO dto = new AccountInfoDTO();
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
            dto.setBalance(user.getBalance());
            return dto;
        }).orElseThrow(() -> new NotFoundException("account info not found"));
    }

}

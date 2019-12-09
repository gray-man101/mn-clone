package com.example.mnclone.service;

import com.example.mnclone.CtxInfo;
import com.example.mnclone.dto.IvstDTO;
import com.example.mnclone.dto.IvstStatusDTO;
import com.example.mnclone.dto.LnDTO;
import com.example.mnclone.dto.PmDTO;
import com.example.mnclone.entity.Ivst;
import com.example.mnclone.entity.Ln;
import com.example.mnclone.entity.LnStatus;
import com.example.mnclone.entity.User;
import com.example.mnclone.exception.InsufficientFundsException;
import com.example.mnclone.exception.NotFoundException;
import com.example.mnclone.mapper.LnDTOMapper;
import com.example.mnclone.mapper.PmDTOMapper;
import com.example.mnclone.model.IvstStatusModel;
import com.example.mnclone.repository.IvstRepository;
import com.example.mnclone.repository.LnRepository;
import com.example.mnclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IvstService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IvstRepository ivstRepository;
    @Autowired
    private LnRepository lnRepository;

    @Transactional
    public void ivst(Long lnId) {
        User user = userRepository.findRegisteredById(CtxInfo.USER_ID).orElseThrow(RuntimeException::new);
        Ln ln = lnRepository.findNewById(lnId).orElseThrow(RuntimeException::new);

        BigDecimal newBalance = user.getBalance().subtract(ln.getAmount());
        if (BigDecimal.ZERO.compareTo(newBalance) > 0) {
            throw new InsufficientFundsException();
        }
        user.setBalance(newBalance);
        userRepository.save(user);

        Ivst ivst = new Ivst();
        ivst.setUser(user);
        ivst.setLn(ln);
        ivstRepository.save(ivst);

        ln.setStatus(LnStatus.IN_PROGRESS);
        lnRepository.save(ln);
    }

    public Page<IvstDTO> getIvsts(Pageable pageable) {
        return ivstRepository.findByUserId(CtxInfo.USER_ID, pageable).map(ivst -> {
            //TODO refine queries
            IvstDTO ivstDTO = new IvstDTO();
            Ln ln = ivst.getLn();
            LnDTO lnDTO = LnDTOMapper.map(ln);
            ivstDTO.setLnDTO(lnDTO);

            List<PmDTO> pmDTOList = ln.getPs().stream()
                    .map(PmDTOMapper::map)
                    .collect(Collectors.toList());
            ivstDTO.setPs(pmDTOList);

            return ivstDTO;
        });
    }

    public IvstStatusDTO getIvsStatus(Long id) {
        IvstStatusModel ivstStatusModel = ivstRepository.findIvstStatus(id).orElseThrow(NotFoundException::new);
        IvstStatusDTO dto = new IvstStatusDTO();
        dto.setId(ivstStatusModel.getId());
        dto.setOverallAmount(ivstStatusModel.getOverallAmount());
        dto.setPaidAmount(ivstStatusModel.getPaidAmount());
        dto.setPayments(ivstStatusModel.getPayments());
        return dto;
    }

}

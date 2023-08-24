package com.lec.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lec.domain.Inform;
import com.lec.persistence.InformRepository;
import com.lec.service.InformService;

@Service
public class InformServiceImpl implements InformService {

    private final InformRepository informRepository;

    @Autowired
    public InformServiceImpl(InformRepository informRepository) {
        this.informRepository = informRepository;
    }

    @Override
    public Inform getInform(Inform inform) {
        return informRepository.findById(inform.getISeq()).orElse(null);
    }

    @Override
    public void insertInform(Inform inform) {
        informRepository.save(inform);
    }

    @Override
    public void updateInform(Inform inform) {
        informRepository.findById(inform.getISeq()).ifPresent(existingInform -> {
            existingInform.setIContent(inform.getIContent());
            existingInform.setITitle(inform.getITitle());
            existingInform.setIDate(LocalDateTime.now()); // 현재 시간으로 업데이트
            // 필요한 경우 다른 필드도 업데이트
            
            informRepository.save(existingInform); // 수정된 엔티티를 저장
        });
    }

    @Override
    public void deleteInform(Inform inform) {
        informRepository.deleteById(inform.getISeq());
    }

    @Override
    public List<Inform> getAllInformsOrderByIDateDesc() {
        return informRepository.findAllByOrderByIDateDesc();
    }

    @Override
    public Page<Inform> getInformlist(Pageable pageable, String searchType, String searchWord) {
        Page<Inform> result;
        if (searchType.equalsIgnoreCase("iTitle")) {
            result = informRepository.findByiTitleContaining(searchWord, pageable);
        } else {
            result = informRepository.findByiContentContaining(searchWord, pageable);
        }

        return result;
    }
}

package com.lec.service;


import com.lec.domain.Inform;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InformService {

    Inform getInform(Inform inform);

    void insertInform(Inform inform);

    void updateInform(Inform inform);

    void deleteInform(Inform inform);
    
    List<Inform> getAllInformsOrderByIDateDesc();

    Page<Inform> getInformlist(Pageable pageable, String searchType, String searchWord);
}

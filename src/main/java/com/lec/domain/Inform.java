package com.lec.domain;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Entity
public class Inform {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int iSeq; // i로 인식이 안된다고함.

    private String iTitle;

    @Lob
    private String iContent;

    private LocalDateTime IDate; // iDate로 하면 실행이 안됨 이유를 모르겠음
    
    @ElementCollection
    private List<String> fileNames;
    
    private int iCnt;
    private String memberId;
}

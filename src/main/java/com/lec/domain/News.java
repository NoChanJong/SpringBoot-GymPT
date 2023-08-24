package com.lec.domain;

import javax.persistence.*;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long n_seq;

    private String n_title;

    private String n_content;

    @Column(insertable = false, updatable = false, columnDefinition = "timestamp default now()")
    private Date n_date;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int n_cnt;

    @ElementCollection
    private List<String> n_uploadFiles;



    // 다른 필드에 대한 getter, setter 등도 필요하다면 추가로 작성합니다.
}




package com.lec.domain;



import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Member {

    @Id
    private String id;
 
    private String password;
    private String name;
    private String role;
    
    // 새로운 필드들 추가
    @Column(nullable = false)
    private Date birth;
    
    @Column(nullable = false)
    private String email;
    
    @Transient
    private MultipartFile uploadFile;
    
    private String profile;
    private Integer height;
    private Integer weight;
    
    
    @Column(name = "userAddr1", nullable = false)
    private String userAddr1;

    @Column(name = "userAddr2", nullable = false)
    private String userAddr2;

    @Column(name = "userAddr3", nullable = false)
    private String userAddr3;
    
    @Column(nullable = false)
    private String p_num;
    
    private String gender;
    private String account;
    
   
    @Column(nullable = false)
    private BigDecimal point = BigDecimal.ZERO;
    
    private String nickname;

}

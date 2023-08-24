package com.lec.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "s_board")
public class Shop {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "s_seq",insertable = false, updatable = false)
    private int sSeq;

	@Column(name = "s_name", nullable = false)
	private String sName;


    @Column(name = "s_content", nullable = false, columnDefinition = "TEXT")
    private String sContent;

    @Column(name = "s_cate", nullable = false)
    private String sCate;

    @Column(name = "s_upload")
    private String sUpload;
    
    @Column(name = "s_upload_thumb")
    private String sUploadThumb;
    @Column(name = "s_upload_content")
    private String sUploadContent;
    @Transient
    private List<MultipartFile> uploadFiles;
    @Transient
    private List<MultipartFile> uploadFilesThumb;
    @Transient
    private List<MultipartFile> uploadFilesContent;


    @Column(name = "s_price", nullable = false)
    private BigDecimal sPrice;

    @Column(name = "s_amount", nullable = false)
    private int sAmount;

    @Column(name = "s_rating", columnDefinition = "DECIMAL(3,2) DEFAULT 0")
    private BigDecimal sRating = BigDecimal.ZERO;

    @Column(name = "s_like", columnDefinition = "INT DEFAULT 0")
    private int sLike;
   
    @Column(name = "member_id", nullable = false)
    private String memberId;
    
	    
	
}

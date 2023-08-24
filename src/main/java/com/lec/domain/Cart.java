package com.lec.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
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
@Table(name = "s_cart")
public class Cart {
   
   @Id
   @Column(name = "cart_no", nullable = false,insertable = false, updatable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int cartNo;
   
   @Column(name = "member_id", nullable = false)
   private String memberId;
   
   
    @Column(name = "s_seq", nullable = false)
   private int sSeq;
   
   @Column(name="cart_date", insertable = false, updatable = false, columnDefinition = "date default now()")
   private Date cartDate;
    
    @Column(name = "s_name", nullable = false)
    private String sName;
    @Column(name = "s_price", nullable = false)
    private BigDecimal sPrice;
    @Column(name = "s_amount", nullable = false)
    private int sAmount;
    
    @Transient
    private List<MultipartFile> uploadFilesThumb;
    @Column(name = "s_upload_thumb")
    private String sUploadThumb;
    
//    @EmbeddedId
//   private CartExamId cartExamId;
    
    
}






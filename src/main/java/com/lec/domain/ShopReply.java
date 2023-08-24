package com.lec.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@ToString
@Getter
@Setter
@Table(name = "s_reply")
public class ShopReply {
   

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "s_rno",insertable = false, updatable = false)
   private int sRno;
   @Column(name = "s_seq", nullable = false)
   private int sSeq;
   
   @Column(name = "member_id", nullable = false)
   private String memberId;
   
   @Column(name = "sr_content", nullable = false)
   private String srContent;
   
   @Column(name="sr_date", insertable = false, updatable = false, columnDefinition = "timestamp default now()")
   private Date srDate;
   
   @Column(name = "s_rating", columnDefinition = "DECIMAL(3,2) DEFAULT 0")
   private BigDecimal sRating;
   
   @Column(name = "sr_upload")
   private String srUpload;

   @Transient
   private List<MultipartFile> uploadFiles;
   
//    @EmbeddedId
//    private ExamId examId;
   
   
}

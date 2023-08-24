package com.lec.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "point_list")
public class PointList {
   
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_no",insertable = false, updatable = false)
    private int listNo;
   
   @Column(name = "member_id", nullable = false)
   private String memberId;
   
   @Column(name = "current_point", nullable = false)
   private BigDecimal currentPoint;
   
   @Column(name = "insert_point", nullable = false)
   private BigDecimal insertPoint;

   @Column(insertable = false, updatable = false, columnDefinition = "timestamp default now()")
   private Date sendDate;
   
   @Column(name = "send_money", nullable = false)
   private String sendMoney;
  
}
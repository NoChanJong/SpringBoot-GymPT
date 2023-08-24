package com.lec.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "s_order_detail")
public class ShopOrderDetail {
        
       @Column(name = "order_no", nullable = false)
       private int orderNo;
       
       @Id
       @Column(name = "order_detail_no", insertable = false, updatable = false)
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private int orderDetailNo;
       
       @Column(name = "s_seq", nullable = false)
       private int sSeq;
       
       @Column(name = "s_amount", nullable = false)
       private int sAmount;
       
       @Column(name = "s_name", nullable = false)
       private String sName;
       @Column(name = "s_price", nullable = false)
       private BigDecimal sPrice;
       @Column(name = "s_upload_thumb")
       private String sUploadThumb;
       @Column(name = "order_status", nullable = false)
       private String orderStatus;
//       @EmbeddedId
//       private OrderExamId orderExamId;
}








package com.lec.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "s_order")
//@IdClass(OrderExamId.class)
public class ShopOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_no",  nullable = false ,insertable = false, updatable = false)
    private int orderNo;

    @Column(name = "member_id", nullable = false)
    private String memberId;

    @Column(name = "order_rec", nullable = false)
    private String orderRec;

    @Column(name = "userAddr1", nullable = false)
    private String userAddr1;

    @Column(name = "userAddr2", nullable = false)
    private String userAddr2;

    @Column(name = "userAddr3", nullable = false)
    private String userAddr3;

    @Column(name = "phoneNum", nullable = false)
    private String phoneNum;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(insertable = false, updatable = false, columnDefinition = "timestamp default now()")
    private Date orderDate;
    
    @Column(name = "delivery", nullable = false)
    private String delivery;
//    @EmbeddedId
//    private OrderExamId orderExamId;
}












package com.lec.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "c_board")
public class Community {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "c_seq")
    private Integer c_seq;
	
	@Column(name = "c_title")
    private String c_title;
	
	@Column(name = "c_cate")
    private String c_cate;

	@Column(name = "c_content")
    private String c_content;

	@Column(name = "c_like", columnDefinition = "INT DEFAULT 0")
	private Integer c_like;
	
	@Column(insertable = false, updatable = false, columnDefinition = "bigint default 0")
	private Long c_cnt;
	
    @Column(insertable = false, updatable = false, columnDefinition = "timestamp default now()")
    private Date c_date;

    // 다중 파일명 저장
    @Column(name = "c_upload")
    private String c_upload;

    // 다중 파일 업로드
    @Transient
    private List<MultipartFile> c_uploadFiles;
    
    @Column(name = "reported_c_seq")
    private Integer reported_c_seq;

    @Column(name = "report_reason")
    private String report_reason;
    
    @Column(name = "reported", columnDefinition = "boolean default false")
    private Boolean reported;
    
    @Column(name = "processed", columnDefinition = "boolean default false", nullable = false)
    private Boolean processed = false;
    
    public Boolean isProcessed() {
    	return processed;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", referencedColumnName = "id", nullable = false)
    private Member member;
    
    @OneToMany(mappedBy = "community", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Likes> likes;
    
    @OneToMany(mappedBy = "community", cascade = CascadeType.REMOVE)
    private List<CommunityReply> replies;
    
    @Transient
    private int replyCount;
    
    public int getReplyCount() {
    	return this.replies.size();
    }
    
    public void likeChange(Integer c_like) {
    	this.c_like = c_like;
    }
    
    
   
}





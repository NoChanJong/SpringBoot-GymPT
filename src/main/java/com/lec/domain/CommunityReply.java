package com.lec.domain;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "c_reply")
public class CommunityReply {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cr_seq")
	private Integer crSeq;

	@Column(name = "cr_content", nullable = false)
	private String crContent;

	@Column(name = "cr_date", nullable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date crDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "c_seq")
	private Community community;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	
}

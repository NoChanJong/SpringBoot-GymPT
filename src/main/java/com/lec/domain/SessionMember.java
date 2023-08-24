package com.lec.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionMember extends Member implements Serializable {

	private String id;
	private String role;
	
	public SessionMember(Member member) {
		this.id = member.getId();
		this.role = member.getRole();
	}

}

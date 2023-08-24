package com.lec.persistence;

import java.util.List;

import com.lec.domain.Community;

public interface CustomCommunityRepository {

	List<Community> findByTitleOrContentContaining(String keyword);
	
}

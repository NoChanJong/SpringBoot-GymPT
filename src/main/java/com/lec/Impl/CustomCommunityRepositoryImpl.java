package com.lec.Impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.lec.domain.Community;
import com.lec.persistence.CustomCommunityRepository;

@Repository
public class CustomCommunityRepositoryImpl implements CustomCommunityRepository{

	@PersistenceContext
    private EntityManager entityManager;
	
	@Override
	public List<Community> findByTitleOrContentContaining(String keyword) {
        String query = "SELECT c FROM Community c WHERE c.c_title LIKE :keyword OR c.c_content LIKE :keyword";
        return entityManager.createQuery(query, Community.class)
                .setParameter("keyword", "%" + keyword + "%")
                .getResultList();
    }

}

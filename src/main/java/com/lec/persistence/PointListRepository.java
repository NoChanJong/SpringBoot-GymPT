package com.lec.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.lec.domain.PointList;

public interface PointListRepository extends CrudRepository<PointList, String>{
	
	@Transactional
	@Modifying
	@Query("SELECT p FROM PointList p ORDER BY p.sendDate DESC")
	List<PointList> findPointList();


	
	PointList findByListNo(Integer listNo);
}

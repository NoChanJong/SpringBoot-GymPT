package com.lec.persistence;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lec.domain.Inform;

@Repository
public interface InformRepository extends JpaRepository<Inform, Integer> {
    List<Inform> findAllByOrderByIDateDesc();
    Page<Inform> findByiTitleContaining(String title, Pageable pageable);
    Page<Inform> findByiContentContaining(String content, Pageable pageable);
}

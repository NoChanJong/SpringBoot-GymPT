package com.lec.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.lec.domain.News;

public interface NewsRepository extends CrudRepository<News, Long>{

    @Query("SELECT n FROM News n WHERE n.n_title LIKE %:n_title%")
    Page<News> findByN_titleContaining(@Param("n_title") String n_title, Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.n_content LIKE %:n_content%")
    Page<News> findByN_contentContaining(@Param("n_content") String n_content, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE News n SET n.n_cnt = n.n_cnt + 1 WHERE n.n_seq = :n_seq")
    int updateReadCount(@Param("n_seq") Long n_seq);

    List<News> findAll(Sort sort);
    // 뉴스 ID로 뉴스를 조회하는 메소드
    @Query("SELECT n FROM News n WHERE n.n_seq = :n_seq")
    News findByN_seq(@Param("n_seq") Long n_seq);
}
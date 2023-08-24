package com.lec.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lec.domain.News;

import net.bytebuddy.dynamic.DynamicType.Builder.FieldDefinition.Optional;

public interface NewsService {

   long getTotalRowCount(News news);
   News getNews(News news);
   Page<News> getNewsList(Pageable pageable, String searchType, String searchWord);
   void insertNews(News news);
   void updateNews(News news);
   void deletenews(News news);
   int updateNReadCount(News news);
   News findNewsById(Long n_seq);



}
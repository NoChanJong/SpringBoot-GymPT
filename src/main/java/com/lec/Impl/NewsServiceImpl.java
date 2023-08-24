package com.lec.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.lec.domain.News;
import com.lec.persistence.NewsRepository;
import com.lec.service.NewsService;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Override
    public News getNews(News news) {
      Optional<News> findNews = newsRepository.findById(news.getN_seq());
      if(findNews.isPresent())
         return findNews.get();
      else return null;
    }

    @Override
    public Page<News> getNewsList(Pageable pageable, String searchType, String searchWord) {
      if(searchType.equalsIgnoreCase("n_title")) {
         return newsRepository.findByN_titleContaining(searchWord, pageable);
      } else {
         return newsRepository.findByN_contentContaining(searchWord, pageable);
      }
    }

    @Override
    public void insertNews(News news) {
       newsRepository.save(news);
    }

    @Override
    public void updateNews(News news) {
        if (news.getN_seq() == null) {
            throw new IllegalArgumentException("The ID must not be null.");
        }

        Optional<News> optionalNews = newsRepository.findById(news.getN_seq());
        if (optionalNews.isPresent()) {
            News existingNews = optionalNews.get();
            existingNews.setN_title(news.getN_title());
            existingNews.setN_content(news.getN_content());

            // 기존 파일들과 새로 추가된 파일들을 모두 저장
            List<String> combinedUploadFiles = new ArrayList<>(existingNews.getN_uploadFiles());
            combinedUploadFiles.addAll(news.getN_uploadFiles());
            existingNews.setN_uploadFiles(combinedUploadFiles);

            newsRepository.save(existingNews);
        } else {
            throw new IllegalArgumentException("News not found with ID: " + news.getN_seq());
        }
    }


    @Override
    public News findNewsById(Long n_seq) {
        return newsRepository.findByN_seq(n_seq);
    }

    @Override
    public void deletenews(News news) {
       newsRepository.deleteById(news.getN_seq());
    }

    @Override
    public int updateNReadCount(News news) {
       return newsRepository.updateReadCount(news.getN_seq());
    }

   @Override
   public long getTotalRowCount(News news) {
      return newsRepository.count();
   }

}

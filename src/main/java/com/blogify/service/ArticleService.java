package com.blogify.service;

import com.blogify.entity.Article;
import com.blogify.exception.ApiException;
import com.blogify.payload.ArticleDto;
import com.blogify.repository.ArticleRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class ArticleService implements EntityService<ArticleDto> {

    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;

    @Override
    public ArticleDto create(ArticleDto newArticle) {
        validateArticle(null, newArticle.getTitle());

        Article savedArticle = articleRepository.save(mapToEntity(newArticle));
        newArticle.setId(savedArticle.getId());

        return newArticle;
    }

    @Override
    public ArticleDto update(Long id, ArticleDto newArticle) {
        validateArticle(id, newArticle.getTitle());

        articleRepository.save(mapToEntity(newArticle));
        newArticle.setId(id);

        return newArticle;
    }

    private void validateArticle(Long id, String title) {
        articleRepository.findByTitle(title).ifPresent(article -> {
           if(!article.getId().equals(id)) {
               throw new ApiException(HttpStatus.BAD_REQUEST, "Title already in use");
           }
        });

        if(id != null && !articleRepository.existsById(id)) {
            throw generateNotFound();
        }
    }

    @Override
    public List<ArticleDto> findAll() {
        return articleRepository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, ArticleDto.class)).toList();
    }

    private Article findByIdInternal(Long id) {
        return articleRepository.findById(id).orElseThrow(this::generateNotFound);
    }

    @Override
    public void deleteById(Long id) {
        Article article = findByIdInternal(id);
        articleRepository.delete(article);
    }

    @Override
    public ArticleDto findById(Long id) {
        Article article = findByIdInternal(id);
        return mapToDto(article);
    }

    private Article mapToEntity(ArticleDto dto) {
        return modelMapper.map(dto, Article.class);
    }

    private ArticleDto mapToDto(Article article) {
        return modelMapper.map(article, ArticleDto.class);
    }

    private ApiException generateNotFound() {
        return ApiException.notFound("Article not found");
    }
}

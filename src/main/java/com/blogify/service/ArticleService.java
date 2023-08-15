package com.blogify.service;

import com.blogify.entity.Article;
import com.blogify.exception.ApiException;
import com.blogify.payload.ArticleDto;
import com.blogify.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class ArticleService implements EntityService<ArticleDto> {

    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;

    @Override
    public ArticleDto create(ArticleDto newArticle) {
        Article savedArticle = articleRepository.save(mapToEntity(newArticle));
        newArticle.setId(savedArticle.getId());

        return newArticle;
    }

    @Override
    public ArticleDto update(Long id, ArticleDto newArticle) {
        validateArticleExist(id);

        newArticle.setId(id);
        articleRepository.save(mapToEntity(newArticle));

        return newArticle;
    }

    private void validateArticleExist(Long id) {
        if (!articleRepository.existsById(id)) {
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

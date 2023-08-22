package com.blogify.service;

import com.blogify.entity.Article;
import com.blogify.exception.ApiException;
import com.blogify.payload.ArticleDto;
import com.blogify.payload.ResponsePage;
import com.blogify.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class ArticleService implements EntityService<ArticleDto> {

    private static final int PAGE_SIZE = 10;

    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;

    @Override
    public ArticleDto create(ArticleDto newArticle) {
        validateArticle(null, newArticle.getTitle());
        save(newArticle);

        return newArticle;
    }

    @Override
    public ArticleDto update(Long id, ArticleDto newArticle) {
        validateArticle(id, newArticle.getTitle());
        save(newArticle);

        return newArticle;
    }

    private void save(ArticleDto newArticle) {
        Article savedArticle = articleRepository.save(mapToEntity(newArticle));
        newArticle.setId(savedArticle.getId());
    }

    private void validateArticle(Long id, String title) {
        articleRepository.findByTitle(title).ifPresent(article -> {
            if (!article.getId().equals(id)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Title already in use");
            }
        });

        if (id != null && !articleRepository.existsById(id)) {
            throw generateNotFound();
        }
    }

    @Override
    public ResponsePage<ArticleDto> findAll(Integer pageNum, Sort sort) {
        Page<Article> page = articleRepository.findAll(PageRequest.of(pageNum, PAGE_SIZE, sort));

        return ResponsePage.<ArticleDto>builder()
                .page(pageNum)
                .pageSize(PAGE_SIZE)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .content(page.getContent().stream().map(this::mapToDto).toList())
                .build();
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

package com.blogify.repository;

import com.blogify.entity.Article;
import com.blogify.entity.Category;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = "classpath:sql/categories.sql")
class CategoryRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ArticleRepository articleRepository;


    @Test
    void givenCategoryName_whenFindByName_thenCategoryIsReturned() {

        Optional<Category> category = categoryRepository.findByName("Technology");

        assertTrue(category.isPresent());
        assertEquals("Technology", category.get().getName());
    }

    @Test
    void givenNonExistingCategoryName_whenFindByName_thenCategoryIsNotReturned() {

        Optional<Category> category = categoryRepository.findByName("NonExistingCategory");

        assertTrue(category.isEmpty());
    }

    @Test
    void givenCategoryId_whenFindById_thenCategoryIsReturned() {

        Optional<Category> category = categoryRepository.findById(1L);

        assertTrue(category.isPresent());
        assertEquals(1L, category.get().getId());
        assertEquals("Technology", category.get().getName());
    }

    @Test
    void givenCategories_whenFindAll_thenAllCategoriesAreReturned() {

        List<Category> categories = categoryRepository.findAll();

        assertEquals(5, categories.size());
        assertThat(categories).extracting(Category::getName).contains("Technology", "Health",
                                                                      "Finance", "Education",
                                                                      "Lifestyle"
        );
    }

    @Test
    void givenCategoryId_whenDeleteById_thenCategoryIsDeleted() {

        categoryRepository.deleteById(1L);

        Optional<Category> category = categoryRepository.findById(1L);
        assertFalse(category.isPresent());
    }

    @Test
    void givenNewCategory_whenSave_thenCategoryIsSaved() {
        Category newCategory = new Category();
        newCategory.setName("NewCategory");

        Category savedCategory = categoryRepository.save(newCategory);

        assertNotNull(savedCategory.getId());
        assertEquals("NewCategory", savedCategory.getName());
    }

    @Test
    void givenCategoryId_whenExistsById_thenReturnTrue() {

        boolean exists = categoryRepository.existsById(1L);

        assertTrue(exists);
    }

    @Test
    void givenNonExistingCategoryId_whenExistsById_thenReturnFalse() {

        boolean exists = categoryRepository.existsById(99L);

        assertFalse(exists);
    }

    @Test
    void givenArticles_whenFindAll_thenAllArticlesAreReturned() {

        List<Article> articles = articleRepository.findAll();

        assertEquals(10, articles.size(), "There should be 10 articles");
        assertThat(articles).extracting(Article::getTitle).contains("Latest Tech Trends",
                                                                    "AI Innovations",
                                                                    "Healthy Living Tips",
                                                                    "Nutrition Basics",
                                                                    "Investing 101",
                                                                    "Saving for Retirement",
                                                                    "Online Learning Platforms",
                                                                    "Study Tips",
                                                                    "Travel on a Budget",
                                                                    "Minimalist Living"
        );
    }

    @Test
    void givenExistingArticle_whenSaveCategoryWithExistingArticle_thenCategoryIsSavedWithArticle() {
        Article existingArticle = articleRepository.findById(1L).orElseThrow();

        Category newCategory = new Category();
        newCategory.setName("NewCategory");
        newCategory.setArticles(List.of(existingArticle));
        Category savedCategory = categoryRepository.save(newCategory);

        assertNotNull(savedCategory.getId(), "Saved category should have an ID");
        assertThat(savedCategory.getArticles()).contains(existingArticle);
    }

    @Test
    void givenExistingArticleAssignedToCategory_whenDeleteCategory_thenArticleRemains() {
        Optional<Category> technology = categoryRepository.findById(1L);
        assertTrue(technology.isPresent());

        List<Article> articles = technology.get().getArticles();
        List<Long> articlesId = articles.stream().map(Article::getId).toList();

        categoryRepository.delete(technology.get());
        categoryRepository.flush();

        // Assert besides transaction
        entityManager.clear();

        articles = articleRepository.findAllById(articlesId);
        assertFalse(articles.isEmpty());
        assertEquals(2, articles.size());
        assertThat(articles).extracting(Article::getCategory).containsOnlyNulls();
    }
}

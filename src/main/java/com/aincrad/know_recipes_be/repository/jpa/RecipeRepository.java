package com.aincrad.know_recipes_be.repository.jpa;

import com.aincrad.know_recipes_be.repository.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    // Buscar por uniqueId
    Optional<Recipe> findByUniqueId(String uniqueId);

    // Receitas públicas com paginação
    Page<Recipe> findByIsPrivateFalseOrderByCreatedAtDesc(Pageable pageable);

    // Receitas de um usuário específico
    Page<Recipe> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Busca por título (case-insensitive) - apenas públicas
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND r.isPrivate = false ORDER BY r.createdAt DESC")
    Page<Recipe> searchPublicRecipesByTitle(@Param("keyword") String keyword, Pageable pageable);

    // Busca por título ou descrição (case-insensitive) - apenas públicas
    @Query("SELECT r FROM Recipe r WHERE (LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND r.isPrivate = false ORDER BY r.createdAt DESC")
    Page<Recipe> searchPublicRecipes(@Param("keyword") String keyword, Pageable pageable);

    // Buscar receitas de um usuário por título
    @Query("SELECT r FROM Recipe r WHERE r.user.id = :userId AND LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY r.createdAt DESC")
    Page<Recipe> searchUserRecipes(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    // Receitas recentes (últimas N receitas públicas)
    List<Recipe> findTop10ByIsPrivateFalseOrderByCreatedAtDesc();

    // Contar receitas de um usuário
    Long countByUserId(Long userId);

}

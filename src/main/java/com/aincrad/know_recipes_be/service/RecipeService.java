package com.aincrad.know_recipes_be.service;

import com.aincrad.know_recipes_be.dto.RecipeRequest;
import com.aincrad.know_recipes_be.dto.RecipeResponse;
import com.aincrad.know_recipes_be.repository.entity.Recipe;
import com.aincrad.know_recipes_be.repository.entity.User;
import com.aincrad.know_recipes_be.repository.jpa.RecipeRepository;
import com.aincrad.know_recipes_be.repository.jpa.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    /**
     * Cria uma nova receita
     */
    @Transactional
    public RecipeResponse createRecipe(RecipeRequest request) {
        User currentUser = getCurrentUser();

        Recipe recipe = new Recipe();
        recipe.setUniqueId(UUID.randomUUID().toString());
        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setImageUrl(request.getImageUrl());
        recipe.setIsPrivate(request.getIsPrivate() != null ? request.getIsPrivate() : false);
        recipe.setUser(currentUser);

        Recipe savedRecipe = recipeRepository.save(recipe);
        return mapToResponse(savedRecipe);
    }

    /**
     * Atualiza uma receita existente
     */
    @Transactional
    public RecipeResponse updateRecipe(Long recipeId, RecipeRequest request) {
        User currentUser = getCurrentUser();

        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new IllegalArgumentException("Receita não encontrada"));

        // Verificar se o usuário é o dono da receita
        if (!recipe.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Você não tem permissão para editar esta receita");
        }

        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setImageUrl(request.getImageUrl());
        recipe.setIsPrivate(request.getIsPrivate() != null ? request.getIsPrivate() : recipe.getIsPrivate());

        Recipe updatedRecipe = recipeRepository.save(recipe);
        return mapToResponse(updatedRecipe);
    }

    /**
     * Deleta uma receita
     */
    @Transactional
    public void deleteRecipe(Long recipeId) {
        User currentUser = getCurrentUser();

        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new IllegalArgumentException("Receita não encontrada"));

        if (!recipe.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Você não tem permissão para deletar esta receita");
        }

        recipeRepository.delete(recipe);
    }

    /**
     * Busca receita por ID (verifica permissões)
     */
    public RecipeResponse getRecipeById(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new IllegalArgumentException("Receita não encontrada"));

        // Se for privada, apenas o dono pode ver
        if (recipe.getIsPrivate()) {
            User currentUser = getCurrentUserOrNull();
            if (currentUser == null || !recipe.getUser().getId().equals(currentUser.getId())) {
                throw new IllegalArgumentException("Você não tem permissão para visualizar esta receita");
            }
        }

        return mapToResponse(recipe);
    }

    /**
     * Busca receita por uniqueId (público)
     */
    public RecipeResponse getRecipeByUniqueId(String uniqueId) {
        Recipe recipe = recipeRepository.findByUniqueId(uniqueId)
            .orElseThrow(() -> new IllegalArgumentException("Receita não encontrada"));

        if (recipe.getIsPrivate()) {
            User currentUser = getCurrentUserOrNull();
            if (currentUser == null || !recipe.getUser().getId().equals(currentUser.getId())) {
                throw new IllegalArgumentException("Receita privada");
            }
        }

        return mapToResponse(recipe);
    }

    /**
     * Lista todas as receitas públicas com paginação
     */
    public Page<RecipeResponse> getPublicRecipes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recipeRepository.findByIsPrivateFalseOrderByCreatedAtDesc(pageable)
            .map(this::mapToResponse);
    }

    /**
     * Lista receitas do usuário autenticado
     */
    public Page<RecipeResponse> getMyRecipes(int page, int size) {
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        return recipeRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId(), pageable)
            .map(this::mapToResponse);
    }

    /**
     * Busca receitas públicas por palavra-chave
     */
    public Page<RecipeResponse> searchPublicRecipes(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recipeRepository.searchPublicRecipes(keyword, pageable)
            .map(this::mapToResponse);
    }

    /**
     * Busca nas receitas do usuário
     */
    public Page<RecipeResponse> searchMyRecipes(String keyword, int page, int size) {
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        return recipeRepository.searchUserRecipes(currentUser.getId(), keyword, pageable)
            .map(this::mapToResponse);
    }

    /**
     * Obtém as 10 receitas mais recentes (feed principal)
     */
    public List<RecipeResponse> getRecentRecipes() {
        return recipeRepository.findTop10ByIsPrivateFalseOrderByCreatedAtDesc()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Estatísticas do usuário
     */
    public long getMyRecipeCount() {
        User currentUser = getCurrentUser();
        return recipeRepository.countByUserId(currentUser.getId());
    }

    // ===== MÉTODOS AUXILIARES =====

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    private User getCurrentUserOrNull() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }
            String email = authentication.getName();
            return userRepository.findByEmail(email).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private RecipeResponse mapToResponse(Recipe recipe) {
        return RecipeResponse.builder()
            .id(recipe.getId())
            .uniqueId(recipe.getUniqueId())
            .title(recipe.getTitle())
            .description(recipe.getDescription())
            .imageUrl(recipe.getImageUrl())
            .isPrivate(recipe.getIsPrivate())
            .createdAt(recipe.getCreatedAt())
            .updatedAt(recipe.getUpdatedAt())
            .authorUsername(recipe.getUser().getUsername())
            .authorId(recipe.getUser().getId())
            .build();
    }

}
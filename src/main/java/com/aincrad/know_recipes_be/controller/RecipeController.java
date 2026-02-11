package com.aincrad.know_recipes_be.controller;

import com.aincrad.know_recipes_be.dto.RecipeRequest;
import com.aincrad.know_recipes_be.dto.RecipeResponse;
import com.aincrad.know_recipes_be.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    /**
     * POST /api/recipes
     * Cria uma nova receita (requer autenticação)
     */
    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(@Valid @RequestBody RecipeRequest request) {
        try {
            RecipeResponse response = recipeService.createRecipe(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/recipes/{id}
     * Atualiza uma receita existente (requer autenticação e ser o autor)
     */
    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponse> updateRecipe(
        @PathVariable Long id,
        @Valid @RequestBody RecipeRequest request) {
        try {
            RecipeResponse response = recipeService.updateRecipe(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * DELETE /api/recipes/{id}
     * Deleta uma receita (requer autenticação e ser o autor)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        try {
            recipeService.deleteRecipe(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * GET /api/recipes/{id}
     * Busca receita por ID
     */
    @GetMapping("/public/{id}")
    public ResponseEntity<RecipeResponse> getRecipeById(@PathVariable Long id) {
        try {
            RecipeResponse response = recipeService.getRecipeById(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/recipes/public/feed
     * Feed principal - 10 receitas mais recentes (público)
     */
    @GetMapping("/public/feed")
    public ResponseEntity<List<RecipeResponse>> getRecentRecipes() {
        List<RecipeResponse> recipes = recipeService.getRecentRecipes();
        return ResponseEntity.ok(recipes);
    }

    /**
     * GET /api/recipes/public
     * Lista todas as receitas públicas com paginação
     * Query params: page (default 0), size (default 20)
     */
    @GetMapping("/public")
    public ResponseEntity<Page<RecipeResponse>> getPublicRecipes(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        Page<RecipeResponse> recipes = recipeService.getPublicRecipes(page, size);
        return ResponseEntity.ok(recipes);
    }

    /**
     * GET /api/recipes/my
     * Lista receitas do usuário autenticado
     * Query params: page (default 0), size (default 20)
     */
    @GetMapping("/my")
    public ResponseEntity<Page<RecipeResponse>> getMyRecipes(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        try {
            Page<RecipeResponse> recipes = recipeService.getMyRecipes(page, size);
            return ResponseEntity.ok(recipes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * GET /api/recipes/search/public
     * Busca receitas públicas por palavra-chave
     * Query params: q (keyword), page (default 0), size (default 20)
     */
    @GetMapping("/search/public")
    public ResponseEntity<Page<RecipeResponse>> searchPublicRecipes(
        @RequestParam String q,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        Page<RecipeResponse> recipes = recipeService.searchPublicRecipes(q, page, size);
        return ResponseEntity.ok(recipes);
    }

    /**
     * GET /api/recipes/search/my
     * Busca nas receitas do usuário autenticado
     * Query params: q (keyword), page (default 0), size (default 20)
     */
    @GetMapping("/search/my")
    public ResponseEntity<Page<RecipeResponse>> searchMyRecipes(
        @RequestParam String q,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        try {
            Page<RecipeResponse> recipes = recipeService.searchMyRecipes(q, page, size);
            return ResponseEntity.ok(recipes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * GET /api/recipes/public/unique/{uniqueId}
     * Busca receita por uniqueId (compartilhamento)
     */
    @GetMapping("/public/unique/{uniqueId}")
    public ResponseEntity<RecipeResponse> getRecipeByUniqueId(@PathVariable String uniqueId) {
        try {
            RecipeResponse response = recipeService.getRecipeByUniqueId(uniqueId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/recipes/my/stats
     * Retorna estatísticas do usuário
     */
    @GetMapping("/my/stats")
    public ResponseEntity<Map<String, Object>> getMyStats() {
        try {
            long count = recipeService.getMyRecipeCount();
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalRecipes", count);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
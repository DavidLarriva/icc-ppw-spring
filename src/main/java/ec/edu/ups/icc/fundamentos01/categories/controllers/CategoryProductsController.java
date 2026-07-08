package ec.edu.ups.icc.fundamentos01.categories.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.icc.fundamentos01.categories.dtos.ProductFilterByCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.services.CategoryService;
import ec.edu.ups.icc.fundamentos01.core.dtos.PaginationDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;

/*
 * Controlador REST encargado de exponer consultas relacionadas
 * entre categorías y productos.
 *
 * La ruta pertenece al contexto semántico de categorías:
 * /categories/{id}/products
 *
 * Categoría es la tabla dominante: se consulta "los productos de esta
 * categoría", igual que se hizo con /users/{id}/products.
 */
@RestController
@RequestMapping("/categories")
public class CategoryProductsController {

    private final CategoryService categoryService;

    public CategoryProductsController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /*
     * Endpoint para consultar los productos de una categoría, con filtros opcionales.
     *
     * GET /api/categories/{id}/products
     * GET /api/categories/{id}/products?name=laptop
     * GET /api/categories/{id}/products?minPrice=500&maxPrice=1500
     * GET /api/categories/{id}/products?userId=1
     */
    @GetMapping("/{id}/products")
    public List<ProductResponseDto> findProductsByCategory(
            @PathVariable Long id,
            @Valid @ModelAttribute ProductFilterByCategoryDto filters
    ) {
        return categoryService.findProductsByCategory(id, filters);
    }

    /*
     * Versión paginada con Page de los productos de una categoría.
     *
     * GET /api/categories/{id}/products/page
     * GET /api/categories/{id}/products/page?page=0&size=5
     * GET /api/categories/{id}/products/page?name=laptop&minPrice=500&page=0&size=5
     */
    @GetMapping("/{id}/products/page")
    public Page<ProductResponseDto> findProductsByCategoryPage(
            @PathVariable Long id,
            @Valid @ModelAttribute ProductFilterByCategoryDto filters,
            @Valid @ModelAttribute PaginationDto pagination
    ) {
        return categoryService.findProductsByCategoryPage(id, filters, pagination);
    }

    /*
     * Versión paginada con Slice de los productos de una categoría.
     *
     * GET /api/categories/{id}/products/slice
     * GET /api/categories/{id}/products/slice?page=0&size=5
     */
    @GetMapping("/{id}/products/slice")
    public Slice<ProductResponseDto> findProductsByCategorySlice(
            @PathVariable Long id,
            @Valid @ModelAttribute ProductFilterByCategoryDto filters,
            @Valid @ModelAttribute PaginationDto pagination
    ) {
        return categoryService.findProductsByCategorySlice(id, filters, pagination);
    }
}

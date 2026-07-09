package ec.edu.ups.icc.fundamentos01.products.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.icc.fundamentos01.core.dtos.PaginationDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.services.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /*
     * Endpoint normal. Se mantiene sin paginación para comparar con los paginados.
     *
     * GET /api/products
     *
     * Solo ADMIN: expone todos los productos de todos los usuarios sin filtrar,
     * a diferencia de /page, /slice y /user/{userId} que sí puede usar cualquiera.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductResponseDto> findAll() {
        return productService.findAll();
    }

    /*
     * Endpoint paginado usando Page (incluye totalElements, totalPages, etc.).
     *
     * GET /api/products/page
     * GET /api/products/page?page=0&size=5
     * GET /api/products/page?page=0&size=5&sortBy=price&direction=desc
     */
    @GetMapping("/page")
    public Page<ProductResponseDto> findAllPage(@Valid @ModelAttribute PaginationDto pagination) {
        return productService.findAllPage(pagination);
    }

    /*
     * Endpoint paginado usando Slice (más liviano, sin COUNT).
     *
     * GET /api/products/slice
     * GET /api/products/slice?page=0&size=5&sortBy=createdAt&direction=desc
     */
    @GetMapping("/slice")
    public Slice<ProductResponseDto> findAllSlice(@Valid @ModelAttribute PaginationDto pagination) {
        return productService.findAllSlice(pagination);
    }

    @GetMapping("/{id}")
    public ProductResponseDto findOne(@PathVariable Long id) {
        return productService.findOne(id);
    }

    @PostMapping
    public ProductResponseDto create(@Valid @RequestBody CreateProductDto dto) {
        return productService.create(dto);
    }

    @PutMapping("/{id}")
    public ProductResponseDto update(@PathVariable Long id, @Valid @RequestBody UpdateProductDto dto) {
        return productService.update(id, dto);
    }

    @PatchMapping("/{id}")
    public ProductResponseDto partialUpdate(@PathVariable Long id, @Valid @RequestBody PartialUpdateProductDto dto) {
        return productService.partialUpdate(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    /*
     * Endpoint para buscar productos por id de usuario.
     *
     * GET /products/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public List<ProductResponseDto> findByUserId(@PathVariable Long userId) {
        return productService.findByUserId(userId);
    }

    /*
     * Endpoint para buscar productos por id de categoría.
     *
     * GET /products/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public List<ProductResponseDto> findByCategoryId(@PathVariable Long categoryId) {
        return productService.findByCategoryId(categoryId);
    }
}

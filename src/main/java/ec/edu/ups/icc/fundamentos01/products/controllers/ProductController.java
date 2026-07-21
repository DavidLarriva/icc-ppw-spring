package ec.edu.ups.icc.fundamentos01.products.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "productos", description = "gestión de productos con paginación, roles y ownership")
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
    @Operation(summary = "listar todos los productos (solo admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "lista completa de productos de todos los usuarios"),
            @ApiResponse(responseCode = "403", description = "el usuario autenticado no tiene rol admin")
    })
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
    @Operation(summary = "listar productos paginados (page, con totales)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "página de productos con totalElements y totalPages"),
            @ApiResponse(responseCode = "400", description = "parámetros de paginación inválidos")
    })
    @GetMapping("/page")
    public Page<ProductResponseDto> findAllPage(@Valid @ModelAttribute PaginationDto pagination) {
        return productService.findAllPage(pagination);
    }

    /*
     * Endpoint paginado usando Slice (más liviano, sin COUNT).
     *
     * Cualquier usuario autenticado puede usarlo, pero solo ve SUS propios
     * productos: el filtro por owner se resuelve en el repositorio a partir
     * del usuario del token, no de un parámetro que mande el cliente.
     *
     * GET /api/products/slice
     * GET /api/products/slice?page=0&size=5&sortBy=createdAt&direction=desc
     */
    @Operation(summary = "listar mis productos paginados (slice, sin totales)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "página de productos propios del usuario autenticado"),
            @ApiResponse(responseCode = "400", description = "parámetros de paginación inválidos")
    })
    @GetMapping("/slice")
    public Slice<ProductResponseDto> findAllSlice(
            @Valid @ModelAttribute PaginationDto pagination,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return productService.findAllSlice(pagination, currentUser);
    }

    @Operation(summary = "obtener un producto por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "producto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "producto no encontrado o eliminado")
    })
    @GetMapping("/{id}")
    public ProductResponseDto findOne(@PathVariable Long id) {
        return productService.findOne(id);
    }

    /*
     * El owner ya no se toma del body: se obtiene del usuario autenticado
     * mediante @AuthenticationPrincipal, así nadie puede crear productos
     * a nombre de otro usuario.
     */
    @Operation(summary = "crear un producto (el dueño es el usuario autenticado)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "producto creado",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "una o más categorías no existen"),
            @ApiResponse(responseCode = "409", description = "ya existe un producto activo con ese nombre")
    })
    @PostMapping
    public ProductResponseDto create(
            @Valid @RequestBody CreateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return productService.create(dto, currentUser);
    }

    /*
     * Ownership: solo el dueño del producto o un ROLE_ADMIN pueden actualizarlo.
     * La validación ocurre en el servicio (ver ProductServiceImpl.validateOwnership).
     */
    @Operation(summary = "actualizar un producto completo (solo el dueño o un admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "producto actualizado",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "datos de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "el producto no te pertenece"),
            @ApiResponse(responseCode = "404", description = "producto o categoría no encontrados")
    })
    @PutMapping("/{id}")
    public ProductResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return productService.update(id, dto, currentUser);
    }

    @Operation(summary = "actualizar un producto parcialmente (solo el dueño o un admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "producto actualizado",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "datos de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "el producto no te pertenece"),
            @ApiResponse(responseCode = "404", description = "producto o categoría no encontrados")
    })
    @PatchMapping("/{id}")
    public ProductResponseDto partialUpdate(
            @PathVariable Long id,
            @Valid @RequestBody PartialUpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return productService.partialUpdate(id, dto, currentUser);
    }

    @Operation(summary = "eliminar un producto (solo el dueño o un admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "producto eliminado (borrado lógico)"),
            @ApiResponse(responseCode = "403", description = "el producto no te pertenece"),
            @ApiResponse(responseCode = "404", description = "producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        productService.delete(id, currentUser);
    }

    /*
     * Endpoint para buscar productos por id de usuario.
     *
     * GET /products/user/{userId}
     */
    @Operation(summary = "listar los productos de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "productos activos del usuario indicado"),
            @ApiResponse(responseCode = "404", description = "usuario no encontrado")
    })
    @GetMapping("/user/{userId}")
    public List<ProductResponseDto> findByUserId(@PathVariable Long userId) {
        return productService.findByUserId(userId);
    }

    /*
     * Endpoint para buscar productos por id de categoría.
     *
     * GET /products/category/{categoryId}
     */
    @Operation(summary = "listar los productos de una categoría")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "productos activos de la categoría indicada"),
            @ApiResponse(responseCode = "404", description = "categoría no encontrada")
    })
    @GetMapping("/category/{categoryId}")
    public List<ProductResponseDto> findByCategoryId(@PathVariable Long categoryId) {
        return productService.findByCategoryId(categoryId);
    }
}

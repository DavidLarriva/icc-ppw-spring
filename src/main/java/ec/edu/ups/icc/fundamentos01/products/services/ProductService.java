package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import ec.edu.ups.icc.fundamentos01.core.dtos.PaginationDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;

/*
 * Contrato del servicio de productos.
 * Aquí va la lógica de negocio; el controlador solo delega en estos métodos.
 */
public interface ProductService {

    List<ProductResponseDto> findAll();

    ProductResponseDto findOne(Long id);

    /*
     * Crea un producto usando como owner al usuario autenticado (currentUser),
     * nunca un userId recibido en el body.
     */
    ProductResponseDto create(CreateProductDto dto, UserDetailsImpl currentUser);

    /*
     * Actualiza completamente un producto. Se valida ownership en el servicio:
     * solo el dueño o un ROLE_ADMIN pueden modificarlo.
     */
    ProductResponseDto update(Long id, UpdateProductDto dto, UserDetailsImpl currentUser);

    /*
     * Actualiza parcialmente un producto. Se valida ownership en el servicio.
     */
    ProductResponseDto partialUpdate(Long id, PartialUpdateProductDto dto, UserDetailsImpl currentUser);

    /*
     * Elimina lógicamente un producto. Se valida ownership en el servicio.
     */
    void delete(Long id, UserDetailsImpl currentUser);

    List<ProductResponseDto> findByUserId(Long userId);

    List<ProductResponseDto> findByCategoryId(Long categoryId);

    /*
     * Retorna productos activos usando Page (incluye metadatos completos).
     */
    Page<ProductResponseDto> findAllPage(PaginationDto pagination);

    /*
     * Retorna productos activos usando Slice (más liviano, sin COUNT).
     */
    Slice<ProductResponseDto> findAllSlice(PaginationDto pagination);
}

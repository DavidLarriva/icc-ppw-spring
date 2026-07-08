package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import ec.edu.ups.icc.fundamentos01.core.dtos.PaginationDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;

/*
 * Contrato del servicio de productos.
 * Aquí va la lógica de negocio; el controlador solo delega en estos métodos.
 */
public interface ProductService {

    List<ProductResponseDto> findAll();

    ProductResponseDto findOne(Long id);

    ProductResponseDto create(CreateProductDto dto);

    ProductResponseDto update(Long id, UpdateProductDto dto);

    ProductResponseDto partialUpdate(Long id, PartialUpdateProductDto dto);

    void delete(Long id);

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

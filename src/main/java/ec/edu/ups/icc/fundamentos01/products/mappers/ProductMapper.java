package ec.edu.ups.icc.fundamentos01.products.mappers;

import java.time.LocalDateTime;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;

/*
 * Convierte entre los DTOs y el modelo de producto.
 * Asigna la fecha de creación al crear el producto.
 */
public class ProductMapper {

    // De CreateProductDto a ProductModel (el id lo asigna el controlador)
    public ProductModel toModel(CreateProductDto dto) {
        ProductModel product = new ProductModel();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCreatedAt(LocalDateTime.now());
        return product;
    }

    // De ProductModel a la respuesta pública
    public ProductResponseDto toResponse(ProductModel product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock());
    }
}

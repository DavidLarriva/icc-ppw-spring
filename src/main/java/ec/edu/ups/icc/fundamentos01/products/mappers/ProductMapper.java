package ec.edu.ups.icc.fundamentos01.products.mappers;

import java.time.LocalDateTime;

import ec.edu.ups.icc.fundamentos01.categories.dtos.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.categories.entities.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;

/*
 * Convierte entre los DTOs, el modelo y la entidad de producto.
 */
public class ProductMapper {

    // De CreateProductDto a ProductModel (el id lo asigna la base de datos)
    public static ProductModel toModelFromDTO(CreateProductDto dto) {
        ProductModel product = new ProductModel();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCreatedAt(LocalDateTime.now());
        return product;
    }

    // De ProductEntity (devuelta por el repositorio) a ProductModel
    public static ProductModel toModelFromEntity(ProductEntity entity) {
        ProductModel product = new ProductModel();
        product.setId(entity.getId());
        product.setName(entity.getName());
        product.setPrice(entity.getPrice());
        product.setStock(entity.getStock());
        product.setCreatedAt(entity.getCreatedAt());
        product.setUpdatedAt(entity.getUpdatedAt());
        product.setDeleted(entity.isDeleted());

        UserEntity owner = entity.getOwner();
        product.setOwnerId(owner.getId());
        product.setOwnerName(owner.getName());
        product.setOwnerEmail(owner.getEmail());

        CategoryEntity category = entity.getCategory();
        product.setCategoryId(category.getId());
        product.setCategoryName(category.getName());
        product.setCategoryDescription(category.getDescription());

        return product;
    }

    // De ProductModel a ProductEntity, antes de guardar en la base de datos
    public static ProductEntity toEntityFromModel(ProductModel model) {
        ProductEntity entity = new ProductEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setPrice(model.getPrice());
        entity.setStock(model.getStock());
        return entity;
    }

    // De ProductModel a la respuesta pública, con owner y category anidados
    public static ProductResponseDto toResponse(ProductModel product) {
        ProductResponseDto dto = new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock());

        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

         dto.setOwner(new UserResponseDto(product.getOwnerId(), product.getOwnerName(), product.getOwnerEmail()));
         dto.setCategory(new CategoryResponseDto(product.getCategoryId(), product.getCategoryName(),
                product.getCategoryDescription()));

        return dto;
    }
}

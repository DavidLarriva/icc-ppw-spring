package ec.edu.ups.icc.fundamentos01.categories.mappers;

import java.time.LocalDateTime;

import ec.edu.ups.icc.fundamentos01.categories.dtos.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.categories.dtos.CreateCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.entities.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.categories.models.CategoryModel;

/*
 * Convierte entre los DTOs, el modelo y la entidad de categoría.
 */
public class CategoryMapper {

    // De CreateCategoryDto a CategoryModel (el id lo asigna la base de datos)
    public static CategoryModel toModelFromDTO(CreateCategoryDto dto) {
        CategoryModel category = new CategoryModel();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setCreatedAt(LocalDateTime.now());
        return category;
    }

    // De CategoryEntity (devuelta por el repositorio) a CategoryModel
    public static CategoryModel toModelFromEntity(CategoryEntity entity) {
        CategoryModel category = new CategoryModel();
        category.setId(entity.getId());
        category.setName(entity.getName());
        category.setDescription(entity.getDescription());
        category.setCreatedAt(entity.getCreatedAt());
        category.setUpdatedAt(entity.getUpdatedAt());
        category.setDeleted(entity.isDeleted());
        return category;
    }

    // De CategoryModel a CategoryEntity, antes de guardar en la base de datos
    public static CategoryEntity toEntityFromModel(CategoryModel model) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setDescription(model.getDescription());
        return entity;
    }

    // De CategoryModel a la respuesta pública
    public static CategoryResponseDto toResponse(CategoryModel category) {
        return new CategoryResponseDto(category.getId(), category.getName(), category.getDescription());
    }
}

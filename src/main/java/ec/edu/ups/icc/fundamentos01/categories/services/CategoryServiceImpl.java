package ec.edu.ups.icc.fundamentos01.categories.services;

import java.util.List;

import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.categories.dtos.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.categories.dtos.CreateCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.dtos.UpdateCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.entities.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.categories.mappers.CategoryMapper;
import ec.edu.ups.icc.fundamentos01.categories.models.CategoryModel;
import ec.edu.ups.icc.fundamentos01.categories.repositories.CategoryRepository;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.NotFoundException;

/*
 * Implementación del servicio de categorías.
 *
 * Usa CategoryRepository para persistir datos en PostgreSQL.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryResponseDto> findAll() {
        return categoryRepository.findAll()
                .stream()
                .filter(entity -> !entity.isDeleted())
                .map(CategoryMapper::toModelFromEntity)
                .map(CategoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponseDto findOne(Long id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (entity.isDeleted()) {
            throw new NotFoundException("Category not found");
        }

        CategoryModel model = CategoryMapper.toModelFromEntity(entity);
        return CategoryMapper.toResponse(model);
    }

    @Override
    public CategoryResponseDto create(CreateCategoryDto dto) {
        if (categoryRepository.findByNameIgnoreCaseAndDeletedFalse(dto.getName()).isPresent()) {
            throw new ConflictException("Category name already registered");
        }

        CategoryModel model = CategoryMapper.toModelFromDTO(dto);
        CategoryEntity entity = CategoryMapper.toEntityFromModel(model);
        CategoryEntity savedEntity = categoryRepository.save(entity);
        CategoryModel savedModel = CategoryMapper.toModelFromEntity(savedEntity);
        return CategoryMapper.toResponse(savedModel);
    }

    @Override
    public CategoryResponseDto update(Long id, UpdateCategoryDto dto) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (entity.isDeleted()) {
            throw new NotFoundException("Category not found");
        }

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());

        CategoryEntity savedEntity = categoryRepository.save(entity);
        CategoryModel model = CategoryMapper.toModelFromEntity(savedEntity);
        return CategoryMapper.toResponse(model);
    }

    @Override
    public void delete(Long id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (entity.isDeleted()) {
            throw new NotFoundException("Category not found");
        }

        entity.setDeleted(true);
        categoryRepository.save(entity);
    }
}

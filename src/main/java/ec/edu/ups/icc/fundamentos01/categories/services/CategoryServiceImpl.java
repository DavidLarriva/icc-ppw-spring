package ec.edu.ups.icc.fundamentos01.categories.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.ups.icc.fundamentos01.categories.dtos.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.categories.dtos.CreateCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.dtos.ProductFilterByCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.dtos.UpdateCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.entities.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.categories.mappers.CategoryMapper;
import ec.edu.ups.icc.fundamentos01.categories.models.CategoryModel;
import ec.edu.ups.icc.fundamentos01.categories.repositories.CategoryRepository;
import ec.edu.ups.icc.fundamentos01.core.dtos.PaginationDto;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.BadRequestException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.core.pagination.PageableFactory;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;
import ec.edu.ups.icc.fundamentos01.products.services.ProductServiceImpl;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.repositories.UserRepository;

/*
 * Implementación del servicio de categorías.
 *
 * Usa CategoryRepository para persistir datos en PostgreSQL.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
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

    /*
     * Retorna los productos activos de una categoría, aplicando filtros opcionales.
     *
     * Primero valida que la categoría exista y no esté eliminada.
     * Luego valida que el rango de precio sea coherente (minPrice <= maxPrice)
     * y, si llega userId, que ese usuario exista y no esté eliminado.
     */
    @Override
    public List<ProductResponseDto> findProductsByCategory(Long categoryId, ProductFilterByCategoryDto filters) {
        validateCategoryContext(categoryId, filters);

        String name = normalizeName(filters.getName());

        return productRepository.findByCategoryIdWithFilters(
                        categoryId,
                        name,
                        filters.getMinPrice(),
                        filters.getMaxPrice(),
                        filters.getUserId())
                .stream()
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }

    /*
     * Productos de una categoría con filtros, usando Page (con metadatos completos).
     *
     * Reutiliza la misma validación y los mismos filtros; solo agrega paginación.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findProductsByCategoryPage(
            Long categoryId, ProductFilterByCategoryDto filters, PaginationDto pagination) {
        validateCategoryContext(categoryId, filters);

        String name = normalizeName(filters.getName());
        Pageable pageable = PageableFactory.build(pagination, ProductServiceImpl.ALLOWED_SORT_FIELDS);

        return productRepository.findByCategoryIdWithFiltersPage(
                        categoryId,
                        name,
                        filters.getMinPrice(),
                        filters.getMaxPrice(),
                        filters.getUserId(),
                        pageable)
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse);
    }

    /*
     * Productos de una categoría con filtros, usando Slice (más liviano, sin COUNT).
     */
    @Override
    @Transactional(readOnly = true)
    public Slice<ProductResponseDto> findProductsByCategorySlice(
            Long categoryId, ProductFilterByCategoryDto filters, PaginationDto pagination) {
        validateCategoryContext(categoryId, filters);

        String name = normalizeName(filters.getName());
        Pageable pageable = PageableFactory.build(pagination, ProductServiceImpl.ALLOWED_SORT_FIELDS);

        return productRepository.findByCategoryIdWithFiltersSlice(
                        categoryId,
                        name,
                        filters.getMinPrice(),
                        filters.getMaxPrice(),
                        filters.getUserId(),
                        pageable)
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse);
    }

    /*
     * Validación común para las 3 consultas de productos por categoría:
     * la categoría existe y no está eliminada, el rango de precio es coherente
     * y, si llega userId como filtro, ese usuario existe y no está eliminado.
     */
    private void validateCategoryContext(Long categoryId, ProductFilterByCategoryDto filters) {
        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (category.isDeleted()) {
            throw new NotFoundException("Category not found");
        }

        if (!filters.hasValidPriceRange()) {
            throw new BadRequestException("El precio máximo debe ser mayor o igual al precio mínimo");
        }

        if (filters.getUserId() != null) {
            UserEntity user = userRepository.findById(filters.getUserId())
                    .orElseThrow(() -> new NotFoundException("User not found"));

            if (user.isDeleted()) {
                throw new NotFoundException("User not found");
            }
        }
    }

    /*
     * Convierte un texto vacío en null para que el repositorio ignore el filtro por nombre.
     */
    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return name.trim();
    }
}

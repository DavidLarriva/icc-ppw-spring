package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.ups.icc.fundamentos01.categories.entities.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.categories.repositories.CategoryRepository;
import ec.edu.ups.icc.fundamentos01.core.dtos.PaginationDto;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.BadRequestException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.core.pagination.PageableFactory;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.repositories.UserRepository;

/*
 * Implementación del servicio de productos.
 *
 * Reemplaza la lista en memoria por ProductRepository.
 * El repositorio se encarga de comunicarse con PostgreSQL mediante JPA.
 *
 * Gestiona además las relaciones hacia UserEntity (owner) y CategoryEntity.
 */
@Service
public class ProductServiceImpl implements ProductService {

    /*
     * Lista blanca de campos por los que sí se puede ordenar en la paginación.
     * Solo campos directos de ProductEntity (no relaciones como owner o categories).
     */
    public static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "name", "price", "stock", "createdAt", "updatedAt");

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(
            ProductRepository productRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /*
     * Retorna todos los productos activos almacenados en PostgreSQL.
     */
    @Override
    public List<ProductResponseDto> findAll() {
        return productRepository.findAll()
                .stream()
                .filter(entity -> !entity.isDeleted())
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }

    /*
     * Busca un producto activo por id.
     *
     * Si no existe o está eliminado, lanza NotFoundException.
     */
    @Override
    public ProductResponseDto findOne(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (entity.isDeleted()) {
            throw new NotFoundException("Product not found");
        }

        ProductModel model = ProductMapper.toModelFromEntity(entity);
        return ProductMapper.toResponse(model);
    }

    /*
     * Crea un nuevo producto: DTO -> Model -> Entity -> guarda -> Model -> Response DTO.
     *
     * El owner se obtiene del usuario autenticado (currentUser), nunca del body,
     * para evitar que un usuario cree productos a nombre de otro.
     * Valida que todas las categorías existan y que no exista ya un producto
     * activo con el mismo nombre.
     */
    @Override
    public ProductResponseDto create(CreateProductDto dto, UserDetailsImpl currentUser) {
        UserEntity owner = findCurrentUserEntity(currentUser);

        Set<CategoryEntity> categories = validateAndGetCategories(dto.getCategoryIds());

        if (productRepository.findByNameAndDeletedFalse(dto.getName()).isPresent()) {
            throw new ConflictException("Product name already registered");
        }

        ProductModel model = ProductMapper.toModelFromDTO(dto);
        ProductEntity entity = ProductMapper.toEntityFromModel(model);
        entity.setOwner(owner);
        entity.setCategories(categories);

        ProductEntity savedEntity = productRepository.save(entity);
        ProductModel savedModel = ProductMapper.toModelFromEntity(savedEntity);
        return ProductMapper.toResponse(savedModel);
    }

    /*
     * Actualiza completamente un producto activo.
     *
     * Solo el dueño del producto o un ROLE_ADMIN pueden ejecutar esta operación.
     * No permite cambiar el usuario propietario.
     * Sí permite cambiar las categorías (reemplaza todas las asociadas).
     * Si el producto no existe o está eliminado, lanza NotFoundException.
     */
    @Override
    public ProductResponseDto update(Long id, UpdateProductDto dto, UserDetailsImpl currentUser) {
        ProductEntity entity = findActiveProductOrThrow(id);

        validateOwnership(entity, currentUser);

        Set<CategoryEntity> categories = validateAndGetCategories(dto.getCategoryIds());

        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        entity.setCategories(categories);

        ProductEntity savedEntity = productRepository.save(entity);
        ProductModel model = ProductMapper.toModelFromEntity(savedEntity);
        return ProductMapper.toResponse(model);
    }

    /*
     * Actualiza parcialmente un producto activo: solo los campos enviados en el DTO.
     *
     * Solo el dueño del producto o un ROLE_ADMIN pueden ejecutar esta operación.
     * Si llegan categoryIds, valida que todas las categorías existan y
     * reemplaza el conjunto completo de categorías asociadas.
     * Si el producto no existe o está eliminado, lanza NotFoundException.
     */
    @Override
    public ProductResponseDto partialUpdate(Long id, PartialUpdateProductDto dto, UserDetailsImpl currentUser) {
        ProductEntity entity = findActiveProductOrThrow(id);

        validateOwnership(entity, currentUser);

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getPrice() != null) {
            entity.setPrice(dto.getPrice());
        }
        if (dto.getStock() != null) {
            entity.setStock(dto.getStock());
        }
        if (dto.getCategoryIds() != null) {
            Set<CategoryEntity> categories = validateAndGetCategories(dto.getCategoryIds());
            entity.setCategories(categories);
        }

        ProductEntity savedEntity = productRepository.save(entity);
        ProductModel model = ProductMapper.toModelFromEntity(savedEntity);
        return ProductMapper.toResponse(model);
    }

    /*
     * Elimina lógicamente un producto activo: marca deleted = true sin borrar el registro.
     *
     * Solo el dueño del producto o un ROLE_ADMIN pueden ejecutar esta operación.
     * Si no existe o ya está eliminado, lanza NotFoundException.
     */
    @Override
    public void delete(Long id, UserDetailsImpl currentUser) {
        ProductEntity entity = findActiveProductOrThrow(id);

        validateOwnership(entity, currentUser);

        entity.setDeleted(true);
        productRepository.save(entity);
    }

    /*
     * Retorna los productos activos creados por un usuario.
     *
     * Primero valida que el usuario exista y no esté eliminado.
     */
    @Override
    public List<ProductResponseDto> findByUserId(Long userId) {
        UserEntity owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (owner.isDeleted()) {
            throw new NotFoundException("User not found");
        }

        return productRepository.findByOwner_IdAndDeletedFalse(userId)
                .stream()
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }

    /*
     * Retorna los productos activos asociados a una categoría.
     *
     * Primero valida que la categoría exista y no esté eliminada.
     */
    @Override
    public List<ProductResponseDto> findByCategoryId(Long categoryId) {
        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (category.isDeleted()) {
            throw new NotFoundException("Category not found");
        }

        return productRepository.findByCategoryIdWithFilters(categoryId, null, null, null, null)
                .stream()
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }

    /*
     * Retorna productos activos usando Page.
     *
     * Incluye metadatos completos: totalElements, totalPages, number, size, first, last.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findAllPage(PaginationDto pagination) {
        Pageable pageable = PageableFactory.build(pagination, ALLOWED_SORT_FIELDS);

        return productRepository.findActivePage(pageable)
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse);
    }

    /*
     * Retorna, usando Slice, solo los productos activos del usuario autenticado.
     *
     * El filtro por owner va en la consulta del repositorio (findActiveSliceByOwnerId),
     * no acá: así el LIMIT/OFFSET de la paginación se aplica ya sobre las filas de
     * ese usuario, en vez de traer todos los productos a memoria y filtrar en Java.
     * No incluye totalElements ni totalPages; es más liviano porque no ejecuta COUNT.
     */
    @Override
    @Transactional(readOnly = true)
    public Slice<ProductResponseDto> findAllSlice(PaginationDto pagination, UserDetailsImpl currentUser) {
        Pageable pageable = PageableFactory.build(pagination, ALLOWED_SORT_FIELDS);
        UserEntity owner = findCurrentUserEntity(currentUser);

        return productRepository.findActiveSliceByOwnerId(owner.getId(), pageable)
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse);
    }

    /*
     * Busca un producto activo por id.
     *
     * Si no existe o está eliminado, lanza NotFoundException.
     */
    private ProductEntity findActiveProductOrThrow(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (entity.isDeleted()) {
            throw new NotFoundException("Product not found");
        }

        return entity;
    }

    /*
     * Obtiene el usuario autenticado como entidad JPA.
     *
     * currentUser viene del token JWT. Se vuelve a consultar en base para
     * asegurar que el usuario siga existiendo y no esté eliminado lógicamente.
     */
    private UserEntity findCurrentUserEntity(UserDetailsImpl currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        return userRepository.findByIdAndDeletedFalse(currentUser.getId())
                .orElseThrow(() -> new AccessDeniedException("Usuario no autorizado"));
    }

    /*
     * Valida si el usuario autenticado puede modificar o eliminar el producto.
     *
     * ROLE_ADMIN puede modificar cualquier producto.
     * ROLE_USER solo puede modificar productos propios.
     */
    private void validateOwnership(ProductEntity product, UserDetailsImpl currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        if (hasRole(currentUser, "ROLE_ADMIN")) {
            return;
        }

        if (product.getOwner() == null || product.getOwner().getId() == null) {
            throw new AccessDeniedException("El producto no tiene propietario válido");
        }

        if (!product.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("No puedes modificar productos ajenos");
        }
    }

    /*
     * Verifica si el usuario autenticado tiene un rol específico
     * (por ejemplo, "ROLE_ADMIN").
     */
    private boolean hasRole(UserDetailsImpl user, String role) {
        return user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(role));
    }

    /*
     * Valida que todas las categorías existan y estén activas.
     *
     * Retorna el conjunto de entidades CategoryEntity que se asociarán al producto.
     */
    private Set<CategoryEntity> validateAndGetCategories(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new BadRequestException("Debe seleccionar al menos una categoría");
        }

        Set<CategoryEntity> categories = new HashSet<>();

        for (Long categoryId : categoryIds) {
            CategoryEntity category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category not found"));

            if (category.isDeleted()) {
                throw new NotFoundException("Category not found");
            }

            categories.add(category);
        }

        return categories;
    }
}

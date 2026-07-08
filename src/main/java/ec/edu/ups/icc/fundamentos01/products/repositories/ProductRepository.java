package ec.edu.ups.icc.fundamentos01.products.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;

/*
 * Repositorio encargado de gestionar la persistencia
 * de productos usando Spring Data JPA.
 *
 * Incluye consultas usando las relaciones con UserEntity y CategoryEntity.
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByNameAndDeletedFalse(String name);

    List<ProductEntity> findByOwner_IdAndDeletedFalse(Long ownerId);

    /*
     * Busca productos activos de un usuario aplicando filtros opcionales.
     *
     * Si name/minPrice/maxPrice llegan como null, ese filtro no se aplica.
     * name hace coincidencia parcial (LIKE %name%) sin distinguir mayúsculas/minúsculas.
     */
    @Query("""
            SELECT p
            FROM ProductEntity p
            WHERE p.deleted = false
              AND p.owner.id = :userId
              AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            """)
    List<ProductEntity> findByOwnerIdWithFilters(
            @Param("userId") Long userId,
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );

    /*
     * Busca productos activos de una categoría aplicando filtros opcionales.
     *
     * La categoría se consulta a través de la tabla intermedia product_categories.
     * Se usa DISTINCT porque el JOIN con p.categories puede repetir el mismo
     * producto si coincide con más de una fila relacionada.
     */
    @Query("""
            SELECT DISTINCT p
            FROM ProductEntity p
            JOIN p.categories c
            WHERE p.deleted = false
              AND c.id = :categoryId
              AND c.deleted = false
              AND p.owner.deleted = false
              AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
              AND (:userId IS NULL OR p.owner.id = :userId)
            """)
    List<ProductEntity> findByCategoryIdWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("userId") Long userId
    );

    /*
     * Consulta productos activos usando Page.
     *
     * Page ejecuta la consulta de datos (con LIMIT/OFFSET) y una consulta COUNT
     * aparte para saber el total de registros.
     */
    @Query(
            value = """
                    SELECT p
                    FROM ProductEntity p
                    WHERE p.deleted = false
                    """,
            countQuery = """
                    SELECT COUNT(p)
                    FROM ProductEntity p
                    WHERE p.deleted = false
                    """
    )
    Page<ProductEntity> findActivePage(Pageable pageable);

    /*
     * Consulta productos activos usando Slice.
     *
     * Slice no ejecuta COUNT: solo sabe si hay página siguiente.
     */
    @Query("""
            SELECT p
            FROM ProductEntity p
            WHERE p.deleted = false
            """)
    Slice<ProductEntity> findActiveSlice(Pageable pageable);

    /*
     * Productos activos de una categoría con filtros opcionales, usando Page.
     *
     * Misma consulta que findByCategoryIdWithFilters pero paginada.
     * El countQuery usa COUNT(DISTINCT p) por el JOIN con las categorías.
     */
    @Query(
            value = """
                    SELECT DISTINCT p
                    FROM ProductEntity p
                    JOIN p.categories c
                    WHERE p.deleted = false
                      AND c.id = :categoryId
                      AND c.deleted = false
                      AND p.owner.deleted = false
                      AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')))
                      AND (:minPrice IS NULL OR p.price >= :minPrice)
                      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
                      AND (:userId IS NULL OR p.owner.id = :userId)
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT p)
                    FROM ProductEntity p
                    JOIN p.categories c
                    WHERE p.deleted = false
                      AND c.id = :categoryId
                      AND c.deleted = false
                      AND p.owner.deleted = false
                      AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')))
                      AND (:minPrice IS NULL OR p.price >= :minPrice)
                      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
                      AND (:userId IS NULL OR p.owner.id = :userId)
                    """
    )
    Page<ProductEntity> findByCategoryIdWithFiltersPage(
            @Param("categoryId") Long categoryId,
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("userId") Long userId,
            Pageable pageable
    );

    /*
     * Productos activos de una categoría con filtros opcionales, usando Slice.
     *
     * No ejecuta COUNT: más liviano para scroll o navegación secuencial.
     */
    @Query("""
            SELECT DISTINCT p
            FROM ProductEntity p
            JOIN p.categories c
            WHERE p.deleted = false
              AND c.id = :categoryId
              AND c.deleted = false
              AND p.owner.deleted = false
              AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
              AND (:userId IS NULL OR p.owner.id = :userId)
            """)
    Slice<ProductEntity> findByCategoryIdWithFiltersSlice(
            @Param("categoryId") Long categoryId,
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("userId") Long userId,
            Pageable pageable
    );
}

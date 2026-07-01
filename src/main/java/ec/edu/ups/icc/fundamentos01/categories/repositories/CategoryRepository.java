package ec.edu.ups.icc.fundamentos01.categories.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.edu.ups.icc.fundamentos01.categories.entities.CategoryEntity;

/*
 * Repositorio encargado de gestionar la persistencia
 * de categorías usando Spring Data JPA.
 */
@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<CategoryEntity> findByNameIgnoreCaseAndDeletedFalse(String name);
}

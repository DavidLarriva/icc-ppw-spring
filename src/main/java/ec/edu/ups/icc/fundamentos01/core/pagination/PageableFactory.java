package ec.edu.ups.icc.fundamentos01.core.pagination;

import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import ec.edu.ups.icc.fundamentos01.core.dtos.PaginationDto;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.BadRequestException;

/*
 * Construye un objeto Pageable a partir de un PaginationDto, validando
 * el campo de ordenamiento (lista blanca) y la dirección.
 *
 * Es reutilizable: tanto la paginación de productos como la de productos
 * por categoría usan la misma lógica sin duplicarla.
 */
public class PageableFactory {

    /*
     * Construye el Pageable validando página, tamaño, campo de ordenamiento y dirección.
     *
     * allowedFields es la lista blanca de campos por los que sí se puede ordenar.
     */
    public static Pageable build(PaginationDto pagination, Set<String> allowedFields) {
        String sortBy = normalizeSortBy(pagination.getSortBy(), allowedFields);
        Sort.Direction direction = normalizeDirection(pagination.getDirection());
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(pagination.getPage(), pagination.getSize(), sort);
    }

    /*
     * Valida que el campo de ordenamiento exista y esté permitido.
     *
     * Se usa lista blanca para evitar ordenar por campos inexistentes
     * o por relaciones complejas no preparadas para esta práctica.
     */
    private static String normalizeSortBy(String sortBy, Set<String> allowedFields) {
        if (sortBy == null || sortBy.isBlank()) {
            return "id";
        }

        if (!allowedFields.contains(sortBy)) {
            throw new BadRequestException("Campo de ordenamiento no permitido: " + sortBy);
        }

        return sortBy;
    }

    /*
     * Convierte la dirección recibida por query param en Sort.Direction.
     */
    private static Sort.Direction normalizeDirection(String direction) {
        if (direction == null || direction.isBlank()) {
            return Sort.Direction.ASC;
        }

        if (direction.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        }

        if (direction.equalsIgnoreCase("desc")) {
            return Sort.Direction.DESC;
        }

        throw new BadRequestException("Dirección de ordenamiento no válida: " + direction);
    }
}

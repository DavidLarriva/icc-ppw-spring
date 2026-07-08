package ec.edu.ups.icc.fundamentos01.categories.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

/*
 * DTO utilizado para recibir filtros opcionales al consultar
 * los productos de una categoría.
 *
 * La categoría ya viene en la ruta, por eso este filtro no tiene categoryId.
 * Sus campos llegan desde query params, por ejemplo:
 * /api/categories/1/products?name=laptop&minPrice=500&userId=2
 */
public class ProductFilterByCategoryDto {

    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    private String name;

    @DecimalMin(value = "0.0", inclusive = true, message = "El precio mínimo no puede ser negativo")
    private Double minPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "El precio máximo no puede ser negativo")
    private Double maxPrice;

    private Long userId;

    public ProductFilterByCategoryDto() {
    }

    public ProductFilterByCategoryDto(String name, Double minPrice, Double maxPrice, Long userId) {
        this.name = name;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.userId = userId;
    }

    /*
     * Valida que el rango de precios sea coherente.
     *
     * Si ambos valores llegan, maxPrice debe ser mayor o igual a minPrice.
     */
    public boolean hasValidPriceRange() {
        if (minPrice != null && maxPrice != null) {
            return maxPrice >= minPrice;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

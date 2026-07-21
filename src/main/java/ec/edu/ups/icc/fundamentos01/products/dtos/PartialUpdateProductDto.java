package ec.edu.ups.icc.fundamentos01.products.dtos;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/*
 * DTO utilizado para recibir del cliente los datos de un producto
 * en una actualización parcial (PATCH).
 *
 * Todos los campos son opcionales (tipos envoltorio para permitir null):
 * solo se actualizan los que lleguen con valor.
 * Solo se validan los campos enviados.
 * No incluye id (se toma de la ruta).
 */
@Schema(description = "Datos a actualizar de un producto (solo se aplican los campos enviados)")
public class PartialUpdateProductDto {

    @Schema(
            description = "Nombre del producto",
            example = "Laptop Lenovo ThinkPad"
    )
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String name;

    @Schema(
            description = "Precio del producto",
            example = "899.99"
    )
    @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
    private Double price;

    @Schema(
            description = "Cantidad disponible en inventario",
            example = "10"
    )
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    private Integer stock;

    @Schema(
            description = "Ids de las categorías a las que pertenece el producto",
            example = "[1, 2]"
    )
    private Set<Long> categoryIds;

    public PartialUpdateProductDto() {
    }

    public PartialUpdateProductDto(String name, Double price, Integer stock, Set<Long> categoryIds) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryIds = categoryIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Set<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(Set<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
}

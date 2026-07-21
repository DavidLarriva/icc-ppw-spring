package ec.edu.ups.icc.fundamentos01.products.dtos;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/*
 * DTO utilizado para recibir del cliente los datos de un producto
 * en una actualización completa (PUT).
 *
 * Reemplaza todos los campos editables del producto, incluidas las categorías.
 * No permite cambiar el usuario propietario.
 * No incluye id (se toma de la ruta).
 * No incluye createdAt.
 */
@Schema(description = "Datos para reemplazar por completo un producto existente")
public class UpdateProductDto {

    @Schema(
            description = "Nombre del producto",
            example = "Laptop Lenovo ThinkPad"
    )
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String name;

    @Schema(
            description = "Precio del producto",
            example = "899.99"
    )
    @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
    private double price;

    @Schema(
            description = "Cantidad disponible en inventario",
            example = "10"
    )
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    private int stock;

    @Schema(
            description = "Ids de las categorías a las que pertenece el producto",
            example = "[1, 2]"
    )
    @NotEmpty(message = "Debe seleccionar al menos una categoría")
    private Set<Long> categoryIds;

    public UpdateProductDto() {
    }

    public UpdateProductDto(String name, double price, int stock, Set<Long> categoryIds) {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Set<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(Set<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
}

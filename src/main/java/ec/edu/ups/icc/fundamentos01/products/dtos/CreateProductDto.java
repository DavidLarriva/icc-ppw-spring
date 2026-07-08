package ec.edu.ups.icc.fundamentos01.products.dtos;

import java.util.Set;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/*
 * DTO utilizado para recibir del cliente los datos necesarios
 * para crear un nuevo producto.
 *
 * Incluye userId y categoryIds porque el producto debe relacionarse
 * con un usuario y con al menos una categoría existentes.
 * No incluye id (lo genera el servidor).
 * No incluye createdAt (lo asigna el servidor).
 */
public class CreateProductDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String name;

    @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
    private double price;

    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    private int stock;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @NotEmpty(message = "Debe seleccionar al menos una categoría")
    private Set<Long> categoryIds;

    public CreateProductDto() {
    }

    public CreateProductDto(String name, double price, int stock, Long userId, Set<Long> categoryIds) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.userId = userId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Set<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(Set<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
}

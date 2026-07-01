package ec.edu.ups.icc.fundamentos01.products.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/*
 * DTO utilizado para recibir del cliente los datos de un producto
 * en una actualización completa (PUT).
 *
 * Reemplaza todos los campos editables del producto, incluida la categoría.
 * No permite cambiar el usuario propietario.
 * No incluye id (se toma de la ruta).
 * No incluye createdAt.
 */
public class UpdateProductDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String name;

    @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
    private double price;

    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    private int stock;

    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long categoryId;

    public UpdateProductDto() {
    }

    public UpdateProductDto(String name, double price, int stock, Long categoryId) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}

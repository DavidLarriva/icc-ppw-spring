package ec.edu.ups.icc.fundamentos01.products.dtos;

import java.time.LocalDateTime;
import java.util.List;

import ec.edu.ups.icc.fundamentos01.categories.dtos.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;

/*
 * DTO utilizado para devolver al cliente los datos públicos
 * de un producto como respuesta de la API.
 *
 * Incluye los datos resumidos del usuario propietario (owner)
 * y de las categorías, como objetos anidados.
 */
@Schema(description = "Datos públicos de un producto devueltos por la API")
public class ProductResponseDto {

    @Schema(description = "Id del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Laptop Lenovo ThinkPad")
    private String name;

    @Schema(description = "Precio del producto", example = "899.99")
    private double price;

    @Schema(description = "Cantidad disponible en inventario", example = "10")
    private int stock;

    @Schema(description = "Usuario propietario del producto")
    private UserResponseDto owner;

    @Schema(description = "Categorías a las que pertenece el producto")
    private List<CategoryResponseDto> categories;

    @Schema(description = "Fecha y hora en que se creó el producto", example = "2026-07-16T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha y hora de la última actualización del producto", example = "2026-07-16T12:00:00")
    private LocalDateTime updatedAt;

    public ProductResponseDto() {
    }

    public ProductResponseDto(Long id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UserResponseDto getOwner() {
        return owner;
    }

    public void setOwner(UserResponseDto owner) {
        this.owner = owner;
    }

    public List<CategoryResponseDto> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryResponseDto> categories) {
        this.categories = categories;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

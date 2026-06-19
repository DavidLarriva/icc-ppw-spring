package ec.edu.ups.icc.fundamentos01.products.dtos;

/*
 * DTO utilizado para recibir del cliente los datos de un producto
 * en una actualización completa (PUT).
 *
 * Reemplaza todos los campos editables del producto.
 * No incluye id (se toma de la ruta).
 * No incluye createdAt.
 */
public class UpdateProductDto {

    private String name;
    private double price;
    private int stock;

    public UpdateProductDto() {
    }

    public UpdateProductDto(String name, double price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
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
}

package ec.edu.ups.icc.fundamentos01.products.dtos;

/*
 * DTO utilizado para recibir del cliente los datos necesarios
 * para crear un nuevo producto.
 *
 * No incluye id (lo genera el servidor).
 * No incluye createdAt (lo asigna el servidor).
 */
public class CreateProductDto {

    private String name;
    private double price;
    private int stock;

    public CreateProductDto() {
    }

    public CreateProductDto(String name, double price, int stock) {
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

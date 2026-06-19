package ec.edu.ups.icc.fundamentos01.products.dtos;

/*
 * DTO utilizado para devolver al cliente los datos públicos
 * de un producto como respuesta de la API.
 */
public class ProductResponseDto {

    private Long id;
    private String name;
    private double price;
    private int stock;

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
}

package ec.edu.ups.icc.fundamentos01.categories.dtos;

/*
 * DTO utilizado para devolver al cliente los datos públicos
 * de una categoría como respuesta de la API.
 */
public class CategoryResponseDto {

    private Long id;
    private String name;
    private String description;

    public CategoryResponseDto() {
    }

    public CategoryResponseDto(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

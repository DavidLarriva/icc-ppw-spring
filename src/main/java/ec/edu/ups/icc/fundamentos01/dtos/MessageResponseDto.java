package ec.edu.ups.icc.fundamentos01.dtos;

/*
 * DTO genérico para devolver un mensaje simple,
 * por ejemplo al eliminar un recurso correctamente.
 */
public class MessageResponseDto {

    private String message;

    public MessageResponseDto() {
    }

    public MessageResponseDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

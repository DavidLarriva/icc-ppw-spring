package ec.edu.ups.icc.fundamentos01.dtos;

/*
 * DTO genérico para devolver un mensaje de error,
 * por ejemplo cuando no se encuentra un recurso.
 */
public class ErrorResponseDto {

    private String message;

    public ErrorResponseDto() {
    }

    public ErrorResponseDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

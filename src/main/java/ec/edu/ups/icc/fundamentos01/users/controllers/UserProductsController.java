package ec.edu.ups.icc.fundamentos01.users.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.ProductFilterDto;
import ec.edu.ups.icc.fundamentos01.users.services.UserService;

/*
 * Controlador REST encargado de exponer consultas relacionadas
 * entre usuarios y productos.
 *
 * La ruta pertenece al contexto semántico de usuarios:
 * /users/{id}/products
 *
 * Usuarios es la tabla dominante: se consulta "los productos de este usuario",
 * no "productos filtrados por un userId".
 */
@RestController
@RequestMapping("/users")
public class UserProductsController {

    private final UserService userService;

    public UserProductsController(UserService userService) {
        this.userService = userService;
    }

    /*
     * Endpoint para consultar los productos de un usuario, con filtros opcionales.
     *
     * GET /api/users/{id}/products
     * GET /api/users/{id}/products?name=laptop
     * GET /api/users/{id}/products?minPrice=500&maxPrice=1500
     * GET /api/users/{id}/products?name=laptop&minPrice=500&maxPrice=1500
     */
    @GetMapping("/{id}/products")
    public List<ProductResponseDto> findProductsByUser(
            @PathVariable Long id,
            @Valid @ModelAttribute ProductFilterDto filters
    ) {
        return userService.findProductsByUser(id, filters);
    }
}

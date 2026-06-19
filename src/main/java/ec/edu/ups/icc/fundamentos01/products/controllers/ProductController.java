package ec.edu.ups.icc.fundamentos01.products.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final List<ProductModel> products = new ArrayList<>();
    private final ProductMapper productMapper = new ProductMapper();
    private long currentId = 1;

    // GET /products - lista todos los productos
    @GetMapping
    public List<ProductResponseDto> getAll() {
        return products.stream().map(productMapper::toResponse).toList();
    }

    // GET /products/{id} - un producto por id
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id) {
        ProductModel product = findById(id);
        if (product == null) {
            return notFound(id);
        }
        return ResponseEntity.ok(productMapper.toResponse(product));
    }

    // POST /products - crea un producto (el id se genera automáticamente)
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody CreateProductDto dto) {
        ProductModel product = productMapper.toModel(dto);
        product.setId(currentId++);
        products.add(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productMapper.toResponse(product));
    }

    // PUT /products/{id} - reemplaza los datos del producto
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable long id, @RequestBody UpdateProductDto dto) {
        ProductModel product = findById(id);
        if (product == null) {
            return notFound(id);
        }
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        return ResponseEntity.ok(productMapper.toResponse(product));
    }

    // PATCH /products/{id} - actualiza solo los campos que llegan
    @PatchMapping("/{id}")
    public ResponseEntity<Object> partialUpdate(@PathVariable long id, @RequestBody PartialUpdateProductDto dto) {
        ProductModel product = findById(id);
        if (product == null) {
            return notFound(id);
        }
        if (dto.getName() != null) {
            product.setName(dto.getName());
        }
        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }
        if (dto.getStock() != null) {
            product.setStock(dto.getStock());
        }
        return ResponseEntity.ok(productMapper.toResponse(product));
    }

    // DELETE /products/{id} - elimina un producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        ProductModel product = findById(id);
        if (product == null) {
            return notFound(id);
        }
        products.remove(product);
        return ResponseEntity.noContent().build();
    }

    private ProductModel findById(long id) {
        return products.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    private ResponseEntity<Object> notFound(long id) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Producto no encontrado con id " + id));
    }
}

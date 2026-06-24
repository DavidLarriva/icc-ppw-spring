package ec.edu.ups.icc.fundamentos01.products.controllers;

import java.util.List;

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
import ec.edu.ups.icc.fundamentos01.products.services.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponseDto> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public Object findOne(@PathVariable long id) {
        return productService.findOne(id);
    }

    @PostMapping
    public ProductResponseDto create(@RequestBody CreateProductDto dto) {
        return productService.create(dto);
    }

    @PutMapping("/{id}")
    public Object update(@PathVariable long id, @RequestBody UpdateProductDto dto) {
        return productService.update(id, dto);
    }

    @PatchMapping("/{id}")
    public Object partialUpdate(@PathVariable long id, @RequestBody PartialUpdateProductDto dto) {
        return productService.partialUpdate(id, dto);
    }

    @DeleteMapping("/{id}")
    public Object delete(@PathVariable long id) {
        return productService.delete(id);
    }
}

package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.dtos.ErrorResponseDto;
import ec.edu.ups.icc.fundamentos01.dtos.MessageResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;

/*
 * Implementación del servicio de productos.
 * Marcada con @Service para que Spring la administre e inyecte.
 * Guarda los productos en memoria mientras la aplicación está encendida.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final List<ProductModel> products = new ArrayList<>();
    private long currentId = 1;

    @Override
    public List<ProductResponseDto> findAll() {
        return products.stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    public Object findOne(long id) {
        ProductModel product = findById(id);
        if (product == null) {
            return new ErrorResponseDto("Producto no encontrado con id " + id);
        }
        return ProductMapper.toResponse(product);
    }

    @Override
    public ProductResponseDto create(CreateProductDto dto) {
        ProductModel product = ProductMapper.toModel(dto);
        product.setId(currentId++);
        products.add(product);
        return ProductMapper.toResponse(product);
    }

    @Override
    public Object update(long id, UpdateProductDto dto) {
        ProductModel product = findById(id);
        if (product == null) {
            return new ErrorResponseDto("Producto no encontrado con id " + id);
        }
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        return ProductMapper.toResponse(product);
    }

    @Override
    public Object partialUpdate(long id, PartialUpdateProductDto dto) {
        ProductModel product = findById(id);
        if (product == null) {
            return new ErrorResponseDto("Producto no encontrado con id " + id);
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
        return ProductMapper.toResponse(product);
    }

    @Override
    public Object delete(long id) {
        boolean removed = products.removeIf(product -> product.getId() == id);
        if (!removed) {
            return new ErrorResponseDto("Producto no encontrado con id " + id);
        }
        return new MessageResponseDto("Producto eliminado correctamente");
    }

    private ProductModel findById(long id) {
        return products.stream()
                .filter(product -> product.getId() == id)
                .findFirst()
                .orElse(null);
    }
}

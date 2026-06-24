package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;

/*
 * Contrato del servicio de productos.
 * Aquí va la lógica de negocio; el controlador solo delega en estos métodos.
 */
public interface ProductService {

    List<ProductResponseDto> findAll();

    Object findOne(long id);

    ProductResponseDto create(CreateProductDto dto);

    Object update(long id, UpdateProductDto dto);

    Object partialUpdate(long id, PartialUpdateProductDto dto);

    Object delete(long id);
}

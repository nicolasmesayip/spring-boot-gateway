package com.nicolasmesa.springboot.products.mapper;

import com.nicolasmesa.springboot.products.dto.ProductDTO;
import com.nicolasmesa.springboot.products.entity.Product;

import java.util.List;

public class ProductMapper {
    public static Product convertToEntity(ProductDTO product) {
        return new Product(
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                product.getCurrency(),
                product.getStockAvailable(),
                product.isAvailable()
        );
    }

    public static ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setCategory(product.getCategory());
        productDTO.setPrice(product.getPrice());
        productDTO.setCurrency(product.getCurrency());
        productDTO.setStockAvailable(product.getStockAvailable());
        productDTO.setAvailable(product.isAvailable());
        return productDTO;
    }

    public static List<ProductDTO> convertToDTOList(List<Product> products) {
        return products.stream().map(ProductMapper::convertToDTO).toList();
    }
}

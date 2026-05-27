package ru.yandex.practicum.shoppingstore.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interactionapi.dto.store.ProductDto;
import ru.yandex.practicum.shoppingstore.model.Product;

@Component
public class ProductMapper {

    public ProductDto toDto(Product product) {
        return new ProductDto(
                product.getProductId(),
                product.getProductName(),
                product.getDescription(),
                product.getImageSrc(),
                product.getQuantityState(),
                product.getProductState(),
                product.getProductCategory(),
                product.getPrice()
        );
    }

    public Product toEntity(ProductDto dto) {
        Product product = new Product();

        product.setProductId(dto.productId());
        product.setProductName(dto.productName());
        product.setDescription(dto.description());
        product.setImageSrc(dto.imageSrc());
        product.setQuantityState(dto.quantityState());
        product.setProductState(dto.productState());
        product.setProductCategory(dto.productCategory());
        product.setPrice(dto.price());

        return product;
    }
}
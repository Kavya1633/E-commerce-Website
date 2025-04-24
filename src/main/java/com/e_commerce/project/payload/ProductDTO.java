package com.e_commerce.project.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long productId;
    @NotBlank
    @Size(min = 3, message = "Product name must be of atleast 3 characters.")
    private String productName;
    private String categoryName;
    @NotBlank
    @Size(min = 5, message = "Product name must be of atleast 5 characters.")
    private String description;
    private String image;
    private double price;
    private double discount;
    private double specialPrice;
    private Integer Quantity;


}

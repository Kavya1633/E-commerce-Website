package com.e_commerce.project.payload;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CartDTO {

    private Long cartId;
    private Double totalPrice=0.0;
    private List<ProductDTO> products=new ArrayList<>();


}

package com.e_commerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private List<ProductDTO> content;

    private  Integer PageNumber;
    private  Integer PageSize;
    private  Long totalElements;
    private  Integer totalPages;
    private  boolean lastPage;

}

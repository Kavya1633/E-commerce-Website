package com.e_commerce.project.payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long categoryId;
    @NotBlank
    @Size(min=3,message="CategoryName must contain atleast 3 characters")
    private String categoryname;
}

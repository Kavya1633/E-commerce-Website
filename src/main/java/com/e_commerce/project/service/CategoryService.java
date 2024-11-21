package com.e_commerce.project.service;

import com.e_commerce.project.model.Category;
import com.e_commerce.project.payload.CategoryDTO;
import com.e_commerce.project.payload.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse getAllcategories(Integer pageNumber, Integer pageSize,String sortBy, String sortOrder);
    CategoryDTO addCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO UpdateCategory(CategoryDTO categoryDTO, Long categoryId);

}

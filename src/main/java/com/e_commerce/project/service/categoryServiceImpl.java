package com.e_commerce.project.service;

import com.e_commerce.project.exceptions.APIExceptions;
import com.e_commerce.project.exceptions.ResourceNotFoundException;
import com.e_commerce.project.model.Category;
import com.e_commerce.project.payload.CategoryDTO;
import com.e_commerce.project.payload.CategoryResponse;
import com.e_commerce.project.repositories.CategoryRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class categoryServiceImpl implements CategoryService{
    @Autowired
    public CategoryRepository categoryRepository;
    @Autowired
    public ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllcategories(Integer pageNumber, Integer pageSize,String sortBy, String sortOrder) {
        // Sorting
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        // pagination
        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category> categoryPage=categoryRepository.findAll(pageDetails);

        List<Category> allcategories= categoryPage.getContent();
        if(allcategories.isEmpty()){
            throw new APIExceptions("No Category has been added!!");
        }
        List<CategoryDTO>categoryDTOS=allcategories.stream()
                .map(category -> modelMapper.map(category,CategoryDTO.class))
                .toList();

        CategoryResponse categoryResponse=new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }
    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        Category newCategory=modelMapper.map(categoryDTO,Category.class);

        Category existCategory=categoryRepository.findByCategoryname(newCategory.getCategoryname());
        if(existCategory!=null){
            throw new APIExceptions("Category with CategoryName "+categoryDTO.getCategoryname()+" already exists!!!");
        }
        CategoryDTO savedCategoryDTO=modelMapper.map(categoryRepository.save(newCategory),CategoryDTO.class);
        return savedCategoryDTO;
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category","categoryID",categoryId));

        categoryRepository.delete(category);
        return modelMapper.map(category,CategoryDTO.class);
    }

    @Override
    public CategoryDTO UpdateCategory(CategoryDTO categoryDTO, Long categoryId) {

        Category savedcategory=categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category","categoryID",categoryId));

        Category category=modelMapper.map(categoryDTO,Category.class);

        savedcategory.setCategoryname(category.getCategoryname());
        Category updatedCategory=categoryRepository.save(savedcategory);
        return modelMapper.map(updatedCategory,CategoryDTO.class);



    }
}

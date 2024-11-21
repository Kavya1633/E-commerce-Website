package com.e_commerce.project.repositories;

import com.e_commerce.project.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    Category findByCategoryname(String categoryname);
    // func name should be convention and attribute name is used with 1st letter caps
}

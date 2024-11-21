package com.e_commerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity(name = "categories")   // class --> table in database
@Data  // contains getter, setter and requiredArgs constructor and tostring()
@NoArgsConstructor

public class Category {
    @Id  // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @NotBlank
    @Size(min=3,message="CategoryName must contain atleast 3 characters")
    private String categoryname;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL)
    private List<Product> products;
}


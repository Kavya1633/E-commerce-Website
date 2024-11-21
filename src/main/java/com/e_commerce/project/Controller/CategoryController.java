package com.e_commerce.project.Controller;

import com.e_commerce.project.Configuration.appConstants;
import com.e_commerce.project.payload.CategoryDTO;
import com.e_commerce.project.payload.CategoryResponse;
import com.e_commerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api")  // mapping at the class level
public class CategoryController {
    @Autowired
    private CategoryService Categoryservice_obj;

//    @GetMapping("/echo")
//    public ResponseEntity<String>echo(@RequestParam(name="message") String message ){
//        return new ResponseEntity<>("Echo message : "+message,HttpStatus.OK);
//    }

    @GetMapping("/public/category")
    // replacement of any mapping
//    @RequestMapping(value = "/public/category", method = RequestMethod.GET)
    public ResponseEntity<CategoryResponse> getAllcategories(@RequestParam(name="pageNumber",defaultValue=appConstants.Page_Number,required = false) Integer pageNumber,
                                                             @RequestParam(name="pageSize",defaultValue = appConstants.Page_Size,required = false) Integer pageSize,
                                                             @RequestParam(name="sortBy",defaultValue = appConstants.Sort_Categories_By,required = false) String sortBy,
                                                             @RequestParam(name="sortOrder",defaultValue = appConstants.Sort_dir,required = false) String sortOrder)
    {
       CategoryResponse categoryResponse= Categoryservice_obj.getAllcategories(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(categoryResponse,HttpStatus.OK);
    }
    @PostMapping("/public/category")
    public ResponseEntity<CategoryDTO> addCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategoryDTO=Categoryservice_obj.addCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO,HttpStatus.CREATED);

    }

    @DeleteMapping("/admin/category/{categoryId}")
   public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){
        CategoryDTO deletedCategoryDTO=Categoryservice_obj.deleteCategory(categoryId);
        return new ResponseEntity<>(deletedCategoryDTO, HttpStatus.OK);
//            return ResponseEntity.ok(status);
//            return ResponseEntity.status(HttpStatus.OK).body(status);

   }
   @PutMapping("/public/category/{categoryId}")
   public ResponseEntity<CategoryDTO> UpdateCategory( @Valid @RequestBody CategoryDTO categoryDTO, @PathVariable Long categoryId){
        CategoryDTO updatedCategoryDTO=Categoryservice_obj.UpdateCategory(categoryDTO,categoryId);

        return new ResponseEntity<>(updatedCategoryDTO,HttpStatus.OK);
   }

}

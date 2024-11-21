package com.e_commerce.project.Controller;

import com.e_commerce.project.Configuration.appConstants;
import com.e_commerce.project.model.Product;
import com.e_commerce.project.payload.ProductDTO;
import com.e_commerce.project.payload.ProductResponse;
import com.e_commerce.project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId){
        ProductDTO savedproductDTO=productService.addProduct(productDTO,categoryId);
        return new ResponseEntity<>(savedproductDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(name = "PageNumber",defaultValue = appConstants.Page_Number,required = false )Integer PageNumber,
                                                          @RequestParam(name = "PageSize",defaultValue = appConstants.Page_Size,required = false)Integer PageSize,
                                                          @RequestParam(name = "SortBy",defaultValue = appConstants.Sort_Products_By,required = false) String SortBy,
                                                          @RequestParam(name = "SortOrder",defaultValue = appConstants.Sort_dir,required = false)String SortOrder){
        ProductResponse productResponse=productService.getAllProducts(PageNumber,PageSize,SortBy,SortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @GetMapping("/public/category/{categoryId}/products")
    public ResponseEntity<ProductResponse> getAllProductsbyCategory(@PathVariable Long categoryId,
                                                                    @RequestParam(name = "PageNumber",defaultValue = appConstants.Page_Number,required = false )Integer PageNumber,
                                                                    @RequestParam(name = "PageSize",defaultValue = appConstants.Page_Size,required = false)Integer PageSize,
                                                                    @RequestParam(name = "SortBy",defaultValue = appConstants.Sort_Products_By,required = false) String SortBy,
                                                                    @RequestParam(name = "SortOrder",defaultValue = appConstants.Sort_dir,required = false)String SortOrder){
        ProductResponse productResponse=productService.getAllProductsbyCategory(categoryId,PageNumber,PageSize,SortBy,SortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }
    @GetMapping("/public/products/Keyword/{Keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String Keyword,
                                                                @RequestParam(name = "PageNumber",defaultValue = appConstants.Page_Number,required = false )Integer PageNumber,
                                                                @RequestParam(name = "PageSize",defaultValue = appConstants.Page_Size,required = false)Integer PageSize,
                                                                @RequestParam(name = "SortBy",defaultValue = appConstants.Sort_Products_By,required = false) String SortBy,
                                                                @RequestParam(name = "SortOrder",defaultValue = appConstants.Sort_dir,required = false)String SortOrder){
        ProductResponse productResponse=productService.searchProductByKeyword(Keyword,PageNumber,PageSize,SortBy,SortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.FOUND);
    }
    @PutMapping("/admin/product/{productId}")
    public ResponseEntity<ProductDTO> UpdateProduct(@Valid @RequestBody ProductDTO productDTO,@PathVariable Long productId){
        ProductDTO UpdatedProductDTO=productService.updateProduct(productDTO,productId);
        return new ResponseEntity<>(UpdatedProductDTO,HttpStatus.OK);
    }
    @DeleteMapping("/admin/product/{productId}")
    public ResponseEntity<ProductDTO> DeleteProduct(@PathVariable Long productId){
        ProductDTO productDTO=productService.deleteProduct(productId);
        return new ResponseEntity<>( productDTO,HttpStatus.OK);
    }
    @PutMapping("/admin/product/{productId}/image")
    public ResponseEntity<ProductDTO> UpdateProductImage(@PathVariable Long productId, @RequestParam("image")MultipartFile image) throws IOException {
        ProductDTO UpdatedProduct=productService.UpdateProductImage(productId,image);
        return new ResponseEntity<>(UpdatedProduct,HttpStatus.OK);
    }



}

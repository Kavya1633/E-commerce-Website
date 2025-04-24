package com.e_commerce.project.service;

import com.e_commerce.project.exceptions.APIExceptions;
import com.e_commerce.project.exceptions.ResourceNotFoundException;
import com.e_commerce.project.model.Cart;
import com.e_commerce.project.model.Category;
import com.e_commerce.project.model.Product;
import com.e_commerce.project.payload.CartDTO;
import com.e_commerce.project.payload.ProductDTO;
import com.e_commerce.project.payload.ProductResponse;
import com.e_commerce.project.repositories.CartRepository;
import com.e_commerce.project.repositories.CategoryRepository;
import com.e_commerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;
    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","CategoryId",categoryId));

        // check product is already in DB or not
        boolean isProductNotPresent=true;
        List<Product> products=category.getProducts();
        for (Product value : products) {
            if (value.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }
        if(isProductNotPresent==false){
            throw new APIExceptions(productDTO+" already exists!!");
        }
        Product product=modelMapper.map(productDTO,Product.class);
        // add the product in DB
        product.setCategory(category);
        product.setImage("default.png");
        double specialPrice= product.getPrice()-(product.getDiscount()*0.01* product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedproduct=productRepository.save(product);

        return modelMapper.map(savedproduct,ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();

        Pageable PageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProduct=productRepository.findAll(PageDetails);

        List<Product> allProducts=pageProduct.getContent();
        if(allProducts.isEmpty()){
            throw new APIExceptions("No products have been added!!");
        }
        List<ProductDTO> productDTOS=allProducts.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class)).toList();

        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProduct.getNumber());
        productResponse.setTotalPages(pageProduct.getTotalPages());
        productResponse.setPageSize(pageProduct.getSize());
        productResponse.setLastPage(pageProduct.isLast());
        productResponse.setTotalElements(pageProduct.getTotalElements());
        return productResponse;

    }

    @Override
    public ProductResponse getAllProductsbyCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","CategoryId",categoryId));

        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();

        Pageable PageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProduct=productRepository.findByCategoryOrderByPriceAsc(category,PageDetails);

        List<Product> products=pageProduct.getContent();
        if(products.isEmpty()){
            throw new APIExceptions("No Products Associated with categoryId "+categoryId);
        }
        List<ProductDTO> productDTOS=products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class)).toList();

        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProduct.getNumber());
        productResponse.setTotalPages(pageProduct.getTotalPages());
        productResponse.setPageSize(pageProduct.getSize());
        productResponse.setLastPage(pageProduct.isLast());
        productResponse.setTotalElements(pageProduct.getTotalElements());
        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();

        Pageable PageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProduct=productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%',PageDetails);


        List<Product> products=pageProduct.getContent();

        List<ProductDTO> productDTOS=products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class)).toList();

        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProduct.getNumber());
        productResponse.setTotalPages(pageProduct.getTotalPages());
        productResponse.setPageSize(pageProduct.getSize());
        productResponse.setLastPage(pageProduct.isLast());
        productResponse.setTotalElements(pageProduct.getTotalElements());

        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        // product from DB
        Product existingProduct=productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));

        // get product from user
        Product product=modelMapper.map(productDTO,Product.class);

        // update the changes
        existingProduct.setProductName(product.getProductName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDiscount(product.getDiscount());
        existingProduct.setQuantity(product.getQuantity());
        double specialPrice= product.getPrice()-(product.getDiscount()*0.01* product.getPrice());
        existingProduct.setSpecialPrice(specialPrice);

        // save Updated Product to DB
        Product savedProduct=productRepository.save(existingProduct);

        List<Cart> carts = cartRepository.findCartsbyProductId(productId);

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

            cartDTO.setProducts(products);

            return cartDTO;

        }).collect(Collectors.toList());

        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));



        ProductDTO UpdatedProductDTO=modelMapper.map(savedProduct,ProductDTO.class);

        return UpdatedProductDTO;
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product=productRepository.findById(productId)
                        .orElseThrow(()->new ResourceNotFoundException("Product","ProductId",productId));

        // DELETE FROM CART
        List<Cart> carts = cartRepository.findCartsbyProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

        productRepository.delete(product);
        return modelMapper.map(product,ProductDTO.class);

    }

    @Override
    public ProductDTO UpdateProductImage(Long productId, MultipartFile image) throws IOException {
        // Get the product from DB
        Product productfromDB=productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","ProductId",productId));
        // Upload the image to server
        // Get the file name of uploaded image
        String filename= fileService.uploadImage(path,image);
        //update image in the product
        productfromDB.setImage(filename);
        // save the product
        Product updatedProduct=productRepository.save(productfromDB);
        // get the product Dto
        return modelMapper.map(updatedProduct,ProductDTO.class);
    }




}

package com.e_commerce.project.service;

import com.e_commerce.project.Util.AuthUtil;
import com.e_commerce.project.exceptions.APIExceptions;
import com.e_commerce.project.exceptions.ResourceNotFoundException;
import com.e_commerce.project.model.Cart;
import com.e_commerce.project.model.CartItems;
import com.e_commerce.project.model.Product;
import com.e_commerce.project.payload.CartDTO;
import com.e_commerce.project.payload.ProductDTO;
import com.e_commerce.project.repositories.CartItemRepository;
import com.e_commerce.project.repositories.CartRepository;
import com.e_commerce.project.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        // retrieve Existing Cart or Create one
        Cart cart=createCart();

        // get Product details
        Product product=productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("product","productId",productId));

        // check validations related to stock Quantity
        CartItems cartItem=cartItemRepository.findItembyProductIdAndCartId(cart.getCartId(),productId);

        if(cartItem!=null){
            throw new APIExceptions("Product "+product.getProductName()+" already exists in the cart!!");
        }

        if(product.getQuantity()==0){
            throw new APIExceptions(product.getProductName()+" is not available!!");
        }

        if(product.getQuantity()<quantity){
            throw new APIExceptions("Please make an order of "+product.getProductName()+" less than or equal to quantity "+product.getQuantity());
        }


        // Create cart Item
        CartItems newcartItems=new CartItems();

        newcartItems.setProduct(product);
        newcartItems.setCart(cart);
        cart.getCartItems().add(newcartItems);
        newcartItems.setQuantity(quantity);
        newcartItems.setDiscount(product.getDiscount());
        newcartItems.setProductPrice(product.getSpecialPrice());

        // save the cartItem
        cartItemRepository.save((newcartItems));
        cart.setTotalPrice(cart.getTotalPrice()+product.getSpecialPrice()*quantity);
        cartRepository.save(cart);
        // return the updated Cart
        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);

        List<CartItems> cartItemsList=cart.getCartItems();
        System.out.println("Cart Items Size: " + cart.getCartItems().size());


        Stream<ProductDTO> productDTOStream=cartItemsList.stream().map(item->
        {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }

    private Cart createCart() {
        Cart userCart= cartRepository.findCartbyEmail(authUtil.loggedInEmail());

        if(userCart!=null){
            return userCart;
        }
        Cart cart=new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());

        Cart newCart= cartRepository.save(cart);

        return newCart;
    }
    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if (carts.size() == 0) {
            throw new APIExceptions("No cart exists");
        }

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            cart.getCartItems().forEach(c ->
                    c.getProduct().setQuantity(c.getQuantity()));

            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

            cartDTO.setProducts(products);

            return cartDTO;

        }).collect(Collectors.toList());

        return cartDTOs;
    }
    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null){
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cart.getCartItems().forEach(c ->
                c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                .toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }
    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartbyEmail(emailId);
        Long cartId  = userCart.getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if (product.getQuantity() == 0) {
            throw new APIExceptions(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new APIExceptions("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        CartItems cartItem = cartItemRepository.findItembyProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new APIExceptions("Product " + product.getProductName() + " not available in the cart!!!");
        }

        // Calculate new quantity
        int newQuantity = cartItem.getQuantity() + quantity;

        // Validation to prevent negative quantities
        if (newQuantity < 0) {
            throw new APIExceptions("The resulting quantity cannot be negative.");
        }

        if (newQuantity == 0){
            deleteProductFromCart(cartId, productId);
        } else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
        }

        CartItems updatedItem = cartItemRepository.save(cartItem);
        if(updatedItem.getQuantity() == 0){
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }


        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItems> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO prd = modelMapper.map(item.getProduct(), ProductDTO.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        });


        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }
    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItems cartItem = cartItemRepository.findItembyProductIdAndCartId(cartId,productId);

        if (cartItem == null) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice() -
                (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteItembyProductIdAndCartId(cartId, productId);

        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart !!!";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItems cartItem = cartItemRepository.findItembyProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new APIExceptions("Product " + product.getProductName() + " not available in the cart!!!");
        }

        double cartPrice = cart.getTotalPrice()
                - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice
                + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItem = cartItemRepository.save(cartItem);
    }
}

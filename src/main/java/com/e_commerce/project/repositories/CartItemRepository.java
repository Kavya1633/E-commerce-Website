package com.e_commerce.project.repositories;

import com.e_commerce.project.model.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItems,Long> {

    @Query("SELECT ci FROM CartItems ci WHERE ci.cart.id = ?1 AND ci.product.id = ?2")
    CartItems findItembyProductIdAndCartId(Long cartId, Long productId);


    @Modifying
    @Query("DELETE FROM CartItems ci WHERE ci.cart.id = ?1 AND ci.product.id = ?2")
    void deleteItembyProductIdAndCartId(Long cartId, Long productId);


}

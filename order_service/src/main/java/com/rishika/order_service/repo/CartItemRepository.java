package com.rishika.order_service.repo;

import com.rishika.order_service.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {



    @Query("SELECT SUM(ci.price) FROM CartItem ci WHERE ci.cart.cartId = :cartId")
    Double sumPriceByCartId(@Param("cartId") Long cartId);
    void deleteByCart_CartId(Long cartId);

    List<CartItem> findByCart_CartId(Long cartId);
}

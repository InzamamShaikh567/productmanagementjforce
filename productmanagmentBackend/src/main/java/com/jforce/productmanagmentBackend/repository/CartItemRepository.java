package com.jforce.productmanagmentBackend.repository;

import com.jforce.productmanagmentBackend.entity.Cart;
import com.jforce.productmanagmentBackend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
    Optional<CartItem> findByCartAndProductId(Cart cart, Long productId);
    void deleteByCart(Cart cart);
}

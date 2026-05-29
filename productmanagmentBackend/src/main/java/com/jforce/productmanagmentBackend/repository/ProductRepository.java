package com.jforce.productmanagmentBackend.repository;

import com.jforce.productmanagmentBackend.entity.Category;
import com.jforce.productmanagmentBackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryAndNameContainingIgnoreCase(Category category, String name);
    List<Product> findByEnabledTrue();
    List<Product> findByCategoryAndEnabledTrue(Category category);
    List<Product> findByCategoryAndNameContainingIgnoreCaseAndEnabledTrue(Category category, String name);
    List<Product> findByNameContainingIgnoreCaseAndEnabledTrue(String name);
    boolean existsByCategory(Category category);
}

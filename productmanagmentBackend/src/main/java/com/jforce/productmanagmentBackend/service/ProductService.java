package com.jforce.productmanagmentBackend.service;

import com.jforce.productmanagmentBackend.dto.request.ProductRequest;
import com.jforce.productmanagmentBackend.dto.response.ProductResponse;
import com.jforce.productmanagmentBackend.entity.Category;
import com.jforce.productmanagmentBackend.entity.Inventory;
import com.jforce.productmanagmentBackend.entity.Product;
import com.jforce.productmanagmentBackend.entity.User;
import com.jforce.productmanagmentBackend.exception.ResourceNotFoundException;
import com.jforce.productmanagmentBackend.repository.InventoryRepository;
import com.jforce.productmanagmentBackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CategoryService categoryService;

    public List<ProductResponse> getAllProducts(String category, String search, User currentUser) {
        boolean isAdmin = currentUser != null && currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN") || r.getName().equals("SUPER_ADMIN"));

        List<Product> products;

        if (isAdmin) {
            if (search != null && !search.isBlank()) {
                if (category != null && !category.isBlank()) {
                    Category cat = categoryService.getCategoryEntityById(Long.parseLong(category));
                    products = productRepository.findByCategoryAndNameContainingIgnoreCase(cat, search);
                } else {
                    products = productRepository.findByNameContainingIgnoreCase(search);
                }
            } else if (category != null && !category.isBlank()) {
                Category cat = categoryService.getCategoryEntityById(Long.parseLong(category));
                products = productRepository.findByCategory(cat);
            } else {
                products = productRepository.findAll();
            }
        } else {
            if (search != null && !search.isBlank()) {
                if (category != null && !category.isBlank()) {
                    Category cat = categoryService.getCategoryEntityById(Long.parseLong(category));
                    products = productRepository.findByCategoryAndNameContainingIgnoreCaseAndEnabledTrue(cat, search);
                } else {
                    products = productRepository.findByNameContainingIgnoreCaseAndEnabledTrue(search);
                }
            } else if (category != null && !category.isBlank()) {
                Category cat = categoryService.getCategoryEntityById(Long.parseLong(category));
                products = productRepository.findByCategoryAndEnabledTrue(cat);
            } else {
                products = productRepository.findByEnabledTrue();
            }
        }

        return products.stream()
                .map(p -> {
                    Inventory inv = inventoryRepository.findByProduct(p).orElse(null);
                    return ProductResponse.from(p, inv != null ? inv.getQuantity() : 0);
                })
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        Inventory inv = inventoryRepository.findByProduct(product).orElse(null);
        return ProductResponse.from(product, inv != null ? inv.getQuantity() : 0);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryService.getCategoryEntityById(request.getCategoryId());

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        product.setImageUrl(request.getImageUrl());
        product.setEnabled(true);

        Product saved = productRepository.save(product);

        Inventory inventory = new Inventory();
        inventory.setProduct(saved);
        inventory.setQuantity(0);
        inventoryRepository.save(inventory);

        return ProductResponse.from(saved, 0);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        Category category = categoryService.getCategoryEntityById(request.getCategoryId());

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        product.setImageUrl(request.getImageUrl());

        Product saved = productRepository.save(product);
        Inventory inv = inventoryRepository.findByProduct(saved).orElse(null);
        return ProductResponse.from(saved, inv != null ? inv.getQuantity() : 0);
    }

    public void setEnabled(Long id, boolean enabled) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        product.setEnabled(enabled);
        productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        inventoryRepository.findByProduct(product).ifPresent(inventoryRepository::delete);
        productRepository.deleteById(id);
    }
}

package com.jforce.productmanagmentBackend.config;

import com.jforce.productmanagmentBackend.entity.*;
import com.jforce.productmanagmentBackend.repository.*;
import com.jforce.productmanagmentBackend.entity.*;
import com.jforce.productmanagmentBackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedRoles();
        seedUsers();
        seedCategories();
        seedProducts();
    }

    private void seedRoles() {
        if (roleRepository.count() > 0) return;

        roleRepository.save(new Role(null, "USER"));
        roleRepository.save(new Role(null, "ADMIN"));
        roleRepository.save(new Role(null, "SUPER_ADMIN"));
    }

    private void seedUsers() {
        Role userRole = roleRepository.findByName("USER").orElse(null);
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        Role superAdminRole = roleRepository.findByName("SUPER_ADMIN").orElse(null);

        if (userRepository.findByUsername("user").isEmpty() && userRole != null) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@store.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
        }

        if (userRepository.findByUsername("admin").isEmpty() && adminRole != null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@store.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("superadmin").isEmpty() && superAdminRole != null) {
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setEmail("superadmin@store.com");
            superAdmin.setPassword(passwordEncoder.encode("admin123"));
            superAdmin.setRoles(Set.of(superAdminRole));
            userRepository.save(superAdmin);
        }
    }

    private void seedCategories() {
        if (categoryRepository.count() > 0) return;

        categoryRepository.save(new Category(null, "Electronics", "Electronic devices and accessories"));
        categoryRepository.save(new Category(null, "Clothing", "Apparel and fashion items"));
        categoryRepository.save(new Category(null, "Home & Kitchen", "Home and kitchen essentials"));
        categoryRepository.save(new Category(null, "Sports", "Sports equipment and gear"));
    }

    private void seedProducts() {
        inventoryRepository.deleteAll();
        productRepository.deleteAll();

        Category electronics = categoryRepository.findAll().get(0);
        Category clothing = categoryRepository.findAll().get(1);
        Category homeKitchen = categoryRepository.findAll().get(2);
        Category sports = categoryRepository.findAll().get(3);

        List<Product> products = Arrays.asList(
            createProduct("Bluetooth Speaker", "Portable wireless speaker with deep bass and 12-hour battery life.", 39.99, electronics),
            createProduct("USB-C Cable", "Fast charging USB-C cable, 6ft length, durable braided design.", 12.99, electronics),
            createProduct("Wireless Mouse", "Ergonomic wireless mouse with silent click and adjustable DPI.", 24.99, electronics),
            createProduct("Cotton T-Shirt", "Soft and breathable 100% cotton t-shirt. Regular fit.", 19.99, clothing),
            createProduct("Denim Jeans", "Classic straight fit denim jeans with 5-pocket styling.", 49.99, clothing),
            createProduct("Winter Jacket", "Warm and lightweight winter jacket with water-resistant outer layer.", 89.99, clothing),
            createProduct("Coffee Maker", "12-cup drip coffee maker with auto shut-off and reusable filter.", 34.99, homeKitchen),
            createProduct("Non-stick Pan", "10-inch non-stick frying pan with durable coating.", 29.99, homeKitchen),
            createProduct("LED Desk Lamp", "Adjustable LED desk lamp with 3 brightness levels.", 27.99, homeKitchen),
            createProduct("Yoga Mat", "6mm thick yoga mat with non-slip surface. Includes carrying strap.", 22.99, sports),
            createProduct("Running Shoes", "Lightweight running shoes with cushioned sole and breathable mesh upper.", 69.99, sports),
            createProduct("Dumbbells (5kg)", "Set of 2x 5kg dumbbells with hexagonal shape to prevent rolling.", 34.99, sports)
        );

        List<Product> savedProducts = productRepository.saveAll(products);

        for (Product product : savedProducts) {
            Inventory inv = new Inventory();
            inv.setProduct(product);
            inv.setQuantity(50);
            inventoryRepository.save(inv);
        }
    }

    private Product createProduct(String name, String description, double price, Category category) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setCategory(category);
        p.setImageUrl("https://placehold.co/150x150?text=" + name.replace(" ", "+"));
        p.setEnabled(true);
        return p;
    }
}

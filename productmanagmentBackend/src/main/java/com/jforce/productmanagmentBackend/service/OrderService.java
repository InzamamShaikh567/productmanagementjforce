package com.jforce.productmanagmentBackend.service;

import com.jforce.productmanagmentBackend.dto.response.OrderResponse;
import com.jforce.productmanagmentBackend.entity.*;
import com.jforce.productmanagmentBackend.entity.*;
import com.jforce.productmanagmentBackend.exception.BadRequestException;
import com.jforce.productmanagmentBackend.exception.ResourceNotFoundException;
import com.jforce.productmanagmentBackend.repository.AddressRepository;
import com.jforce.productmanagmentBackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final InventoryService inventoryService;
    private final AddressRepository addressRepository;

    @Transactional
    public OrderResponse checkout(User user, Long addressId) {
        List<CartItem> cartItems = cartService.getCartItems(user);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Address address;
        if (addressId == null) {
            List<Address> userAddresses = addressRepository.findByUser(user);
            if (userAddresses.isEmpty()) {
                throw new BadRequestException("Please add a shipping address before checkout");
            }
            address = userAddresses.stream()
                    .filter(Address::isDefault)
                    .findFirst()
                    .orElse(userAddresses.get(0));
        } else {
            address = addressRepository.findById(addressId)
                    .orElseThrow(() -> new ResourceNotFoundException("Address", addressId));
            if (!address.getUser().getId().equals(user.getId())) {
                throw new BadRequestException("Address does not belong to this user");
            }
        }

        inventoryService.validateAndDeduct(cartItems);

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PROCESSING);

        double total = 0;
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getProduct().getPrice());
            order.getItems().add(orderItem);
            total += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        cartService.clearCart(user);
        return OrderResponse.from(saved);
    }

    public List<OrderResponse> getUserOrders(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user).stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    public OrderResponse updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        order.setStatus(status);
        return OrderResponse.from(orderRepository.save(order));
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        orderRepository.deleteById(id);
    }
}

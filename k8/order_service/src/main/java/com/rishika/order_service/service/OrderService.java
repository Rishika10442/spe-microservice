package com.rishika.order_service.service;

import com.rishika.order_service.dto.OrderItemResponseDTO;
import com.rishika.order_service.dto.OrderResponseDTO;
import com.rishika.order_service.dto.UserOrderItemDTO;
import com.rishika.order_service.dto.UserOrderResponseDTO;
import com.rishika.order_service.entity.Cart;
import com.rishika.order_service.entity.CartItem;
import com.rishika.order_service.entity.Order;
import com.rishika.order_service.entity.OrderItem;
import com.rishika.order_service.repo.CartItemRepository;
import com.rishika.order_service.repo.CartRepository;
import com.rishika.order_service.repo.OrderItemRepository;
import com.rishika.order_service.repo.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional
    public OrderResponseDTO placeOrder(Long cartId) {
        try {
            // Fetch cart and items
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + cartId));

            List<CartItem> cartItems = cartItemRepository.findByCart_CartId(cartId);

            if (cartItems.isEmpty()) {
                throw new RuntimeException("Cart is empty. Cannot place order.");
            }

            // Create Order
            Order order = Order.builder()
                    .userId(cart.getUserId())
                    .totalAmount(cart.getTotalPrice())
                    .orderedAt(LocalDateTime.now())
                    .status("PLACED")
                    .build();
            orderRepository.save(order);

            // Create OrderItems
            List<OrderItem> orderItems = new ArrayList<>();
            List<OrderItemResponseDTO> orderItemDTOs = new ArrayList<>();

            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .bookId(cartItem.getBookId())
                        .quantity(cartItem.getQuantity())
                        .priceAtPurchase(cartItem.getPrice())
                        .build();
                orderItems.add(orderItem);
            }
            orderItemRepository.saveAll(orderItems);

            // Clear cart items (recommended: use repository method for specific cart ID)
            cartItemRepository.deleteByCart_CartId(cartId);

            // Reset cart total
            cart.setTotalPrice(0.0);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);

            // Map to response DTO
            for (OrderItem item : orderItems) {
                orderItemDTOs.add(OrderItemResponseDTO.builder()
                        .orderItemId(item.getOrderItemId())
                        .bookId(item.getBookId())
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPriceAtPurchase())
                        .build());
            }

            return OrderResponseDTO.builder()
                    .orderId(order.getOrderId())
                    .userId(order.getUserId())
                    .totalAmount(order.getTotalAmount())
                    .orderedAt(order.getOrderedAt())
                    .status(order.getStatus())
                    .orderItems(orderItemDTOs)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to place order: " + e.getMessage(), e);
        }
    }


    public List<UserOrderResponseDTO> getUserOrders(Long userId) {
        try {
            List<Order> orders = orderRepository.findByUserId(userId);

            if (orders.isEmpty()) {
                return Collections.emptyList();
            }

            List<UserOrderResponseDTO> response = new ArrayList<>();

            for (Order order : orders) {
                List<OrderItem> orderItems = orderItemRepository.findByOrder_OrderId(order.getOrderId());

                List<UserOrderItemDTO> itemDTOs = orderItems.stream()
                        .map(item -> UserOrderItemDTO.builder()
                                .orderItemId(item.getOrderItemId())
                                .bookId(item.getBookId())
                                .quantity(item.getQuantity())
                                .priceAtPurchase(item.getPriceAtPurchase())
                                .build())
                        .collect(Collectors.toList());

                response.add(UserOrderResponseDTO.builder()
                        .orderId(order.getOrderId())
                        .orderedAt(order.getOrderedAt())
                        .totalAmount(order.getTotalAmount())
                        .status(order.getStatus())
                        .items(itemDTOs)
                        .build());
            }

            return response;
        } catch (Exception e) {
            // Log the error if needed
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch user orders: " + e.getMessage());
        }
    }

}

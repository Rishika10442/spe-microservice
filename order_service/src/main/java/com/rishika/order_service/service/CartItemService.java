package com.rishika.order_service.service;


import com.rishika.order_service.dto.BookPriceResponse;
import com.rishika.order_service.dto.CartItemResponseDTO;
import com.rishika.order_service.entity.Cart;
import com.rishika.order_service.entity.CartItem;
import com.rishika.order_service.repo.CartItemRepository;
import com.rishika.order_service.repo.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@Service
public class CartItemService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(CartItemService.class);
    public Map<String, Object> addToCart(Long userId, Long bookId,String jwtToken) {
        Map<String, Object> response = new HashMap<>();
        String CATALOG_SERVICE_URL="http://localhost:8081/catalogue/price/";
        logger.info("jwt token{}",jwtToken);
        try {
            // Step 1: Prepare HTTP headers and add the Authorization token
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", jwtToken);

            // Step 2: Create HttpEntity with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Step 3: Fetch book price from catalog service using RestTemplate
            ResponseEntity<BookPriceResponse> bookResponse = restTemplate.exchange(
                    CATALOG_SERVICE_URL + bookId,  // URL to fetch book price
                    HttpMethod.GET,                 // HTTP Method (GET)
                    entity,                         // Pass headers (Authorization)
                    BookPriceResponse.class        // Response body type
            );

            BookPriceResponse bookPriceResponse = bookResponse.getBody();
            Double bookPrice = 0.0;

            if (bookPriceResponse != null && bookPriceResponse.isSuccess()) {
                bookPrice = bookPriceResponse.getPrice();
            } else {
                response.put("success", false);
                response.put("message", "Failed to fetch price from Catalogue service");
                return response;
            }

            // Step 2: Create CartItem with default quantity of 1
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setBookId(bookId);
            cartItem.setQuantity(1);
            cartItem.setPrice(bookPrice);
            cartItem.setAddedAt(LocalDateTime.now());

            // Step 3: Create or Update the Cart
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        Cart newCart = Cart.builder()
                                .userId(userId)
                                .totalPrice(0.0)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                        return cartRepository.save(newCart); // 🔥 Save the new Cart before using it
                    });

            // Set the cartId for the cartItem
            cartItem.setCart(cart);

            // Save the cart item
            cartItemRepository.save(cartItem);

            // Update cart total price by recalculating total from cart items
            Double newTotalPrice = cartItemRepository.sumPriceByCartId(cart.getCartId());
            cart.setTotalPrice(newTotalPrice);

            // Save the updated cart
            cartRepository.save(cart);

            // Step 4: Return the response with cart items and total price
            response.put("success", true);
            response.put("cartItems", cartItemRepository.findByCart_CartId(cart.getCartId()));
            response.put("totalPrice", cart.getTotalPrice());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred while adding to cart: " + e.getMessage());
        }

        return response;
    }
    public Map<String, Object> updateCartItemQuantity(Long cartItemId, Integer newQuantity) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Fetch the cart item
            CartItem cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(() -> new RuntimeException("Cart item not found with ID: " + cartItemId));

            if (newQuantity <= 0) {
                response.put("success", false);
                response.put("message", "Quantity must be greater than zero.");
                return response;
            }

            // Calculate per-item price
            double unitPrice = cartItem.getPrice() / cartItem.getQuantity();

            // Update quantity and price
            cartItem.setQuantity(newQuantity);
            cartItem.setPrice(unitPrice * newQuantity);
            cartItemRepository.save(cartItem);

            // Update cart total
            Cart cart = cartItem.getCart();
            Double updatedTotal = cartItemRepository.sumPriceByCartId(cart.getCartId());
            cart.setTotalPrice(updatedTotal);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);

            // Prepare DTO
            CartItemResponseDTO cartItemDTO = new CartItemResponseDTO(
                    cartItem.getCartItemId(),
                    cartItem.getBookId(),
                    cartItem.getUserId(),
                    cartItem.getQuantity(),
                    cartItem.getPrice(),
                    cartItem.getAddedAt()
            );

            response.put("success", true);
            response.put("updatedCartItem", cartItemDTO);
            response.put("totalPrice", updatedTotal);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred: " + e.getMessage());
        }

        return response;
    }


}

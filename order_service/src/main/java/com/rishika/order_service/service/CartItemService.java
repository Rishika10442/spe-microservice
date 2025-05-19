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
import java.util.*;

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
        String CATALOG_SERVICE_URL="http://catalogue-service:8082/catalogue/price/";
      //  String CATALOG_SERVICE_URL="http://localhost:8081/catalogue/price/";
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
            List<CartItem> cartItems = cartItemRepository.findByCart_CartId(cart.getCartId());
            List<Map<String, Object>> enrichedCartItems = new ArrayList<>();

            for (CartItem item : cartItems) {
                ResponseEntity<BookPriceResponse> detailResponse = restTemplate.exchange(
                        CATALOG_SERVICE_URL + item.getBookId(),
                        HttpMethod.GET,
                        entity,
                        BookPriceResponse.class
                );

                String bookName = "";
                if (detailResponse.getBody() != null && detailResponse.getBody().isSuccess()) {
                    bookName = detailResponse.getBody().getName();
                }

                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("cartItemId", item.getCartItemId());
                itemMap.put("bookId", item.getBookId());
                itemMap.put("userId", item.getUserId());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("price", item.getPrice());
                itemMap.put("addedAt", item.getAddedAt());
                itemMap.put("bookName", bookName);
                itemMap.put("cart", item.getCart());

                enrichedCartItems.add(itemMap);
            }

            response.put("success", true);
            response.put("totalPrice", cart.getTotalPrice());
            response.put("cartItems", enrichedCartItems);

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



    public Map<String, Object> getCartItems(Long userId, String jwtToken) {
        Map<String, Object> response = new HashMap<>();
        String CATALOG_SERVICE_URL = "http://catalogue-service:8082/catalogue/book-details/";
      //  String CATALOG_SERVICE_URL = "http://localhost:8081/catalogue/book-details/";
        try {
            // Step 1: Get cart for user
            Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
            if (optionalCart.isEmpty()) {
                response.put("success", true);
                response.put("totalPrice", 0.0);
                response.put("cartItems", List.of());
                return response;
            }

            Cart cart = optionalCart.get();

            // Step 2: Fetch all items for the cart
            List<CartItem> cartItems = cartItemRepository.findByCart_CartId(cart.getCartId());

            // Step 3: Prepare enriched cart items
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", jwtToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            List<Map<String, Object>> enrichedCartItems = new ArrayList<>();

            for (CartItem item : cartItems) {
                ResponseEntity<BookPriceResponse> detailResponse = restTemplate.exchange(
                        CATALOG_SERVICE_URL + item.getBookId(),
                        HttpMethod.GET,
                        entity,
                        BookPriceResponse.class
                );

                String bookName = "";
                if (detailResponse.getBody() != null && detailResponse.getBody().isSuccess()) {
                    bookName = detailResponse.getBody().getName();
                }

                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("cartItemId", item.getCartItemId());
                itemMap.put("bookId", item.getBookId());
                itemMap.put("userId", item.getUserId());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("price", item.getPrice());
                itemMap.put("addedAt", item.getAddedAt());
                itemMap.put("bookName", bookName);
                itemMap.put("cart", item.getCart());

                enrichedCartItems.add(itemMap);
            }

            response.put("success", true);
            response.put("totalPrice", cart.getTotalPrice());
            response.put("cartItems", enrichedCartItems);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred while fetching cart items: " + e.getMessage());
        }

        return response;
    }


    public Map<String, Object> removeFromCart(Long cartItemId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<CartItem> optionalItem = cartItemRepository.findById(cartItemId);
            if (optionalItem.isEmpty()) {
                response.put("success", false);
                response.put("message", "Cart item not found");
                return response;
            }

            CartItem cartItem = optionalItem.get();
            Cart cart = cartItem.getCart();

            // Deduct the item's price from cart total
            double itemPrice = cartItem.getPrice(); // this is already quantity-adjusted
            double newTotal = cart.getTotalPrice() - itemPrice;

            // Delete the cart item
            cartItemRepository.delete(cartItem);

            // Update cart total price and save
            cart.setTotalPrice(Math.max(newTotal, 0.0)); // prevent negative price
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);

            response.put("success", true);
            response.put("message", "Item removed successfully");
            response.put("newTotalPrice", cart.getTotalPrice());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error while removing item: " + e.getMessage());
        }

        return response;
    }

}

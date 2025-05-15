package com.rishika.order_service.controller;

import com.rishika.order_service.dto.CartRequest;
import com.rishika.order_service.dto.OrderResponseDTO;
import com.rishika.order_service.dto.UserOrderResponseDTO;
import com.rishika.order_service.service.CartItemService;
import com.rishika.order_service.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private OrderService orderService;
//    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @GetMapping("/test")
    public String test() {
        return "Order Service is Working!";
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestHeader("Authorization") String jwtToken,
                                       @RequestBody CartRequest cartRequest) {
        try {
            Long userId = cartRequest.getUserId();
            Long bookId = cartRequest.getBookId();

            // Call the service to add the book to the cart
            Map<String, Object> response = cartItemService.addToCart(userId, bookId,jwtToken);
            if ((boolean) response.get("success")) {
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "An error occurred: " + e.getMessage()));
        }
    }


    @PostMapping("/update-quantity")
    public ResponseEntity<?> updateCartItemQuantity(@RequestBody Map<String, Object> body) {
        try {
            Long cartItemId = Long.valueOf(body.get("cartItemId").toString());
            Integer newQuantity = Integer.valueOf(body.get("newQuantity").toString());

            Map<String, Object> response = cartItemService.updateCartItemQuantity(cartItemId, newQuantity);

            if ((boolean) response.get("success")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to update quantity: " + e.getMessage()));
        }
    }

    @PostMapping("/placeOrder")
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> request) {
        try {
            Long cartId = Long.valueOf(request.get("cartId").toString());
            OrderResponseDTO response = orderService.placeOrder(cartId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Order placed successfully",
                    "order", response
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to place order: " + e.getMessage()
                    ));
        }
    }

    @PostMapping("/your-orders")
    public ResponseEntity<?> getUserOrders(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            List<UserOrderResponseDTO> userOrders = orderService.getUserOrders(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orders", userOrders
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Failed to fetch orders: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/items/{userId}")
    public ResponseEntity<?> getCartItems(@PathVariable Long userId, @RequestHeader("Authorization") String jwtToken) {
        Map<String, Object> response = cartItemService.getCartItems(userId, jwtToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/remove/{cartItemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartItemId) {
        Map<String, Object> response = cartItemService.removeFromCart(cartItemId);
        return ResponseEntity.ok(response);
    }

}

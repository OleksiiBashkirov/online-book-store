package mate.academy.controller;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.model.Order;
import mate.academy.model.OrderItem;
import mate.academy.security.UserPrincipal;
import mate.academy.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Order> placeOrder(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody Order order) {
        order.setUser(userPrincipal.getUser());
        var placedOrder = orderService.placeOrder(order);
        return ResponseEntity.status(CREATED).body(placedOrder);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<Order>> getOrderHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        var orderHistory = orderService.getOrderHistory(userPrincipal.getUser().getId());
        return ResponseEntity.ok(orderHistory);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id,
            @RequestBody Order order) {
        var updaterOrder = orderService.updateOrderStatus(id, order.getStatus());
        return ResponseEntity.ok(updaterOrder);
    }

    @GetMapping("/{orderid}/items")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable Long orderId) {
        var orderItems = orderService.getOrderItems(orderId);
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<OrderItem> getOrderItem(@PathVariable Long orderId,
            @PathVariable Long itemId) {
        var orderItem = orderService.getOrderItem(orderId, itemId);
        return ResponseEntity.ok(orderItem);
    }
}

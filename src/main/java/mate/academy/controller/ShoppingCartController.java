package mate.academy.controller;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.AddToCartRequest;
import mate.academy.dto.UpdateCartItemRequest;
import mate.academy.model.ShoppingCart;
import mate.academy.security.UserPrincipal;
import mate.academy.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    public ResponseEntity<ShoppingCart> getCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ShoppingCart cart = shoppingCartService
                .getShoppingCartByUserId(userPrincipal.getUser());
        return ResponseEntity.ok(cart);
    }

    @PostMapping
    public ResponseEntity<Void> addToCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody AddToCartRequest request) {
        shoppingCartService.addToCart(
                userPrincipal.getUser(),
                request.getBookId(),
                request.getQuantity()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @PutMapping("/cart-items/{cartItemId}")
    public ResponseEntity<Void> updateCartItem(@PathVariable Long cartItemId,
                                               @RequestBody UpdateCartItemRequest request) {
        shoppingCartService.updateCartItem(cartItemId, request.getQuantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long cartItemId) {
        shoppingCartService.removeCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }
}

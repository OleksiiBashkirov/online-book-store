package mate.academy.service;

import mate.academy.model.ShoppingCart;

public interface ShoppingCartService {
    ShoppingCart getShoppingCartByUserId(Long userId);

    void addToCart(Long userId, Long bookId, int quantity);

    void updateCartItem(Long cartItemId, int quantity);

    void removeCartItem(Long cartItemId);
}

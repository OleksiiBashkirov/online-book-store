package mate.academy.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.model.Book;
import mate.academy.model.CartItem;
import mate.academy.model.ShoppingCart;
import mate.academy.repository.BookRepository;
import mate.academy.repository.CartItemRepository;
import mate.academy.repository.ShoppingCartRepository;
import mate.academy.repository.UserRepository;
import mate.academy.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    public ShoppingCart getShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId).orElseGet(
                () -> createShoppingCartForUser(userId));
    }

    @Override
    public void addToCart(Long userId, Long bookId, int quantity) {
        var cart = getShoppingCartByUserId(userId);
        var book = bookRepository.findByIdWithCategories(bookId).orElseThrow(
                () -> new EntityNotFoundException("Book not found by id: " + bookId));
        var cartItem = createCartItem(quantity, cart, book);
        cart.getCartItems().add(cartItem);
        shoppingCartRepository.save(cart);
    }

    @Override
    public void updateCartItem(Long cartItemId, int quantity) {
        var cartItem = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException("CartItem not found by id: " + cartItemId));
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    @Override
    public void removeCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    private ShoppingCart createShoppingCartForUser(Long userId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found by id: " + userId));
        var cart = new ShoppingCart();
        cart.setUser(user);
        return shoppingCartRepository.save(cart);
    }

    private CartItem createCartItem(int quantity, ShoppingCart cart, Book book) {
        var cartItem = new CartItem();
        cartItem.setShoppingCart(cart);
        cartItem.setBook(book);
        cartItem.setQuantity(quantity);
        return cartItem;
    }
}

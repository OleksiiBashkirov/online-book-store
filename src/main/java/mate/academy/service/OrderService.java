package mate.academy.service;

import java.util.List;
import mate.academy.model.Order;
import mate.academy.model.OrderItem;

public interface OrderService {
    Order placeOrder(Order order);

    List<Order> getOrderHistory(Long userId);

    Order updateOrderStatus(Long orderId, Order.Status status);

    List<OrderItem> getOrderItems(Long orderId);

    OrderItem getOrderItem(Long orderId, Long itemId);
}

package com.food.ordering.system.order.service;

import com.food.ordering.system.order.service.entity.Order;
import com.food.ordering.system.order.service.entity.Resturant;
import com.food.ordering.system.order.service.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.event.OrderPaidEvent;

import java.util.List;

public interface OrderDomainService {
    OrderCreatedEvent validateAndInitializeOrder(Order order, Resturant resturant);

    OrderPaidEvent payOrder(Order order);

    void approveOrder(Order order);

    OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages);

    void cancelOrder(Order order, List<String> failureMessages);
}

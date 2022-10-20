package com.food.ordering.system.order.service;

import com.food.ordering.system.order.service.entity.Order;
import com.food.ordering.system.order.service.entity.Product;
import com.food.ordering.system.order.service.entity.Resturant;
import com.food.ordering.system.order.service.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.event.OrderPaidEvent;
import com.food.ordering.system.order.service.exception.OrderDomainException;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService {

    public static final String UTC = "UTC";

    @Override
    public OrderCreatedEvent validateAndInitializeOrder(Order order, Resturant resturant) {
        validateRestaurant(resturant);
        setOrderProductInformation(order, resturant);
        order.validateOrder();
        order.initializeOrder();
        log.info("Order with id {} is initialized", order.getId());

        return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public OrderPaidEvent payOrder(Order order) {
        order.pay();
        log.info("Order with id {} is paid", order.getId());
        return new OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public void approveOrder(Order order) {
        order.approve();
        log.info("Order with id {} is approved", order.getId());
    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages) {
        order.initCancel(failureMessages);
        log.info("Order with id {} is cancelling.", order.getId());
        return new OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
        log.info("Order with id {} is cancelled", order.getId());
    }

    private void validateRestaurant(Resturant resturant) {
        if (!resturant.isActive()) {
            throw new OrderDomainException("Restaurant with id " + resturant.getId() + " is currently not active!");
        }
    }

    private void setOrderProductInformation(Order order, Resturant resturant) {
        order.getItems().forEach(orderItem -> {
            Product currentProduct = orderItem.getProduct();
            Product restaurantProduct = resturant.getProducts().get(currentProduct.getId());
            currentProduct.updateWithConfirmedNameAndPrice(restaurantProduct.getName(), restaurantProduct.getPrice());
        });
    }
}

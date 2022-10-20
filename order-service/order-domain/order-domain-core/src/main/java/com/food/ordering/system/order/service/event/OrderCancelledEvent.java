package com.food.ordering.system.order.service.event;

import com.food.ordering.system.order.service.entity.Order;

import java.time.ZonedDateTime;

public class OrderCancelledEvent extends OrderEvent{
    public OrderCancelledEvent(Order order, ZonedDateTime createdAt) {
        super(order, createdAt);
    }
}

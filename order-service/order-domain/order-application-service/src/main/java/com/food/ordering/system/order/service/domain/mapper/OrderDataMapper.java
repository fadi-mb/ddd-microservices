package com.food.ordering.system.order.service.domain.mapper;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.entity.Order;
import com.food.ordering.system.order.service.entity.OrderItem;
import com.food.ordering.system.order.service.entity.Product;
import com.food.ordering.system.order.service.entity.Resturant;
import com.food.ordering.system.order.service.valueobject.StreetAddress;
import com.food.ordering.system.valueobject.CustomerId;
import com.food.ordering.system.valueobject.Money;
import com.food.ordering.system.valueobject.ProductId;
import com.food.ordering.system.valueobject.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {
    public Resturant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
        return Resturant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(createOrderCommand.getItems().stream().map(orderItem -> {
                    return new Product(new ProductId(orderItem.getProductId()));
                }).collect(Collectors.toMap(Product::getId, Function.identity(), (o, o2) -> o2, HashMap::new)))
                .build();
    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.getAddress()))
                .price(new Money(createOrderCommand.getPrice()))
                .items(orderItemsToOrderItemsEntities(createOrderCommand.getItems()))
                .build();
    }

    private List<OrderItem> orderItemsToOrderItemsEntities(List<com.food.ordering.system.order.service.domain.dto.create.OrderItem> items) {
        return items.stream().map(orderItem -> OrderItem.builder()
                .product(new Product(new ProductId(orderItem.getProductId())))
                .price(new Money(orderItem.getPrice()))
                .quantity(orderItem.getQuantity())
                .subtotal(new Money(orderItem.getSubTotal()))
                .build()).collect(Collectors.toList());
    }

    private StreetAddress orderAddressToStreetAddress(OrderAddress address) {
        return new StreetAddress(
                UUID.randomUUID(),
                address.getStreat(),
                address.getPostalCode(),
                address.getCity()
        );
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order savedOrder, String message) {
        return CreateOrderResponse.builder()
                .orderTrackingId(savedOrder.getTrackingId().getValue())
                .status(savedOrder.getOrderStatus())
                .message(message)
                .build();
    }

    public TrackOrderResponse orderToTrackOrderResponse(Order order) {
        return TrackOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .status(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .build();
    }
}

package com.food.ordering.system.order.service.domain.ports.output.repository;

import com.food.ordering.system.order.service.entity.Resturant;

import java.util.Optional;

public interface RestaurantRepository {
    Optional<Resturant> findRestaurantInformation(Resturant restaurant);
}

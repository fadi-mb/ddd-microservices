package com.food.ordering.system.order.service.entity;

import com.food.ordering.system.entity.AggregateRoot;
import com.food.ordering.system.valueobject.ProductId;
import com.food.ordering.system.valueobject.RestaurantId;

import java.util.HashMap;

public class Resturant extends AggregateRoot<RestaurantId> {

    private HashMap<ProductId, Product> products;
    private boolean active;

    private Resturant(Builder builder) {
        super.setId( builder.restaurantId);
        products = builder.products;
        active = builder.active;
    }

    public HashMap<ProductId, Product> getProducts() {
        return products;
    }

    public boolean isActive() {
        return active;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {
        private RestaurantId restaurantId;
        private HashMap<ProductId, Product> products;
        private boolean active;

        private Builder() {
        }

        public Builder restaurantId(RestaurantId restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }

        public Builder products(HashMap<ProductId, Product> val) {
            products = val;
            return this;
        }

        public Builder active(boolean val) {
            active = val;
            return this;
        }

        public Resturant build() {
            return new Resturant(this);
        }
    }
}

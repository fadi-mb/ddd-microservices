package com.food.ordering.system.order.service.valueobject;

import com.food.ordering.system.valueobject.BaseId;

import java.util.UUID;

public class TrackingId extends BaseId<UUID> {
    public TrackingId(UUID value) {
        super(value);
    }
}

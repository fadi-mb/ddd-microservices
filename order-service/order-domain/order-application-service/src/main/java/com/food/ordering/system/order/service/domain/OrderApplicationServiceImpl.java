package com.food.ordering.system.order.service.domain;


import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Slf4j
@Service
public class OrderApplicationServiceImpl implements OrderApplicationService {

    private final CreateOrderCommandHandler createOrderCommandHandler;
    private final TrackOrderCommandHandler  trackOrderCommandHandler;

    public OrderApplicationServiceImpl(CreateOrderCommandHandler createOrderCommandHandler, TrackOrderCommandHandler trackOrderCommandHandler) {
        this.createOrderCommandHandler = createOrderCommandHandler;
        this.trackOrderCommandHandler = trackOrderCommandHandler;
    }

    @Override
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        return createOrderCommandHandler.createOrder(createOrderCommand);
    }

    @Override
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        return trackOrderCommandHandler.trackOrder(trackOrderQuery);
    }
}

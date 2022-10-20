package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.OrderDomainService;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import com.food.ordering.system.order.service.entity.Customer;
import com.food.ordering.system.order.service.entity.Order;
import com.food.ordering.system.order.service.entity.Resturant;
import com.food.ordering.system.order.service.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.exception.OrderDomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderCreateHelper {

    private final OrderDomainService orderDomainService;
    private final OrderDataMapper orderDataMapper;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;


    public OrderCreateHelper(
            OrderDomainService orderDomainService,
            OrderDataMapper orderDataMapper,
            OrderRepository orderRepository,
            RestaurantRepository restaurantRepository,
            CustomerRepository customerRepository) {
        this.orderDomainService = orderDomainService;
        this.orderDataMapper = orderDataMapper;
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand) {
        checkCustomer(createOrderCommand.getCustomerId());
        Resturant resturant = checkRestaurant(createOrderCommand);
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitializeOrder(order, resturant);
        saveOrder(order);

        return orderCreatedEvent;
    }

    private Order saveOrder(Order order) {
        Order saveOrderResult = orderRepository.save(order);
        if (order == null) {
            log.warn("Could not save order.");
            throw new OrderDomainException("Could not save order.");
        }
        log.info("Order is saved with id: {}", order.getId().getValue());
        return saveOrderResult;
    }

    private Resturant checkRestaurant(CreateOrderCommand createOrderCommand) {
        Resturant inputRestaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        Optional<Resturant> restaurantFetchResult = restaurantRepository.findRestaurantInformation(inputRestaurant);
        if (restaurantFetchResult.isEmpty()) {
            log.warn("Could not find restaurant with id :{}", inputRestaurant.getId());
            throw new OrderDomainException("Could not find restaurant with id " + inputRestaurant.getId());
        }
        return restaurantFetchResult.get();
    }

    private void checkCustomer(UUID customerId) {
        Optional<Customer> customer = customerRepository.findCustomer(customerId);
        if (customer.isEmpty()) {
            log.warn("Could not find customer with customer id:", customerId);
            throw new OrderDomainException("Could not find customer with customer id: " + customerId);
        }
    }
}

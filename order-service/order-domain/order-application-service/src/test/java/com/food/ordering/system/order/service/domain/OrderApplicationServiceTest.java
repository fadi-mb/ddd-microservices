package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import com.food.ordering.system.order.service.entity.Customer;
import com.food.ordering.system.order.service.entity.Order;
import com.food.ordering.system.order.service.entity.Product;
import com.food.ordering.system.order.service.entity.Resturant;
import com.food.ordering.system.order.service.exception.OrderDomainException;
import com.food.ordering.system.valueobject.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.food.ordering.system.valueobject.OrderStatus.PENDING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {
    @Autowired
    OrderApplicationService orderApplicationService;

    @Autowired
    OrderDataMapper orderDataMapper;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    CustomerRepository customerRepository;


    private CreateOrderCommand createOrderCommand;
    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand createOrderCommandWrongProductPrice;

    private UUID CUSTOMER_ID = UUID.fromString("62cb6470-3d37-4bb0-9f0c-9be82d416478");
    private UUID RESTAURANT_ID = UUID.fromString("119d9879-2de3-46fe-b57b-769f5652b294");
    private UUID PRODUCT_ID = UUID.fromString("0e1224bd-2250-4995-ae94-dc0fa40da7f3");
    private UUID ORDER_ID = UUID.fromString("10fe48f8-fbfc-41c6-9043-7ad9f124bacc");
    private BigDecimal PRICE = new BigDecimal("200");
    private BigDecimal PRODUCT_PRICE = new BigDecimal("50");


    @BeforeAll
    public void init() {
        createOrderCommand = CreateOrderCommand.builder()
                .restaurantId(RESTAURANT_ID)
                .customerId(CUSTOMER_ID)
                .address(OrderAddress.builder()
                        .streat("street_1")
                        .city("Yaroomba")
                        .postalCode("750356")
                        .build())
                .items(List.of(
                        OrderItem.builder().subTotal(PRODUCT_PRICE.multiply(BigDecimal.valueOf(1))).quantity(1).productId(PRODUCT_ID).price(PRODUCT_PRICE).build(),
                        OrderItem.builder().subTotal(PRODUCT_PRICE.multiply(BigDecimal.valueOf(3))).quantity(3).productId(PRODUCT_ID).price(PRODUCT_PRICE).build()))
                .price(PRICE)
                .build();

        createOrderCommandWrongPrice = CreateOrderCommand.builder()
                .restaurantId(RESTAURANT_ID)
                .customerId(CUSTOMER_ID)
                .address(OrderAddress.builder()
                        .streat("street_1")
                        .city("Yaroomba")
                        .postalCode("750356")
                        .build())
                .items(List.of(
                        OrderItem.builder().subTotal(PRODUCT_PRICE.multiply(BigDecimal.valueOf(1))).quantity(1).productId(PRODUCT_ID).price(PRODUCT_PRICE).build(),
                        OrderItem.builder().subTotal(PRODUCT_PRICE.multiply(BigDecimal.valueOf(3))).quantity(3).productId(PRODUCT_ID).price(PRODUCT_PRICE).build()))
                .price(PRICE.subtract(BigDecimal.TEN))
                .build();

        createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
                .restaurantId(RESTAURANT_ID)
                .customerId(CUSTOMER_ID)
                .address(OrderAddress.builder()
                        .streat("street_1")
                        .city("Yaroomba")
                        .postalCode("750356")
                        .build())
                .items(List.of(
                        OrderItem.builder().subTotal(PRODUCT_PRICE.multiply(BigDecimal.valueOf(1))).quantity(1).productId(PRODUCT_ID).price(PRODUCT_PRICE).build(),
                        OrderItem.builder().subTotal(PRODUCT_PRICE.multiply(BigDecimal.valueOf(3))).quantity(3).productId(PRODUCT_ID).price(PRODUCT_PRICE.add(BigDecimal.ONE)).build()))
                .price(PRICE)
                .build();

        Customer customer = new Customer();
        customer.setId(new CustomerId(CUSTOMER_ID));

        Resturant restaurantResponse = Resturant.builder()
                .restaurantId(new RestaurantId(RESTAURANT_ID))
                .products(new HashMap<ProductId, Product>() {{
                    put(new ProductId(PRODUCT_ID),
                            new Product(new ProductId(PRODUCT_ID), "product_1", new Money(PRODUCT_PRICE)));
                }})
                .active(true)
                .build();

        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(new OrderId(ORDER_ID));

        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(restaurantResponse));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
    }

    @Test
    public void testCreateOrder() {
        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
        assertEquals(PENDING, createOrderResponse.getStatus());
        assertEquals("Order created successfully.", createOrderResponse.getMessage());
        assertNotNull(createOrderResponse.getOrderTrackingId());
    }

    @Test
    public void testCreateOrderWrongPrice() {
        OrderDomainException exception =
                assertThrows(OrderDomainException.class,
                        () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));
        assertEquals("Total price 190 is not equal to the items total price 200.00!", exception.getMessage());

    }

    @Test
    public void testCreateOrderWrongProductPrice() {
        OrderDomainException exception =
                assertThrows(OrderDomainException.class,
                        () -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice));
        assertEquals("Order item price 51 is not valid for product 0e1224bd-2250-4995-ae94-dc0fa40da7f3!", exception.getMessage());

    }
}

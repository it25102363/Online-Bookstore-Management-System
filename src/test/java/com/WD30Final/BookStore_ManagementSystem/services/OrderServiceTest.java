
package com.WD30Final.BookStore_ManagementSystem.services;

import com.WD30Final.BookStore_ManagementSystem.entity.*;
import com.WD30Final.BookStore_ManagementSystem.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;

    // Prepare test data before every test
    @BeforeEach
    void setUp() {

        customer = new Customer();

        customer.setId(7L);
        customer.setName("Test Customer");
        customer.setEmail("reader@example.com");
    }

    // Helper: Create sample order item
    private OrderItem createItem(
            String title,
            int quantity,
            String price) {

        OrderItem item = new OrderItem();

        item.setBookId(1L);
        item.setBookTitle(title);
        item.setQuantity(quantity);
        item.setUnitPrice(new BigDecimal(price));

        return item;
    }

    // Helper: Create sample order
    private Order createOrder(OrderStatus status) {

        Order order = new Order();

        order.setCustomer(customer);
        order.setStatus(status);

        return order;
    }

    // =====================================
    // TEST 01 - CREATE ORDER
    // =====================================

    @Test
    void testPlaceOrderSuccessfully() {

        OrderItem javaBook =
                createItem("Java Book", 2, "2500.00");

        OrderItem pythonBook =
                createItem("Python Book", 1, "1800.00");

        when(userRepository.findByEmail(
                "reader@example.com"))
                .thenReturn(Optional.of(customer));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        Order result = orderService.placeOrder(
                "reader@example.com",
                List.of(javaBook, pythonBook)
        );

        assertNotNull(result);

        assertEquals(
                new BigDecimal("6800.00"),
                result.getTotalAmount()
        );

        assertEquals(OrderStatus.PENDING,
                result.getStatus());

        assertEquals(2, result.getItems().size());

        assertSame(customer, result.getCustomer());

        verify(orderRepository, times(1))
                .save(any(Order.class));
    }

    // =====================================
    // TEST 02 - REJECT EMPTY ORDER
    // =====================================

    @Test
    void testEmptyOrderNotAllowed() {

        when(userRepository.findByEmail(
                "reader@example.com"))
                .thenReturn(Optional.of(customer));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(
                        "reader@example.com",
                        List.of()
                )
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    // =====================================
    // TEST 03 - READ CUSTOMER ORDERS
    // =====================================

    @Test
    void testGetCustomerOrders() {

        Order order1 = createOrder(
                OrderStatus.PENDING);

        Order order2 = createOrder(
                OrderStatus.DELIVERED);

        when(userRepository.findByEmail(
                "reader@example.com"))
                .thenReturn(Optional.of(customer));

        when(orderRepository
                .findByCustomer_IdOrderByCreatedAtDesc(7L))
                .thenReturn(List.of(order1, order2));

        List<Order> result =
                orderService.getCustomerOrders(
                        "reader@example.com"
                );

        assertEquals(2, result.size());

        verify(orderRepository)
                .findByCustomer_IdOrderByCreatedAtDesc(7L);
    }

    // =====================================
    // TEST 04 - CUSTOMER OWNERSHIP CHECK
    // =====================================

    @Test
    void testCannotAccessOtherCustomerOrder() {

        when(userRepository.findByEmail(
                "reader@example.com"))
                .thenReturn(Optional.of(customer));

        when(orderRepository
                .findByIdAndCustomer_Id(55L, 7L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.getCustomerOrder(
                        55L,
                        "reader@example.com"
                )
        );
    }

    // =====================================
    // TEST 05 - READ ALL ORDERS (ADMIN)
    // =====================================

    @Test
    void testGetAllOrders() {

        Order order1 = createOrder(
                OrderStatus.PENDING);

        Order order2 = createOrder(
                OrderStatus.SHIPPED);

        when(orderRepository
                .findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(order1, order2));

        List<Order> result =
                orderService.getAllOrders();

        assertEquals(2, result.size());
    }

    // =====================================
    // TEST 06 - UPDATE ORDER STATUS
    // =====================================

    @Test
    void testUpdateOrderStatus() {

        Order order = createOrder(
                OrderStatus.PENDING);

        when(orderRepository.findById(55L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        Order result =
                orderService.updateOrderStatus(
                        55L,
                        OrderStatus.CONFIRMED
                );

        assertEquals(
                OrderStatus.CONFIRMED,
                result.getStatus()
        );

        verify(orderRepository).save(order);
    }

    // =====================================
    // TEST 07 - INVALID STATUS TRANSITION
    // =====================================

    @Test
    void testInvalidStatusUpdate() {

        Order order = createOrder(
                OrderStatus.PENDING);

        when(orderRepository.findById(55L))
                .thenReturn(Optional.of(order));

        assertThrows(
                IllegalStateException.class,
                () -> orderService.updateOrderStatus(
                        55L,
                        OrderStatus.DELIVERED
                )
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    // =====================================
    // TEST 08 - CANCEL ORDER
    // =====================================

    @Test
    void testCancelOrder() {

        Order order = createOrder(
                OrderStatus.PENDING);

        when(userRepository.findByEmail(
                "reader@example.com"))
                .thenReturn(Optional.of(customer));

        when(orderRepository
                .findByIdAndCustomer_Id(55L, 7L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        Order result = orderService.cancelOrder(
                55L,
                "reader@example.com"
        );

        assertEquals(
                OrderStatus.CANCELLED,
                result.getStatus()
        );
    }

    // =====================================
    // TEST 09 - CANNOT CANCEL SHIPPED ORDER
    // =====================================

    @Test
    void testCannotCancelShippedOrder() {

        Order order = createOrder(
                OrderStatus.SHIPPED);

        when(userRepository.findByEmail(
                "reader@example.com"))
                .thenReturn(Optional.of(customer));

        when(orderRepository
                .findByIdAndCustomer_Id(55L, 7L))
                .thenReturn(Optional.of(order));

        assertThrows(
                IllegalStateException.class,
                () -> orderService.cancelOrder(
                        55L,
                        "reader@example.com"
                )
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    // =====================================
    // TEST 10 - DELETE CANCELLED ORDER
    // =====================================

    @Test
    void testDeleteCancelledOrder() {

        Order order = createOrder(
                OrderStatus.CANCELLED);

        when(orderRepository.findById(55L))
                .thenReturn(Optional.of(order));

        orderService.deleteOrder(55L);

        verify(orderRepository, times(1))
                .delete(order);
    }

    // =====================================
    // TEST 11 - REJECT DELETE ACTIVE ORDER
    // =====================================

    @Test
    void testCannotDeleteActiveOrder() {

        Order order = createOrder(
                OrderStatus.CONFIRMED);

        when(orderRepository.findById(55L))
                .thenReturn(Optional.of(order));

        assertThrows(
                IllegalStateException.class,
                () -> orderService.deleteOrder(55L)
        );

        verify(orderRepository, never())
                .delete(any(Order.class));
    }

    // =====================================
    // TEST 12 - TOTAL ORDER COUNT
    // =====================================

    @Test
    void testTotalOrderCount() {

        when(orderRepository.count())
                .thenReturn(5L);

        long count = orderService.getTotalOrders();

        assertEquals(5L, count);
    }
}

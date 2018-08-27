package com.odde.isolated;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    OrderService orderService = spy(OrderService.class);
    BookDao mockBookDao = mock(BookDao.class);

    @Before
    public void givenBookDao() {
        when(orderService.getBookDao()).thenReturn(mockBookDao);
    }

    @Test
    public void syncbookorders_3_orders_only_2_book_order() {
        givenOrderWithTypes("Book", "3C", "Book");

        orderService.syncBookOrders();

        verifyInsertWithType("Book", 2);
    }

    private void verifyInsertWithType(final String type, int times) {
//        ArgumentCaptor<Order> captor = forClass(Order.class);
//        verify(mockBookDao, times(times)).insert(captor.capture());
//        assertThat(captor.getAllValues()).usingFieldByFieldElementComparator().containsExactly(order(type), order(type));
        verify(mockBookDao, times(times)).insert(should(order ->
                assertThat(order).isEqualToComparingFieldByField(order(type))));
    }

    public static <T> T should(Consumer<T> assertion) {
        return argThat(argument -> {
            assertion.accept(argument);
            return true;
        });
    }

    private Order order(String type) {
        return new Order() {{
            setType(type);
        }};
    }

    private void givenOrderWithTypes(String... types) {
        when(orderService.getOrders()).thenReturn(
                Arrays.stream(types)
                        .map(this::order).collect(Collectors.toList()));
    }

}

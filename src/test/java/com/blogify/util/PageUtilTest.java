package com.blogify.util;

import com.blogify.entity.Customer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PageUtilTest {

    @ParameterizedTest
    @MethodSource("provideSortParameters")
    void parseSortTest(String[] sortInput, String expectedSort) {
        // Act
        Sort sort = PageUtil.parseSort(sortInput, Customer.class);

        // Assert
        List<Sort.Order> orders = sort.toList();
        String actualSort = orders.stream()
                .map(order -> order.getProperty() + ": " + order.getDirection())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        assertEquals(expectedSort, actualSort);
    }

    private static Stream<Arguments> provideSortParameters() {
        return Stream.of(
                Arguments.of(new String[]{}, ""),
                Arguments.of(new String[]{"firstName"}, "firstName: ASC"),
                Arguments.of(new String[]{"firstName", "lastName", "desc"}, "firstName: DESC, lastName: DESC"),
                Arguments.of(new String[]{"firstName", "desc"}, "firstName: DESC"),
                Arguments.of(new String[]{"firstName", "lastName"}, "firstName: ASC, lastName: ASC"),
                Arguments.of(new String[]{"firstName", "lastName", "asc"}, "firstName: ASC, lastName: ASC"),
                Arguments.of(new String[]{"firstName", "lastName", "email", "desc"}, "firstName: DESC, lastName: DESC, email: DESC"),
                Arguments.of(new String[]{"firstName", "lastName","desc", }, "firstName: DESC, lastName: DESC")
        );
    }
}
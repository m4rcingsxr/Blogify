package com.blogify.util;

import com.blogify.exception.ApiException;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class PageUtil {

    public static Sort parseSort(String[] sorts, Class<?> entityClass) {
        if (sorts == null || sorts.length == 0) {
            return Sort.unsorted();
        }

        boolean isMultipleOrder = Arrays.stream(sorts).anyMatch(sort -> sort.contains(","));

        List<Sort.Order> orders = new ArrayList<>();

        if (isMultipleOrder) {
            // Each sort parameter is a single string like "firstName,lastName,desc"
            for (String sort : sorts) {
                String[] fields = sort.split(",");
                if (fields.length == 0) {
                    continue;
                }

                String lastField = fields[fields.length - 1];
                try {
                    Sort.Direction sortDirection = Sort.Direction.valueOf(lastField.toUpperCase());
                    for (int i = 0; i < fields.length - 1; i++) {
                        validateField(fields[i], entityClass);
                        orders.add(new Sort.Order(sortDirection, fields[i]));
                    }
                } catch (IllegalArgumentException e) {
                    // The last field is not a valid direction, treat all as ascending
                    for (String field : fields) {
                        validateField(field, entityClass);
                        orders.add(new Sort.Order(Sort.Direction.ASC, field));
                    }
                }
            }
        } else {
            // Each sort parameter is a separate string
            if (sorts.length == 1) {
                validateField(sorts[0], entityClass);
                orders.add(new Sort.Order(Sort.Direction.ASC, sorts[0]));
            } else {
                String lastField = sorts[sorts.length - 1];
                try {
                    Sort.Direction sortDirection = Sort.Direction.valueOf(lastField.toUpperCase());
                    for (int i = 0; i < sorts.length - 1; i++) {
                        validateField(sorts[i], entityClass);
                        orders.add(new Sort.Order(sortDirection, sorts[i]));
                    }
                } catch (IllegalArgumentException e) {
                    // The last field is not a valid direction, treat all as ascending
                    for (String field : sorts) {
                        validateField(field, entityClass);
                        orders.add(new Sort.Order(Sort.Direction.ASC, field));
                    }
                }
            }
        }

        return Sort.by(orders);
    }

    private void validateField(String field, Class<?> entityClass) {
        try {
            entityClass.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            throw ApiException.badRequest("Field " + field + " not found in class " + entityClass.getName());
        }
    }
}
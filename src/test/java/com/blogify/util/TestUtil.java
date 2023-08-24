package com.blogify.util;

import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

@UtilityClass
public class TestUtil {

    private static final int PAGE_SIZE = 5;
    private static final ModelMapper modelMapper = new ModelMapper();

    public static <T, R> R map(Class<R> clazz, T entity) {
        return modelMapper.map(entity, clazz);
    }

    public static PageRequest getPageRequest(int page, Sort sort) {
        return PageRequest.of(page, PAGE_SIZE, sort);
    }

    public static Sort getSort(String sortField, Sort.Direction sortDirection) {
        return Sort.by(sortDirection, sortField);
    }

    public static Sort getSortByMultipleFields(Sort.Direction sortDirection, String... sortField) {
        return Sort.by(sortDirection, sortField);
    }

    public static Sort getJoinedSort(Sort a, Sort b) {
        Sort combinedSort = Sort.unsorted();

        for (Sort.Order order : a) {
            combinedSort = combinedSort.and(Sort.by(order));
        }

        for (Sort.Order order : b) {
            combinedSort = combinedSort.and(Sort.by(order));
        }

        return combinedSort;
    }

    public static <T> boolean isPageSortedCorrectly(Page<T> page, Sort sort) {
        List<T> content = page.getContent();

        if (sort.isUnsorted() || content.isEmpty()) {
            return true; // No sorting needed or empty content
        }

        Comparator<T> combinedComparator = null;

        for (Sort.Order order : sort) {
            String property = order.getProperty();
            Sort.Direction direction = order.getDirection();

            Comparator<T> comparator = Comparator.comparing(
                    item -> (Comparable) getFieldValue(item, property),
                    Comparator.nullsFirst(Comparator.naturalOrder())
            );

            if (direction == Sort.Direction.DESC) {
                comparator = comparator.reversed();
            }

            if (combinedComparator == null) {
                combinedComparator = comparator;
            } else {
                combinedComparator = combinedComparator.thenComparing(comparator);
            }
        }

        return isListSorted(content, combinedComparator);
    }

    private static <T> boolean isListSorted(List<T> list, Comparator<T> comparator) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (comparator.compare(list.get(i), list.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }

    private static <T> Comparable<?> getFieldValue(T item, String fieldName) {
        try {
            String getterMethodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            Method getterMethod = item.getClass().getMethod(getterMethodName);
            return (Comparable<?>) getterMethod.invoke(item);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Invalid field: " + fieldName, e);
        }
    }

}

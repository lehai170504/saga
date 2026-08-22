package com.saga.shared.util;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class SearchSpecification {
    public static <T> Specification<T> searchByFields(String keyword, String... fields) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return null;
            }
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();
            for (String field : fields) {
                if (field.contains(".")) {
                    String[] parts = field.split("\\.");
                    jakarta.persistence.criteria.Path<String> path = root.get(parts[0]);
                    for (int i = 1; i < parts.length; i++) {
                        path = path.get(parts[i]);
                    }
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(path), searchPattern));
                } else {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field).as(String.class)), searchPattern));
                }
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
    
    public static <T> Specification<T> exactMatch(String field, Object value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                return null;
            }
            if (field.contains(".")) {
                String[] parts = field.split("\\.");
                jakarta.persistence.criteria.Path<Object> path = root.get(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    path = path.get(parts[i]);
                }
                return criteriaBuilder.equal(path, value);
            }
            return criteriaBuilder.equal(root.get(field), value);
        };
    }
}

//package com.hack.journal.helper;
//
//
//import org.springframework.data.domain.Sort;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
//public class QueryHelper {
//
//    public static String createQueryToFetchVariantIdBasedOnProductId(long productId) {
//        return "select variant_id from variant where product_id = " + productId;
//    }
//
//    public static String createConditionsFromParameters(List<Integer> categories, Double minPrice, Double maxPrice, List<Long> manufacturers, Double minRateValue, Double maxRateValue, List<Long> brands, String productName, List<Integer> propertyList, List<String> propertyValues) throws Exception {
//        ArrayList<String> conditionList = new ArrayList<>(8);
//
//
//        conditionList.add(minRateValue != null ? " p.average_rating >= " + minRateValue : "");
//        conditionList.add(maxRateValue != null ? " p.average_rating <= " + maxRateValue : "");
//        conditionList.add(minPrice != null ? " v.base_price >= " + minPrice : "");
//        conditionList.add(maxPrice != null ? " v.base_price <= " + maxPrice : "");
//        conditionList.add(productName != null ? " UPPER(p.product_name) LIKE '%" + productName.toUpperCase() + "%'" : "");
//
//        conditionList.add(createConditionsFromList(categories, "c.category_id"));
//        conditionList.add(createConditionsFromList(manufacturers, "m.manufacturer_id"));
//        conditionList.add(createConditionsFromList(brands, "b.brand_id"));
//
//        String conditions = "";
//
//        conditions = conditionList.stream().filter(x -> !x.isBlank()).collect(Collectors.joining(" AND "));
//
//        if (!conditions.isBlank()) {
//            conditions = " AND " + conditions;
//        }
//
//        String propertyConditions = createConditionsForProperty(propertyList, propertyValues);
//
//        if (!propertyConditions.isBlank()) {
//            propertyConditions = " AND " + propertyConditions;
//        }
//        return conditions + propertyConditions;
//    }
//
//    private static String createConditionsForProperty(List<Integer> propertyList, List<String> propertyValues) throws Exception {
//        List<String> conditions = new ArrayList<>();
//        if (propertyList == null) {
//            return "";
//        }
//        if (propertyList.size() != propertyValues.size()) {
//            throw new RuntimeException("Property list and property sizes list must be of same size");
//        }
//
//        for (int i = 0; i < propertyList.size(); i++) {
//            conditions.add(" (vp.property_id = " + propertyList.get(i) + " and " + " vp.value = '" + propertyValues.get(i).strip() + "')");
//        }
//
//        return String.join(" and ", conditions);
//
//    }
//
//    private static String createConditionsFromList(List<? extends Number> listOfConditions, String column) {
//        if (listOfConditions != null && !listOfConditions.isEmpty()) {
//            return column + " IN " + listOfConditions.stream().map(Object::toString).collect(Collectors.joining(", ", "(", ")"));
//        }
//        return "";
//    }
//
//    public static String createOrderingScheme(List<String> sortList, List<String> sortOrder) throws Exception {
//        if (sortList.size() != sortOrder.size()) {
//            throw new Exception("sort list and sort order list must be of same length");
//        }
//        ArrayList<String> orderList = new ArrayList<>();
//        HashMap<String, String> propertyDict = new HashMap<>();
//        propertyDict.put("productName", "p.product_name");
//        propertyDict.put("basePrice", "v.base_price");
//        for (int i = 0; i < sortList.size(); i++) {
//            if (propertyDict.containsKey(sortList.get(i))) {
//                String order = Objects.equals(sortOrder.get(i), "DESC") ? " DESC" : " ASC";
//                orderList.add(propertyDict.get(sortList.get(i)) + order);
//            }
//        }
//        if (orderList.isEmpty()) {
//            return "";
//        }
//        return " ORDER BY " + orderList.stream().collect(Collectors.joining(",", " ", " "));
//    }
//
//    public static String createQueryToGetVariantsOfProductBasedOnProperties(long productId, List<String> propertyNames, List<String> values) {
//
//        String CONDITION_TEMPLATE = " ip.property_name = '%s' AND vp.value = '%s' ";
//        String BASE_QUERY = "select v.variant_id from variant v " +
//                " join variant_property vp on v.variant_id = vp.variant_id " +
//                "join item_property ip on vp.property_id = ip.property_id where " +
//                "v.product_id =  " + productId + " and ";
//
//        if (propertyNames == null || values == null || propertyNames.size() != values.size()) {
//            throw new RuntimeException("Property names and values must be equal size and can't be null");
//        }
//        List<String> conditionsList = new ArrayList<>();
//        for (int i = 0; i < propertyNames.size(); i++) {
//            conditionsList.add(CONDITION_TEMPLATE.formatted(propertyNames.get(i), values.get(i)));
//        }
//
//        String conditions = conditionsList.stream().collect(Collectors.joining(" intersect " + BASE_QUERY));
//
//        return BASE_QUERY + conditions;
//
//
//    }
//
//    public static List<Sort.Order> createSortOrder(List<String> sortList, String sortOrder) {
//        List<Sort.Order> sorts = new ArrayList<>();
//        Sort.Direction direction;
//
//        for (String sort : sortList) {
//            if (sortOrder != null) {
//                direction = Sort.Direction.fromString(sortOrder);
//            } else {
//                direction = Sort.Direction.ASC;
//            }
//            sorts.add(new Sort.Order(direction, sort));
//        }
//        return sorts;
//    }
//
//
//}

package com.aca.shoppingapi.model.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSummaryDto {
    private String category;
    private long totalOrders;
    private double totalQuantity;
    private double totalAmount;

    public OrderSummaryDto(String category, long totalOrders, double totalQuantity, double totalAmount) {
        this.category = category;
        this.totalOrders = totalOrders;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
    }
}

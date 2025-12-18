package com.bitsquad.ordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OrderSummaryResponse {

    private Long userId;
    private long totalOrders;

    private long created;
    private long processing;
    private long completed;
    private long cancelled;
}

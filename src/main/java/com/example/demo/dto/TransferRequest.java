package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 转账请求体：POST /user/transfer
 * {"fromId": 1, "toId": 2, "amount": 100.00}
 */
@Data
public class TransferRequest {
    private Long fromId;
    private Long toId;
    private BigDecimal amount;
}

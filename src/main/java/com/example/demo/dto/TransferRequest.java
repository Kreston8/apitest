package com.example.demo.dto;

import com.example.demo.enums.PayMethod;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 转账请求体：POST /user/transfer
 * {"fromId": 1, "toId": 2, "amount": 100.00, "payMethod": "ALIPAY"}
 * payMethod 可选：CASH 现金（默认）/ ALIPAY 支付宝 / WECHAT 微信，也兼容小写或中文写法
 */
@Data
public class TransferRequest {
    private Long fromId;
    private Long toId;
    private BigDecimal amount;

    /** 转账方式：缺省为 null，Service 层默认按现金处理 */
    private PayMethod payMethod;
}

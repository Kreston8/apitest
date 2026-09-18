package com.example.demo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 转账方式：CASH 现金 / ALIPAY 支付宝 / WECHAT 微信。
 * JSON 里三种写法都兼容：枚举名（CASH）、小写（cash）、中文（现金）。
 */
@Getter
public enum PayMethod {

    CASH("现金"),
    ALIPAY("支付宝"),
    WECHAT("微信");

    /** 中文描述：响应用户/日志用 */
    private final String desc;

    PayMethod(String desc) {
        this.desc = desc;
    }

    /**
     * Jackson 反序列化入口（@JsonCreator）：把 JSON 里的字符串转成枚举。
     * 支持 "CASH"/"cash"/"Cash"/"现金" 任意写法；null/空串返回 null（由调用方给默认值）。
     * 不认识的字符串抛 IllegalArgumentException，由全局异常处理器转成 400。
     */
    @JsonCreator
    public static PayMethod of(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        for (PayMethod m : values()) {
            if (m.name().equalsIgnoreCase(trimmed) || m.desc.equals(trimmed)) {
                return m;
            }
        }
        throw new IllegalArgumentException("不支持的转账方式: " + value);
    }

    /**
     * Jackson 序列化出口（@JsonValue）：返回 JSON 时用枚举名（CASH），简洁且与请求写法一致。
     */
    @JsonValue
    public String toJson() {
        return name();
    }
}

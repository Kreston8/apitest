package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("t_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private Integer age;
    private String email;
    /** 账户余额(存款)，对应 DECIMAL(12,2)，用 BigDecimal 避免浮点精度丢失 */
    private BigDecimal balance;
}

package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 原子扣减余额：条件 UPDATE 保证余额不足时影响行数为 0，并发转账也不会扣成负数。
     */
    @Update("UPDATE t_user SET balance = balance - #{amount} WHERE id = #{fromId} AND balance >= #{amount}")
    int deductBalance(@Param("fromId") Long fromId, @Param("amount") BigDecimal amount);

    /**
     * 增加余额：影响行数为 0 说明转入账户不存在。
     */
    @Update("UPDATE t_user SET balance = balance + #{amount} WHERE id = #{toId}")
    int addBalance(@Param("toId") Long toId, @Param("amount") BigDecimal amount);
}

package com.example.demo.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.repository.AbstractRepository;
import com.example.demo.entity.User;
import com.example.demo.enums.PayMethod;
import com.example.demo.exception.TransferException;
import com.example.demo.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;

@Service
public class UserService extends AbstractRepository<UserMapper, User> {

    @Resource
    private UserMapper userMapper;

    @Override
    public BaseMapper<User> getBaseMapper() {
        return userMapper;
    }

    @Override
    public boolean saveBatch(Collection<User> entityList, int batchSize) {
        for (User user : entityList) {
            userMapper.insert(user);
        }
        return true;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<User> entityList, int batchSize) {
        for (User user : entityList) {
            if (user.getId() != null) {
                userMapper.updateById(user);
            } else {
                userMapper.insert(user);
            }
        }
        return true;
    }

    @Override
    public boolean updateBatchById(Collection<User> entityList, int batchSize) {
        for (User user : entityList) {
            userMapper.updateById(user);
        }
        return true;
    }

    /**
     * 账户间转账：@Transactional 保证"扣款 + 入账"两步要么全部成功、要么全部回滚。
     * 任何一步失败都会抛 TransferException → 整个事务回滚，双方余额保持原状。
     *
     * @param payMethod 转账方式（CASH/ALIPAY/WECHAT），传 null 时默认按现金处理
     */
    @Transactional(rollbackFor = Exception.class)
    public void transfer(Long fromId, Long toId, BigDecimal amount, PayMethod payMethod) {
        // 1. 参数校验（在事务内，抛异常即回滚，虽然此时还没写库）
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferException("转账金额必须大于 0");
        }
        if (fromId == null || toId == null) {
            throw new TransferException("转出/转入账户不能为空");
        }
        if (fromId.equals(toId)) {
            throw new TransferException("不能给自己转账");
        }

        // 2. 转账方式：缺省默认现金（枚举取值由 Jackson 反序列化保证合法）
        PayMethod method = (payMethod == null) ? PayMethod.CASH : payMethod;

        // 3. 先扣转出方余额：条件 UPDATE（balance >= amount），
        //    余额不足或转出账户不存在时影响行数为 0，并发下也不会扣成负数
        int deducted = userMapper.deductBalance(fromId, amount);
        if (deducted == 0) {
            throw new TransferException("转出账户不存在或余额不足");
        }

        // 4. 再给转入方入账：账户不存在时影响行数为 0，
        //    抛异常触发回滚，把第 3 步的扣款一并撤销 —— 这就是"要么全部成功，要么全部失败"
        int added = userMapper.addBalance(toId, amount);
        if (added == 0) {
            throw new TransferException("转入账户不存在");
        }
    }
}

package com.example.demo.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.repository.AbstractRepository;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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
}

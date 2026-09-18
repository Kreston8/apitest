package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.dto.TransferRequest;
import com.example.demo.entity.Result;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    // 查询全部
    @GetMapping("/list")
    public Result<List<User>> list(){
        return Result.success(userService.list());
    }

    // 根据id查询单个
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id){
        return Result.success(userService.getById(id));
    }

    // 新增
    @PostMapping
    public Result<Boolean> add(@RequestBody User user){
        boolean save = userService.save(user);
        return Result.success(save);
    }

    // 修改
    @PutMapping
    public Result<Boolean> update(@RequestBody User user){
        boolean b = userService.updateById(user);
        return Result.success(b);
    }

    // 删除
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id){
        boolean b = userService.removeById(id);
        return Result.success(b);
    }

    // 条件查询：用户名模糊 + 年龄区间
    @GetMapping("/query")
    public Result<List<User>> query(@RequestParam(required = false) String username,
                                    @RequestParam(required = false) Integer minAge,
                                    @RequestParam(required = false) Integer maxAge){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if(username != null){
            wrapper.like(User::getUsername, username);
        }
        if(minAge != null){
            wrapper.ge(User::getAge, minAge);
        }
        if(maxAge != null){
            wrapper.le(User::getAge, maxAge);
        }
        return Result.success(userService.list(wrapper));
    }

    // 转账：同一事务内扣款 + 入账，要么全部成功，要么全部失败（失败时余额不变）
    // payMethod 可选：CASH（默认）/ ALIPAY / WECHAT，也兼容小写或中文写法
    @PostMapping("/transfer")
    public Result<Boolean> transfer(@RequestBody TransferRequest req){
        userService.transfer(req.getFromId(), req.getToId(), req.getAmount(), req.getPayMethod());
        return Result.success(true);
    }
}

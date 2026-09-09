package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.Result;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

    // 条件模糊查询
    @GetMapping("/query")
    public Result<List<User>> query(@RequestParam(required = false) String username){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if(username != null){
            wrapper.like(User::getUsername, username);
        }
        return Result.success(userService.list(wrapper));
    }
}

package com.example.demo.exception;

import com.example.demo.entity.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：所有接口的异常统一转成 Result，前端契约保持 {code, msg, data} 不变。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 转账业务异常 → 400 + 具体原因（触发回滚后前端拿到明确提示）
    @ExceptionHandler(TransferException.class)
    public Result<Void> handleTransfer(TransferException e) {
        return Result.fail(400, e.getMessage());
    }

    // 兜底：未预期异常 → 500，不让原始堆栈直接暴露给前端
    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception e) {
        return Result.fail(500, "系统异常：" + e.getMessage());
    }
}

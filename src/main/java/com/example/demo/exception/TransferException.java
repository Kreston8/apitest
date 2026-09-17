package com.example.demo.exception;

/**
 * 转账业务异常：抛出即触发事务回滚，由全局异常处理器转成 Result.fail(400, msg)。
 */
public class TransferException extends RuntimeException {
    public TransferException(String message) {
        super(message);
    }
}

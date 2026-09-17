DROP TABLE IF EXISTS t_user;

CREATE TABLE t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键id',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    age INT COMMENT '年龄',
    email VARCHAR(100) COMMENT '邮箱',
    balance DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '账户余额(存款)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 初始化测试数据：三人初始余额随机不等
INSERT INTO t_user(username,age,email,balance) VALUES
('zhangsan',22,'zhangsan@test.com',12800.50),
('lisi',25,'lisi@test.com',9377.25),
('wangwu',30,'wangwu@test.com',15666.80);

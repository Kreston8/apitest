DROP TABLE IF EXISTS t_user;

CREATE TABLE t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键id',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    age INT COMMENT '年龄',
    email VARCHAR(100) COMMENT '邮箱',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 初始化测试数据
INSERT INTO t_user(username,age,email) VALUES
('zhangsan',22,'zhangsan@test.com'),
('lisi',25,'lisi@test.com');

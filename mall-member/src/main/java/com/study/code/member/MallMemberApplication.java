package com.study.code.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.study.code.member.mapper")
@SpringBootApplication(scanBasePackages = {"com.study.code.commons.exception", "com.study.code.member"})
@EnableDiscoveryClient // 开启服务的注册与发现
@EnableFeignClients("com.study.code.commons.feign") // 开启feign调用
public class MallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallMemberApplication.class, args);
    }

}

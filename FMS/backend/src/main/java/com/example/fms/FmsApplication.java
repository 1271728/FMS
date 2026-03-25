package com.example.fms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan({
        "com.example.fms.modules.auth.mapper",
        "com.example.fms.modules.admin.user.mapper",
        "com.example.fms.modules.admin.org.mapper",
        "com.example.fms.modules.project.mapper",
        "com.example.fms.modules.budget.mapper",
        "com.example.fms.modules.reimburse.mapper",
        "com.example.fms.modules.budgetAdjust.mapper",
        "com.example.fms.modules.workflow.mapper",
        "com.example.fms.modules.pay.mapper",
        "com.example.fms.modules.archive.mapper",
        "com.example.fms.modules.msg.mapper",
        "com.example.fms.modules.dashboard.mapper",
        "com.example.fms.modules.report.mapper"
})
public class FmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FmsApplication.class, args);
    }

}
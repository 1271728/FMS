package com.example.fms.modules.pay.controller;

import com.example.fms.common.api.ApiResponse;
import com.example.fms.modules.pay.dto.PayCreateOrUpdateReq;
import com.example.fms.modules.pay.dto.PaymentVO;
import com.example.fms.modules.pay.service.PayService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pay")
public class PayController {

    private final PayService payService;

    public PayController(PayService payService) {
        this.payService = payService;
    }

    @PostMapping("/createOrUpdate")
    public ApiResponse<Void> createOrUpdate(@RequestBody PayCreateOrUpdateReq req) {
        payService.createOrUpdate(req);
        return ApiResponse.ok(null);
    }

    @GetMapping("/detail")
    public ApiResponse<PaymentVO> detail(@RequestParam("reimbId") Long reimbId) {
        return ApiResponse.ok(payService.detailByReimbId(reimbId));
    }
}

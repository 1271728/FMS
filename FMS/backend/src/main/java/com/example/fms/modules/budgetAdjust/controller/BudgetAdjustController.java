package com.example.fms.modules.budgetAdjust.controller;

import com.example.fms.common.api.ApiResponse;
import com.example.fms.common.api.PageResult;
import com.example.fms.modules.budgetAdjust.dto.*;
import com.example.fms.modules.budgetAdjust.service.BudgetAdjustService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budgetAdjust")
public class BudgetAdjustController {

    private final BudgetAdjustService budgetAdjustService;

    public BudgetAdjustController(BudgetAdjustService budgetAdjustService) {
        this.budgetAdjustService = budgetAdjustService;
    }

    @PostMapping("/page")
    public ApiResponse<PageResult<BudgetAdjustVO>> page(@RequestBody(required = false) BudgetAdjustPageReq req) {
        return ApiResponse.ok(budgetAdjustService.page(req));
    }

    @GetMapping("/detail")
    public ApiResponse<BudgetAdjustDetailVO> detail(@RequestParam("id") Long id) {
        return ApiResponse.ok(budgetAdjustService.detail(id));
    }

    @PostMapping("/create")
    public ApiResponse<Long> create(@RequestBody BudgetAdjustCreateReq req) {
        return ApiResponse.ok(budgetAdjustService.create(req));
    }

    @PostMapping("/update")
    public ApiResponse<Void> update(@RequestBody BudgetAdjustUpdateReq req) {
        budgetAdjustService.update(req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(@RequestBody BudgetAdjustIdReq req) {
        budgetAdjustService.delete(req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/submit")
    public ApiResponse<Void> submit(@RequestBody BudgetAdjustIdReq req) {
        budgetAdjustService.submit(req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/withdraw")
    public ApiResponse<Void> withdraw(@RequestBody BudgetAdjustIdReq req) {
        budgetAdjustService.withdraw(req);
        return ApiResponse.ok(null);
    }
}

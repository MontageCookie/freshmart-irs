package com.freshmart.irs.api;

import com.freshmart.irs.common.ApiResponse;
import com.freshmart.irs.common.PageResponse;
import com.freshmart.irs.config.OpenApiConfig;
import com.freshmart.irs.dto.alert.AlertActionCreateRequest;
import com.freshmart.irs.dto.alert.AlertActionResponse;
import com.freshmart.irs.dto.alert.AlertDetailResponse;
import com.freshmart.irs.dto.alert.AlertListItemResponse;
import com.freshmart.irs.enums.AlertStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Alerts")
@RestController
@RequestMapping("/api/v1/alerts")
public class AlertsController {

    @Operation(summary = "预警列表（分页，占位）", description = "鉴权：JWT；角色：STORE_MANAGER/WAREHOUSE")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping
    public ApiResponse<PageResponse<AlertListItemResponse>> list(
            @Parameter(description = "预警类型：LOW_STOCK/NEAR_EXPIRY/EXPIRED") @RequestParam(required = false) String alertType,
            @Parameter(description = "预警状态：OPEN/ACKED/RESOLVED/CLOSED") @RequestParam(required = false) AlertStatus status,
            @Parameter(description = "商品 ID") @RequestParam(required = false) Long productId,
            @Parameter(description = "库存批次 ID") @RequestParam(required = false) Long batchId,
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小，默认 10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序：sort=field,asc|desc，可重复传参") @RequestParam(required = false) List<String> sort
    ) {
        PageResponse<AlertListItemResponse> data = new PageResponse<>(
                page,
                size,
                1,
                List.of(new AlertListItemResponse(
                        1,
                        "LOW_STOCK",
                        AlertStatus.OPEN,
                        1L,
                        null,
                        20,
                        null,
                        Instant.now(),
                        null
                ))
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "预警详情（占位）", description = "鉴权：JWT；角色：STORE_MANAGER/WAREHOUSE")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping("/{id}")
    public ApiResponse<AlertDetailResponse> get(@PathVariable long id) {
        List<AlertActionResponse> actions = List.of(
                new AlertActionResponse(1, id, 3, "ACK", "已确认", Instant.now())
        );
        AlertDetailResponse data = new AlertDetailResponse(
                id,
                "NEAR_EXPIRY",
                AlertStatus.ACKED,
                1L,
                1L,
                null,
                LocalDate.now().plusDays(2),
                Instant.now(),
                null,
                actions
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "新增预警处置动作（占位）", description = "鉴权：JWT；角色：STORE_MANAGER/WAREHOUSE")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PostMapping("/{id}/actions")
    public ApiResponse<AlertActionResponse> addAction(@PathVariable long id, @Valid @RequestBody AlertActionCreateRequest request) {
        AlertActionResponse data = new AlertActionResponse(
                1,
                id,
                3,
                request.actionType(),
                request.actionResult(),
                Instant.now()
        );
        return ApiResponse.ok(data);
    }
}

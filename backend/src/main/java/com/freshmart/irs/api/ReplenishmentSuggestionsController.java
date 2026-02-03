package com.freshmart.irs.api;

import com.freshmart.irs.common.ApiResponse;
import com.freshmart.irs.common.PageResponse;
import com.freshmart.irs.config.OpenApiConfig;
import com.freshmart.irs.dto.replenishment.ReplenishmentApproveRequest;
import com.freshmart.irs.dto.replenishment.ReplenishmentApproveResponse;
import com.freshmart.irs.dto.replenishment.ReplenishmentDetailResponse;
import com.freshmart.irs.dto.replenishment.ReplenishmentGenerateRequest;
import com.freshmart.irs.dto.replenishment.ReplenishmentGenerateResponse;
import com.freshmart.irs.dto.replenishment.ReplenishmentItemResponse;
import com.freshmart.irs.dto.replenishment.ReplenishmentListItemResponse;
import com.freshmart.irs.dto.replenishment.ReplenishmentRejectRequest;
import com.freshmart.irs.dto.replenishment.ReplenishmentRejectResponse;
import com.freshmart.irs.enums.ReplenishmentApprovalStatus;
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
import java.util.List;

@Tag(name = "Replenishment")
@RestController
@RequestMapping("/api/v1/replenishment-suggestions")
public class ReplenishmentSuggestionsController {

    @Operation(summary = "触发预测并生成建议采购单（占位）", description = "鉴权：JWT；角色：STORE_MANAGER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PostMapping("/generate")
    public ApiResponse<ReplenishmentGenerateResponse> generate(@Valid @RequestBody ReplenishmentGenerateRequest request) {
        ReplenishmentGenerateResponse data = new ReplenishmentGenerateResponse(
                1,
                "RS-20260204-0001",
                ReplenishmentApprovalStatus.PENDING,
                Instant.now()
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "建议采购单列表（分页，占位）", description = "鉴权：JWT；角色：STORE_MANAGER/WAREHOUSE")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping
    public ApiResponse<PageResponse<ReplenishmentListItemResponse>> list(
            @Parameter(description = "状态：PENDING/APPROVED/REJECTED") @RequestParam(required = false) ReplenishmentApprovalStatus status,
            @Parameter(description = "生成时间起（ISO-8601）") @RequestParam(required = false) Instant generatedFrom,
            @Parameter(description = "生成时间止（ISO-8601）") @RequestParam(required = false) Instant generatedTo,
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小，默认 10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序：sort=field,asc|desc，可重复传参") @RequestParam(required = false) List<String> sort
    ) {
        PageResponse<ReplenishmentListItemResponse> data = new PageResponse<>(
                page,
                size,
                1,
                List.of(new ReplenishmentListItemResponse(
                        1,
                        "RS-20260204-0001",
                        ReplenishmentApprovalStatus.PENDING,
                        null,
                        null,
                        Instant.now()
                ))
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "建议采购单详情（占位）", description = "鉴权：JWT；角色：STORE_MANAGER/WAREHOUSE")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @GetMapping("/{id}")
    public ApiResponse<ReplenishmentDetailResponse> get(@PathVariable long id) {
        List<ReplenishmentItemResponse> items = List.of(
                new ReplenishmentItemResponse(1, 10, 7, 20, 5, 85)
        );
        ReplenishmentDetailResponse data = new ReplenishmentDetailResponse(
                id,
                "RS-20260204-0001",
                ReplenishmentApprovalStatus.PENDING,
                null,
                null,
                null,
                Instant.now(),
                items
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "店长审批通过（占位）", description = "鉴权：JWT；角色：STORE_MANAGER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PostMapping("/{id}/approve")
    public ApiResponse<ReplenishmentApproveResponse> approve(@PathVariable long id, @Valid @RequestBody ReplenishmentApproveRequest request) {
        ReplenishmentApproveResponse data = new ReplenishmentApproveResponse(
                id,
                ReplenishmentApprovalStatus.APPROVED,
                2,
                request.approvedAt() == null ? Instant.now() : request.approvedAt()
        );
        return ApiResponse.ok(data);
    }

    @Operation(summary = "店长驳回（占位）", description = "鉴权：JWT；角色：STORE_MANAGER")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER)
    @PostMapping("/{id}/reject")
    public ApiResponse<ReplenishmentRejectResponse> reject(@PathVariable long id, @Valid @RequestBody ReplenishmentRejectRequest request) {
        ReplenishmentRejectResponse data = new ReplenishmentRejectResponse(
                id,
                ReplenishmentApprovalStatus.REJECTED,
                2,
                Instant.now(),
                request.rejectReason()
        );
        return ApiResponse.ok(data);
    }
}

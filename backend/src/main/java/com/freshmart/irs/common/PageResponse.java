package com.freshmart.irs.common;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "PageResponse", description = "分页响应模型")
public record PageResponse<T>(
        @Schema(description = "页码，从 1 开始", example = "1") int page,
        @Schema(description = "每页大小", example = "10") int size,
        @Schema(description = "总数", example = "0") long total,
        @Schema(description = "数据列表") List<T> items
) {
    public static <T> PageResponse<T> empty(int page, int size) {
        return new PageResponse<>(page, size, 0, List.of());
    }
}


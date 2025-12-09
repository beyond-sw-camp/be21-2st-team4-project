package com.ohgiraffers.order.core.api.command.response;

public record ProductResponse(
        Long id,
        String imageUrl,
        String name
) {
}

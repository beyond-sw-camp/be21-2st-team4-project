package com.ohgiraffers.timedeal.core.api.controller.v1;

import com.ohgiraffers.timedeal.core.api.controller.v1.request.PromotionRequest;
import com.ohgiraffers.timedeal.core.domain.Promotion;
import com.ohgiraffers.timedeal.core.domain.PromotionService;
import com.ohgiraffers.timedeal.core.enums.PromotionStatus;
import com.ohgiraffers.timedeal.core.support.response.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class PromotionController {
    private PromotionService promotionService;

    @Autowired
    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @PostMapping("/api/v1/promotion")
    public ApiResult<Promotion> save(@RequestBody PromotionRequest promotionRequest) {

        return ApiResult.success(promotionService.promotionSave(promotionRequest));
    }
    @PutMapping("/api/v1/promotion")
        public ApiResult<Promotion> promotionUpdateStatusById(@RequestParam(name = "promotionId") Long promotionId) {
            return ApiResult.success(promotionService.promotionUpdateStatusById(promotioId));
        }
}

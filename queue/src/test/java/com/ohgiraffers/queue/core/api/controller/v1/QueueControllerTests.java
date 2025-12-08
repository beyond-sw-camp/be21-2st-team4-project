package com.ohgiraffers.queue.core.api.controller.v1;

import com.ohgiraffers.common.support.error.GlobalExceptionHandler;
import com.ohgiraffers.queue.core.api.controller.v1.response.QueueStatusResponse;
import com.ohgiraffers.queue.core.domain.QueueService;
import com.ohgiraffers.queue.core.enums.QueueStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("대기열 컨트롤러 단위 테스트")
@WebMvcTest(controllers = QueueController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class QueueControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    QueueService queueService;

    @Nested
    @DisplayName("대기열 진입 API")
    class EnterQueue {
        private static final Integer MAX_USER = 100;

        @Test
        @DisplayName("처음 대기열 진입 시 대기 순번을 반환")
        void firstEnterQueue() throws Exception {
            // given
            Long dealId = 1L;
            Long userId = 100L;
            QueueStatusResponse queueResponse = new QueueStatusResponse(1L, 0L,0L, QueueStatus.WAITING);

            given(queueService.enterQueue(dealId, userId)).willReturn(queueResponse);

            // when & then
            mockMvc.perform(post("/api/v1/queue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "timedealId": 1,
                                        "userId": 100
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.position").value(1L))
                    .andExpect(jsonPath("$.data.waitingTime").value(0L))
                    .andExpect(jsonPath("$.data.status").value("WAITING"));
        }

        @Test
        @DisplayName("중복 대기열 진입 시 새로운 대기 순번을 반환")
        void duplicateEnterQueue() throws Exception {
            // given
            Long dealId = 1L;
            Long userId = 100L;
            QueueStatusResponse queueResponse = new QueueStatusResponse(5L, 0L,50L, QueueStatus.WAITING);

            given(queueService.enterQueue(dealId, userId)).willReturn(queueResponse);

            // when & then
            mockMvc.perform(post("/api/v1/queue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "timedealId": 1,
                                        "userId": 100
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.position").value(5))
                    .andExpect(jsonPath("$.data.waitingTime").value(50))
                    .andExpect(jsonPath("$.data.status").value("WAITING"));
        }

        @Test
        @DisplayName("userId가 없으면 400 에러를 반환")
        void enterQueueWithoutUserId() throws Exception {
            // when & then
            mockMvc.perform(post("/api/v1/queue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "userId": 1
                                    }
                                    """))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("dealId가 없으면 400 에러를 반환")
        void enterQueueWithoutDealId() throws Exception {
            // when & then
            mockMvc.perform(post("/api/v1/queue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "timedealId": 1
                                    }
                                    """))
                    .andExpect(status().isBadRequest());
        }
    }
}
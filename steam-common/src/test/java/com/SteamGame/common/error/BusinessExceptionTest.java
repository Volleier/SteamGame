package com.SteamGame.common.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionTest {

    @Test
    void holdsErrorCode() {
        BusinessException ex = new BusinessException(ErrorCode.STEAM_CREDENTIAL_NOT_FOUND);
        assertEquals(ErrorCode.STEAM_CREDENTIAL_NOT_FOUND, ex.getErrorCode());
        assertEquals("Steam 凭据未配置", ex.getMessage());
    }

    @Test
    void holdsErrorCodeWithCustomMessage() {
        BusinessException ex = new BusinessException(ErrorCode.GAME_SYNC_FAILED, "同步失败：网络错误");
        assertEquals(ErrorCode.GAME_SYNC_FAILED, ex.getErrorCode());
        assertEquals("同步失败：网络错误", ex.getMessage());
    }

    @Test
    void isRuntimeException() {
        BusinessException ex = new BusinessException(ErrorCode.INTERNAL_ERROR);
        assertTrue(ex instanceof RuntimeException);
    }
}

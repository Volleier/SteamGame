package com.SteamGame.common.response;

import com.SteamGame.common.error.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void okWithData() {
        ApiResponse<String> resp = ApiResponse.ok("hello");
        assertEquals(200, resp.getCode());
        assertEquals("success", resp.getMsg());
        assertEquals("hello", resp.getData());
        assertTrue(resp.isSuccess());
    }

    @Test
    void okWithoutData() {
        ApiResponse<Void> resp = ApiResponse.ok();
        assertEquals(200, resp.getCode());
        assertNull(resp.getData());
        assertTrue(resp.isSuccess());
    }

    @Test
    void failWithCodeAndMsg() {
        ApiResponse<Void> resp = ApiResponse.fail(500, "server error");
        assertEquals(500, resp.getCode());
        assertEquals("server error", resp.getMsg());
        assertNull(resp.getData());
        assertFalse(resp.isSuccess());
    }

    @Test
    void failWithErrorCode() {
        ApiResponse<Void> resp = ApiResponse.fail(ErrorCode.STEAM_CREDENTIAL_NOT_FOUND);
        assertEquals(1001, resp.getCode());
        assertEquals("Steam 凭据未配置", resp.getMsg());
        assertNull(resp.getData());
    }

    @Test
    void failWithErrorCodeAndCustomMessage() {
        ApiResponse<Void> resp = ApiResponse.fail(ErrorCode.STEAM_API_TIMEOUT, "请求超时，请重试");
        assertEquals(2001, resp.getCode());
        assertEquals("请求超时，请重试", resp.getMsg());
    }

    @Test
    void getMessageAliasWorks() {
        ApiResponse<String> resp = ApiResponse.ok("data");
        assertEquals("success", resp.getMessage());
    }
}

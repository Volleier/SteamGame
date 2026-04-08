package com.SteamGame.login.controller;

import com.SteamGame.login.dto.ApiResponse;
import com.SteamGame.login.dto.CredentialInputDTO;
import com.SteamGame.login.service.CredentialConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 凭据配置相关接口控制器（原 RegisterController）
 */
@RestController
@RequestMapping("/api/credentials")
public class CredentialConfigController {

    private static final Logger logger = LoggerFactory.getLogger(CredentialConfigController.class);

    private final CredentialConfigService registerService;

    // SteamApiService 可选，暂不直接依赖；若需要在线校验可通过 RestTemplate 实现

    @Autowired
    public CredentialConfigController(CredentialConfigService registerService) {
        this.registerService = registerService;
    }

    public void register() {
        // 应用初始化时的凭据配置初始化逻辑，可调用 service
    }

    @PostMapping("/configure")
    public ResponseEntity<ApiResponse<Object>> configureCredentials(@RequestBody CredentialInputDTO input) {
        // 控制器只负责接收参数并转发给服务层，服务层返回统一 ApiResponse
        ApiResponse<Object> resp = registerService.saveCredentialInfo(input);
        if (resp != null && resp.isSuccess()) {
            return ResponseEntity.status(201).body(resp);
        }
        // 对失败情况，直接返回服务层的统一响应（HTTP 200），客户端通过 code 判断具体语义
        return ResponseEntity
                .ok(resp != null ? resp : ApiResponse.fail(com.SteamGame.login.dto.ResultCode.INTERNAL_ERROR, "未知错误"));
    }
}

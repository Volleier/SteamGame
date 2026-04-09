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

    private final com.SteamGame.login.service.CredentialConfigService credentialConfigService;
    private final com.SteamGame.login.service.CredentialService credentialService;

    // SteamApiService 可选，暂不直接依赖；若需要在线校验可通过 RestTemplate 实现

    @Autowired
    public CredentialConfigController(CredentialConfigService credentialConfigService,
            com.SteamGame.login.service.CredentialService credentialService) {
        this.credentialConfigService = credentialConfigService;
        this.credentialService = credentialService;
    }

    public void init() {
        // 应用初始化时的凭据配置初始化逻辑，可调用 service
    }

    @PostMapping("/configure")
    public ResponseEntity<ApiResponse<Object>> configureCredentials(@RequestBody CredentialInputDTO input) {
        // 控制器只负责接收参数并委托到编排服务进行保存并立即校验
        ApiResponse<Object> resp = credentialService.registerAndValidate(input);
        if (resp != null && resp.isSuccess()) {
            return ResponseEntity.status(201).body(resp);
        }
        return ResponseEntity
                .ok(resp != null ? resp : ApiResponse.fail(com.SteamGame.login.dto.ResultCode.INTERNAL_ERROR, "未知错误"));
    }

}

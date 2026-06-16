package com.SteamGame.api.controller;

import com.SteamGame.api.dto.dashboard.CardLayoutDTO;
import com.SteamGame.common.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard/layout")
public class DashboardLayoutController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardLayoutController.class);

    @Value("${login.config.path:auth.yaml}")
    private String configPath;

    @GetMapping
    public ApiResponse<List<CardLayoutDTO>> getLayout() {
        List<CardLayoutDTO> layouts = new ArrayList<>();
        File file = new File(configPath);
        if (!file.exists()) {
            return ApiResponse.ok(layouts);
        }

        try (InputStream is = Files.newInputStream(file.toPath())) {
            Yaml yaml = new Yaml();
            Object loaded = yaml.load(is);
            if (loaded instanceof Map) {
                Map<String, Object> root = (Map<String, Object>) loaded;
                Object dashboardObj = root.get("dashboard");
                if (dashboardObj instanceof Map) {
                    Map<String, Object> dashboard = (Map<String, Object>) dashboardObj;
                    Object layoutObj = dashboard.get("layout");
                    if (layoutObj instanceof List) {
                        List<?> list = (List<?>) layoutObj;
                        for (Object item : list) {
                            if (item instanceof Map) {
                                Map<?, ?> map = (Map<?, ?>) item;
                                CardLayoutDTO dto = new CardLayoutDTO();
                                if (map.get("id") instanceof Number) {
                                    dto.setId(((Number) map.get("id")).intValue());
                                }
                                if (map.get("x") instanceof Number) {
                                    dto.setX(((Number) map.get("x")).intValue());
                                }
                                if (map.get("y") instanceof Number) {
                                    dto.setY(((Number) map.get("y")).intValue());
                                }
                                if (map.get("w") instanceof Number) {
                                    dto.setW(((Number) map.get("w")).intValue());
                                }
                                if (map.get("visible") instanceof Boolean) {
                                    dto.setVisible((Boolean) map.get("visible"));
                                } else {
                                    dto.setVisible(true); // default to visible
                                }
                                layouts.add(dto);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to read dashboard layout from auth.yaml", e);
        }

        return ApiResponse.ok(layouts);
    }

    @PostMapping
    public ApiResponse<Boolean> saveLayout(@RequestBody List<CardLayoutDTO> layouts) {
        try {
            Map<String, Object> root = readExistingRoot();
            Map<String, Object> dashboard = new LinkedHashMap<>();
            List<Map<String, Object>> layoutList = new ArrayList<>();

            for (CardLayoutDTO dto : layouts) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", dto.getId());
                map.put("x", dto.getX());
                map.put("y", dto.getY());
                map.put("w", dto.getW());
                map.put("visible", dto.isVisible());
                layoutList.add(map);
            }

            dashboard.put("layout", layoutList);
            root.put("dashboard", dashboard);

            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            Yaml yaml = new Yaml(options);

            File target = new File(configPath);
            File tmp = new File(configPath + ".tmp");
            try (FileWriter writer = new FileWriter(tmp)) {
                yaml.dump(root, writer);
            }
            Files.move(tmp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

            logger.info("Saved dashboard layout to auth.yaml");
            return ApiResponse.ok(true);
        } catch (Exception e) {
            logger.error("Failed to save dashboard layout to auth.yaml", e);
            return ApiResponse.fail(com.SteamGame.common.error.ErrorCode.INTERNAL_ERROR, "保存布局失败: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readExistingRoot() {
        File f = new File(configPath);
        if (!f.exists()) return new LinkedHashMap<>();

        try (InputStream is = Files.newInputStream(f.toPath())) {
            Yaml yaml = new Yaml();
            Object loaded = yaml.load(is);
            if (loaded instanceof Map) {
                return (Map<String, Object>) loaded;
            }
        } catch (Exception e) {
            logger.warn("Failed to read existing auth.yaml: {}", e.getMessage());
        }
        return new LinkedHashMap<>();
    }
}

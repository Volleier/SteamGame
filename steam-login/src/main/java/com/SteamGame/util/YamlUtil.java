package com.SteamGame.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

import com.SteamGame.login.dto.LoginDTO;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class YamlUtil {
    private static final String projectRoot = new File("").getAbsolutePath();
    private static final String filePath = projectRoot + "/auth.yaml";

    // 写入YAML文件
    public static void writeYaml(LoginDTO dto) {
        try {
            // 创建文件对象
            File file = new File(filePath);

            // 检查父目录是否存在，如不存在则创建
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean created = parentDir.mkdirs();
                if (!created) {
                    throw new IOException("无法创建目录: " + parentDir.getAbsolutePath());
                }
            }

            // 检查文件是否存在，如不存在则创建
            if (!file.exists()) {
                boolean created = file.createNewFile();
                if (!created) {
                    throw new IOException("无法创建文件: " + file.getAbsolutePath());
                }
                System.out.println("已创建新的auth.yaml文件: " + file.getAbsolutePath());
            }

            // 使用LinkedHashMap保持属性顺序
            Map<String, Object> authMap = new LinkedHashMap<>();
            Map<String, Object> dtoMap = new LinkedHashMap<>();

            // 按照要求的顺序添加属性
            dtoMap.put("time", dto.getTime());
            dtoMap.put("steamId", dto.getSteamId());
            dtoMap.put("apiKey", dto.getApiKey());
            dtoMap.put("rememberMe", dto.isRememberMe());

            authMap.put("auth", dtoMap);

            // 配置YAML输出格式
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);

            Yaml yaml = new Yaml(options);
            FileWriter writer = new FileWriter(filePath);
            yaml.dump(authMap, writer);
            writer.close(); // 确保文件被正确关闭
        } catch (IOException e) {
            throw new RuntimeException("写入YAML文件失败: " + filePath, e);
        }
    }

    // 读取YAML文件 - 通过文件路径
    @NotNull
    public static LoginDTO readAuthYaml() {
        try {
            File file = new File(filePath);
            InputStream inputStream = Files.newInputStream(file.toPath());
            Yaml yaml = new Yaml();

            // 读取为Map
            Map<String, Object> yamlMap = yaml.load(inputStream);
            inputStream.close();

            // 检查是否存在auth顶级属性
            if (yamlMap != null && yamlMap.containsKey("auth")) {
                Object authObj = yamlMap.get("auth");
                if (authObj instanceof Map) {
                    // 添加类型安全转换
                    @SuppressWarnings("unchecked")
                    Map<String, Object> authMap = (Map<String, Object>) authObj;
                    return buildLoginDTO(authMap);
                }
            }

            // 创建并填充LoginDTO对象
            LoginDTO dto = new LoginDTO();
            if (yamlMap != null && yamlMap.containsKey("time")) dto.setTime((String) yamlMap.get("time"));
            if (yamlMap != null && yamlMap.containsKey("steamId")) dto.setSteamId((String) yamlMap.get("steamId"));
            if (yamlMap != null && yamlMap.containsKey("apiKey")) dto.setApiKey((String) yamlMap.get("apiKey"));
            if (yamlMap != null && yamlMap.containsKey("rememberMe"))
                dto.setRememberMe((Boolean) yamlMap.get("rememberMe"));

            return dto;
        } catch (IOException e) {
            throw new RuntimeException("读取YAML文件失败: " + filePath, e);
        }
    }

    // 抽取构建DTO的公共方法
    private static LoginDTO buildLoginDTO(Map<String, Object> yamlMap) {
        LoginDTO dto = new LoginDTO();
        if (yamlMap.containsKey("time")) dto.setTime((String) yamlMap.get("time"));
        if (yamlMap.containsKey("steamId")) dto.setSteamId((String) yamlMap.get("steamId"));
        if (yamlMap.containsKey("apiKey")) dto.setApiKey((String) yamlMap.get("apiKey"));
        if (yamlMap.containsKey("rememberMe")) dto.setRememberMe((Boolean) yamlMap.get("rememberMe"));
        return dto;
    }
}
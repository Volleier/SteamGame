package com.SteamGame.login.repository.impl;

import com.SteamGame.login.model.CredentialRecord;
import com.SteamGame.login.model.CredentialValidationMeta;
import com.SteamGame.login.repository.CredentialRepository;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class YamlCredentialRepository implements CredentialRepository {
    private final String filePath;

    public YamlCredentialRepository() {
        this.filePath = new File("").getAbsolutePath() + File.separator + "auth.yaml";
    }

    @Override
    public Optional<CredentialRecord> findByUserId(String userId) {
        try {
            File f = new File(filePath);
            if (!f.exists())
                return Optional.empty();
            InputStream is = Files.newInputStream(f.toPath());
            Yaml yaml = new Yaml();
            Object loaded = yaml.load(is);
            is.close();
            if (!(loaded instanceof Map))
                return Optional.empty();
            @SuppressWarnings("unchecked")
            Map<String, Object> root = (Map<String, Object>) loaded;
            Object auth = root.get("auth");
            if (auth instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> authMap = (Map<String, Object>) auth;
                // new format: users list
                Object usersObj = authMap.get("users");
                if (usersObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> users = (List<Map<String, Object>>) usersObj;
                    for (Map<String, Object> u : users) {
                        String uid = (String) u.get("userId");
                        if (userId != null && userId.equals(uid)) {
                            return Optional.of(mapToRecord(uid, u));
                        }
                    }
                } else {
                    // legacy single-user format (steamId/apiKey under auth)
                    String steamId = (String) authMap.get("steamId");
                    String apiKey = (String) authMap.get("apiKey");
                    if (steamId != null || apiKey != null) {
                        CredentialRecord r = new CredentialRecord();
                        r.setUserId(userId == null ? "default" : userId);
                        r.setSteamId(steamId);
                        r.setApiKey(apiKey);
                        r.setValidation(null);
                        return Optional.of(r);
                    }
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("读取 auth.yaml 失败", e);
        }
    }

    private CredentialRecord mapToRecord(String uid, Map<String, Object> u) {
        CredentialRecord r = new CredentialRecord();
        r.setUserId(uid);
        r.setSteamId((String) u.get("steamId"));
        Object api = u.get("apiKey");
        if (api != null)
            r.setApiKey((String) api);
        Object v = u.get("validation");
        if (v instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> vm = (Map<String, Object>) v;
            CredentialValidationMeta m = new CredentialValidationMeta();
            m.setStatus((String) vm.get("status"));
            m.setLastValidatedAt((String) vm.get("lastValidatedAt"));
            m.setNextRevalidateAt((String) vm.get("nextRevalidateAt"));
            Object fc = vm.get("failCount");
            if (fc instanceof Number)
                m.setFailCount(((Number) fc).intValue());
            m.setLastErrorCode((String) vm.get("lastErrorCode"));
            r.setValidation(m);
        }
        return r;
    }

    @Override
    public void upsert(CredentialRecord record) {
        try {
            File f = new File(filePath);
            Map<String, Object> root = new LinkedHashMap<>();
            Map<String, Object> auth = new LinkedHashMap<>();
            List<Map<String, Object>> users = new ArrayList<>();

            // load existing users if present
            if (f.exists()) {
                InputStream is = Files.newInputStream(f.toPath());
                Yaml yaml = new Yaml();
                Object loaded = yaml.load(is);
                is.close();
                if (loaded instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> loadedMap = (Map<String, Object>) loaded;
                    Object a = loadedMap.get("auth");
                    if (a instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> aMap = (Map<String, Object>) a;
                        Object usersObj = aMap.get("users");
                        if (usersObj instanceof List) {
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> existing = (List<Map<String, Object>>) usersObj;
                            users.addAll(existing);
                        } else {
                            // legacy single-user -> migrate
                            Map<String, Object> migrated = new LinkedHashMap<>();
                            migrated.put("userId", "default");
                            migrated.put("steamId", aMap.get("steamId"));
                            migrated.put("apiKey", aMap.get("apiKey"));
                            users.add(migrated);
                        }
                    }
                }
            }

            // remove existing entry with same userId
            users.removeIf(u -> record.getUserId() != null && record.getUserId().equals(u.get("userId")));

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("userId", record.getUserId());
            entry.put("steamId", record.getSteamId());
            entry.put("apiKey", record.getApiKey());
            if (record.getValidation() != null) {
                Map<String, Object> v = new LinkedHashMap<>();
                CredentialValidationMeta m = record.getValidation();
                v.put("status", m.getStatus());
                v.put("lastValidatedAt", m.getLastValidatedAt());
                v.put("nextRevalidateAt", m.getNextRevalidateAt());
                v.put("failCount", m.getFailCount());
                v.put("lastErrorCode", m.getLastErrorCode());
                entry.put("validation", v);
            }
            users.add(entry);

            auth.put("users", users);
            root.put("auth", auth);

            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            Yaml yamlWriter = new Yaml(options);
            FileWriter writer = new FileWriter(f);
            yamlWriter.dump(root, writer);
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException("写入 auth.yaml 失败", e);
        }
    }

    @Override
    public List<CredentialRecord> findDueForRevalidation(long nowEpochMillis) {
        List<CredentialRecord> due = new ArrayList<>();
        try {
            File f = new File(filePath);
            if (!f.exists())
                return due;
            InputStream is = Files.newInputStream(f.toPath());
            Yaml yaml = new Yaml();
            Object loaded = yaml.load(is);
            is.close();
            if (!(loaded instanceof Map))
                return due;
            @SuppressWarnings("unchecked")
            Map<String, Object> root = (Map<String, Object>) loaded;
            Object auth = root.get("auth");
            if (auth instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> authMap = (Map<String, Object>) auth;
                Object usersObj = authMap.get("users");
                if (usersObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> users = (List<Map<String, Object>>) usersObj;
                    DateTimeFormatter tf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    for (Map<String, Object> u : users) {
                        CredentialRecord r = mapToRecord((String) u.get("userId"), u);
                        if (r != null && r.getValidation() != null && r.getValidation().getNextRevalidateAt() != null) {
                            try {
                                LocalDateTime next = LocalDateTime.parse(r.getValidation().getNextRevalidateAt(), tf);
                                long epoch = next.toInstant(ZoneOffset.UTC).toEpochMilli();
                                if (epoch <= nowEpochMillis) {
                                    due.add(r);
                                }
                            } catch (Exception ex) {
                                // ignore parse errors and continue
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("读取 auth.yaml 失败", e);
        }
        return due;
    }
}

package com.SteamGame.api.dto;

import java.util.List;

/**
 * DTO for Steam API supported list response (GET /api/admin/steam-api/supported).
 */
public class SteamApiSupportedListDTO {
    private String source;
    private Boolean withKey;
    private List<InterfaceDTO> interfaces;

    public static class InterfaceDTO {
        private String name;
        private List<MethodDTO> methods;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<MethodDTO> getMethods() { return methods; }
        public void setMethods(List<MethodDTO> methods) { this.methods = methods; }
    }

    public static class MethodDTO {
        private String name;
        private Integer version;
        private String httpMethod;
        private List<ParameterDTO> parameters;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getVersion() { return version; }
        public void setVersion(Integer version) { this.version = version; }
        public String getHttpMethod() { return httpMethod; }
        public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
        public List<ParameterDTO> getParameters() { return parameters; }
        public void setParameters(List<ParameterDTO> parameters) { this.parameters = parameters; }
    }

    public static class ParameterDTO {
        private String name;
        private String type;
        private Boolean optional;
        private String description;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Boolean getOptional() { return optional; }
        public void setOptional(Boolean optional) { this.optional = optional; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Boolean getWithKey() { return withKey; }
    public void setWithKey(Boolean withKey) { this.withKey = withKey; }
    public List<InterfaceDTO> getInterfaces() { return interfaces; }
    public void setInterfaces(List<InterfaceDTO> interfaces) { this.interfaces = interfaces; }
}

package com.SteamGame.api.dto;

/**
 * DTO for health check response (GET /api/health).
 */
public class HealthDTO {
    private String status;
    private String application;
    private String time;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getApplication() { return application; }
    public void setApplication(String application) { this.application = application; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}

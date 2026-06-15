package com.SteamGame.api.dto.stats;

import java.util.List;

/**
 * DTO for batch player count response.
 */
public class CurrentPlayerBatchDTO {
    private List<CurrentPlayerCountDTO> items;

    public List<CurrentPlayerCountDTO> getItems() { return items; }
    public void setItems(List<CurrentPlayerCountDTO> items) { this.items = items; }
}

CREATE TABLE IF NOT EXISTS owned_game (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(64) NOT NULL DEFAULT 'default',
  steam_id VARCHAR(32) NOT NULL,
  appid BIGINT NOT NULL,
  name VARCHAR(512),
  playtime_forever INT NOT NULL DEFAULT 0,
  last_synced_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_owned_game_user_app UNIQUE (user_id, appid)
);

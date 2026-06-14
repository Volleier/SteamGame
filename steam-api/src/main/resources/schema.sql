CREATE TABLE IF NOT EXISTS owned_game (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(64) NOT NULL DEFAULT 'default',
  steam_id VARCHAR(32) NOT NULL,
  appid BIGINT NOT NULL,
  name VARCHAR(512),
  playtime_forever INT NOT NULL DEFAULT 0,
  developer VARCHAR(256),
  publisher VARCHAR(256),
  release_date VARCHAR(128),
  tags VARCHAR(1024),
  last_synced_at TIMESTAMP,
  details_synced_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_owned_game_user_app UNIQUE (user_id, appid)
);

CREATE INDEX IF NOT EXISTS idx_owned_game_user_id ON owned_game(user_id);

ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS developer VARCHAR(256);
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS publisher VARCHAR(256);
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS release_date VARCHAR(128);
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS tags VARCHAR(1024);
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS details_synced_at TIMESTAMP;

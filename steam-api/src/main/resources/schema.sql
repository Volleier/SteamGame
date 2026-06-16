-- ============================================================
-- SteamGame Database Schema
-- H2 compatible, repeatable execution (IF NOT EXISTS)
-- ============================================================

-- ============================================================
-- 1. owned_game (existing, extended)
-- ============================================================
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

-- Extend owned_game with additional Steam fields (repeatable)
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS developer VARCHAR(256);
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS publisher VARCHAR(256);
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS release_date VARCHAR(128);
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS tags VARCHAR(1024);
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS details_synced_at TIMESTAMP;
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS playtime_2weeks INT DEFAULT 0;
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS rtime_last_played BIGINT;
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS img_icon_url VARCHAR(512);
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS has_community_visible_stats BOOLEAN;
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS playtime_windows_forever INT DEFAULT 0;
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS playtime_mac_forever INT DEFAULT 0;
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS playtime_linux_forever INT DEFAULT 0;
ALTER TABLE owned_game ADD COLUMN IF NOT EXISTS playtime_deck_forever INT DEFAULT 0;

-- ============================================================
-- 2. game_metadata (Store AppDetails)
-- ============================================================
CREATE TABLE IF NOT EXISTS game_metadata (
  appid BIGINT PRIMARY KEY,
  name VARCHAR(512),
  type VARCHAR(64),
  short_description CLOB,
  detailed_description CLOB,
  header_image VARCHAR(1024),
  capsule_image VARCHAR(1024),
  website VARCHAR(1024),
  developers CLOB,
  publishers CLOB,
  release_date VARCHAR(128),
  coming_soon BOOLEAN,
  platform_windows BOOLEAN,
  platform_mac BOOLEAN,
  platform_linux BOOLEAN,
  price_currency VARCHAR(16),
  price_initial INT,
  price_final INT,
  discount_percent INT,
  metacritic_score INT,
  pc_requirements CLOB,
  metadata_source VARCHAR(64),
  metadata_synced_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 3. game_category (Store AppDetails categories)
-- ============================================================
CREATE TABLE IF NOT EXISTS game_category (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  appid BIGINT NOT NULL,
  category_id INT,
  description VARCHAR(256),
  source VARCHAR(64),
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_game_category_appid ON game_category(appid);

-- ============================================================
-- 4. game_genre (Store AppDetails genres)
-- ============================================================
CREATE TABLE IF NOT EXISTS game_genre (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  appid BIGINT NOT NULL,
  genre_id VARCHAR(64),
  description VARCHAR(256),
  source VARCHAR(64),
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_game_genre_appid ON game_genre(appid);

-- ============================================================
-- 5. game_realtime_stats (current player count cache)
-- ============================================================
CREATE TABLE IF NOT EXISTS game_realtime_stats (
  appid BIGINT PRIMARY KEY,
  player_count INT,
  cached BOOLEAN DEFAULT TRUE,
  stale BOOLEAN DEFAULT FALSE,
  synced_at TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 6. player_profile (Steam player profile cache)
-- ============================================================
CREATE TABLE IF NOT EXISTS player_profile (
  user_id VARCHAR(64) PRIMARY KEY,
  steam_id VARCHAR(32),
  persona_name VARCHAR(256),
  profile_url VARCHAR(1024),
  avatar VARCHAR(1024),
  avatar_medium VARCHAR(1024),
  avatar_full VARCHAR(1024),
  persona_state INT,
  community_visibility_state INT,
  last_logoff BIGINT,
  time_created BIGINT,
  country_code VARCHAR(16),
  synced_at TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 7. recent_game (recently played games cache)
-- ============================================================
CREATE TABLE IF NOT EXISTS recent_game (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(64) NOT NULL,
  appid BIGINT NOT NULL,
  name VARCHAR(512),
  playtime_2weeks INT,
  playtime_forever INT,
  icon_url VARCHAR(1024),
  synced_at TIMESTAMP,
  UNIQUE(user_id, appid)
);

-- ============================================================
-- 8. game_news (game news cache)
-- ============================================================
CREATE TABLE IF NOT EXISTS game_news (
  gid VARCHAR(128) PRIMARY KEY,
  appid BIGINT NOT NULL,
  title VARCHAR(1024),
  url VARCHAR(2048),
  external_url BOOLEAN,
  author VARCHAR(256),
  contents CLOB,
  feed_label VARCHAR(256),
  date BIGINT,
  synced_at TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_game_news_appid ON game_news(appid);

-- ============================================================
-- 9. game_achievement_global (global achievement percentages)
-- ============================================================
CREATE TABLE IF NOT EXISTS game_achievement_global (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  appid BIGINT NOT NULL,
  name VARCHAR(512),
  percent DOUBLE,
  synced_at TIMESTAMP,
  UNIQUE(appid, name)
);

-- ============================================================
-- 10. player_friend (Steam friend list cache)
-- ============================================================
CREATE TABLE IF NOT EXISTS player_friend (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(64) NOT NULL,
  friend_steam_id VARCHAR(32) NOT NULL,
  relationship VARCHAR(64),
  friend_since BIGINT,
  synced_at TIMESTAMP,
  UNIQUE(user_id, friend_steam_id)
);

-- ============================================================
-- 11. player_wishlist (Steam wishlist cache)
-- ============================================================
CREATE TABLE IF NOT EXISTS player_wishlist (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(64) NOT NULL,
  appid BIGINT NOT NULL,
  name VARCHAR(512),
  priority INT,
  added_at BIGINT,
  synced_at TIMESTAMP,
  UNIQUE(user_id, appid)
);

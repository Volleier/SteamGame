/**
 * Mock API for fetching Steam games data.
 * Once the backend is ready, this file will be integrated with Axios or Fetch.
 */

// A collection of popular Steam App IDs for our poster wall
const TRENDING_GAME_IDS = [
  730,      // CS:GO / CS2
  570,      // Dota 2
  578080,   // PUBG
  1172470,  // Apex Legends
  271590,   // GTA V
  1086940,  // Baldur's Gate 3
  1091500,  // Cyberpunk 2077
  413150,   // Stardew Valley
  252490,   // Rust
  105600,   // Terraria
  292030,   // The Witcher 3
  814380,   // Sekiro
  1245620,  // Elden Ring
  230410,   // Warframe
  359550    // Rainbow Six Siege
];

export interface GamePosterData {
  id: number;
  imageUrl: string;
}

/**
 * Returns a list of game poster URLs.
 * Simulates a network delay for realism.
 */
export async function getTrendingGamePosters(): Promise<GamePosterData[]> {
  // Simulate network fetch
  await new Promise(resolve => setTimeout(resolve, 500));
  
  return TRENDING_GAME_IDS.map(id => ({
    id,
    imageUrl: `https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/${id}/library_600x900.jpg`
  }));
}

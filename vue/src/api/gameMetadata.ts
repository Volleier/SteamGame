/**
 * 游戏元数据 API — Store AppDetails 数据
 */
import http from './http';

export interface GameMetadataPlatforms {
  windows: boolean;
  mac: boolean;
  linux: boolean;
}

export interface GameMetadataPrice {
  currency: string;
  initial: number;
  fin: number;
  discountPercent: number;
}

export interface GameMetadataCategory {
  id: number;
  description: string;
}

export interface GameMetadataGenre {
  id: string;
  description: string;
}

export interface GameMetadata {
  appid: number;
  name: string;
  type: string;
  shortDescription: string;
  headerImage: string;
  capsuleImage: string;
  website: string;
  developers: string[];
  publishers: string[];
  releaseDate: string;
  comingSoon: boolean;
  platforms: GameMetadataPlatforms;
  price: GameMetadataPrice | null;
  categories: GameMetadataCategory[];
  genres: GameMetadataGenre[];
  metadataSource: string;
  metadataSyncedAt: string | null;
}

export interface GameMetadataSyncResult {
  requested: number;
  updated: number;
  failed: number;
}

function unwrap<T>(response: any): T {
  return response.data?.data ?? response.data ?? ({} as T);
}

/** GET /api/game-metadata/{appid} */
export async function getGameMetadata(appid: number): Promise<GameMetadata | null> {
  const response = await http.get(`/game-metadata/${appid}`);
  return unwrap<GameMetadata>(response);
}

/** POST /api/game-metadata/{appid}/sync */
export async function syncGameMetadata(appid: number): Promise<GameMetadata | null> {
  const response = await http.post(`/game-metadata/${appid}/sync`);
  return unwrap<GameMetadata>(response);
}

/** POST /api/game-metadata/sync-missing */
export async function syncMissingMetadata(userId = 'default', limit = 50): Promise<GameMetadataSyncResult> {
  const response = await http.post('/game-metadata/sync-missing', { userId, limit });
  return unwrap<GameMetadataSyncResult>(response);
}

/**
 * Steam 图片 URL 工具 —— 生成海报、头图、胶囊图，支持降级链。
 */

const STEAM_MEDIA_BASE = 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps';

export function getSteamPosterUrl(appid: number): string {
  return `${STEAM_MEDIA_BASE}/${appid}/library_600x900.jpg`;
}

export function getSteamHeaderUrl(appid: number): string {
  return `${STEAM_MEDIA_BASE}/${appid}/header.jpg`;
}

export function getSteamCapsuleUrl(appid: number): string {
  return `${STEAM_MEDIA_BASE}/${appid}/capsule_616x353.jpg`;
}

export function getFallbackPosterDataUrl(): string {
  return `data:image/svg+xml,${encodeURIComponent(
    `<svg xmlns="http://www.w3.org/2000/svg" width="600" height="900" viewBox="0 0 600 900">
       <rect width="100%" height="100%" fill="%231a1d24"/>
       <text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" font-family="sans-serif" font-size="64" fill="%234a5264">🎮</text>
     </svg>`
  )}`;
}

export function resolveNextImageFallback(currentUrl: string): string {
  if (currentUrl.includes('library_600x900.jpg')) {
    return currentUrl.replace('library_600x900.jpg', 'header.jpg');
  }
  if (currentUrl.includes('header.jpg')) {
    return currentUrl.replace('header.jpg', 'capsule_616x353.jpg');
  }
  return getFallbackPosterDataUrl();
}

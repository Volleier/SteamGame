/**
 * 通用格式化工具。
 */

/** 将游玩分钟数格式化为小时（保留两位小数） */
export function formatPlaytimeMinutes(minutes: number): string {
  return (minutes / 60).toFixed(2);
}

/** 格式化时间戳为本地日期字符串 */
export function formatTimestamp(timestamp: string | number | Date): string {
  return new Date(timestamp).toLocaleString();
}

/** 格式化数字显示 */
export function formatCount(count: number): string {
  if (count >= 10000) return (count / 10000).toFixed(1) + '万';
  return String(count);
}

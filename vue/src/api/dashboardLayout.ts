import http from './http';

export interface DashboardLayoutItemDTO {
  id: number;
  x: number;
  y: number;
  w: number;
  visible: boolean;
}

export async function getDashboardLayout(userId?: string): Promise<DashboardLayoutItemDTO[]> {
  try {
    const response = await http.get('/dashboard/layout', { params: userId ? { userId } : {} });
    const data = response.data?.data ?? response.data;
    return Array.isArray(data) ? data : [];
  } catch {
    return [];
  }
}

export async function saveDashboardLayout(layout: DashboardLayoutItemDTO[], userId?: string): Promise<void> {
  await http.post('/dashboard/layout', layout, { params: userId ? { userId } : {} });
}

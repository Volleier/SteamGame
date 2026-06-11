/**
 * CSV 导出工具
 */

export function downloadCsv(filename: string, header: string[], rows: string[][]): void {
  const csv = [header.join(',')]
    .concat(rows.map(row => row.map(cell => `"${String(cell).replace(/"/g, '""')}"`).join(',')))
    .join('\n');

  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}

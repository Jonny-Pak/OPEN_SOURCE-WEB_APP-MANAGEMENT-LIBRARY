/**
 * lienHeService.ts — Service xử lý gửi liên hệ.
 */
import apiClient from './apiClient'

export interface LienHeRequest {
  hoTen: string
  email: string
  tieuDe: string
  noiDung: string
}

export const lienHeService = {
  guiLienHe: (body: LienHeRequest) =>
    apiClient.post<void>('/api/v1/lien-he', body),
}

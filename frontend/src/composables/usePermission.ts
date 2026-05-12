/**
 * composables/usePermission.ts
 * Composable để kiểm tra quyền (permission) của người dùng hiện tại.
 * Dùng trong các component để hiển thị/ẩn nút thêm/sửa/xóa dựa trên role.
 */
import { useAuthStore } from '@/stores/auth'

export function usePermission() {
  const authStore = useAuthStore()

  /**
   * Kiểm tra xem người dùng có permission nào đó không
   * @param permission - Permission string cần kiểm tra (vd: 'sach:create')
   * @returns true nếu người dùng có permission, false nếu không
   */
  const can = (permission: string): boolean => authStore.hasPermission(permission)

  /**
   * Helper untuk kiểm tra quyền CREATE
   * @param resource - Resource name (vd: 'sach' → kiểm tra 'sach:create')
   */
  const canCreate = (resource: string): boolean => can(`${resource}:create`)

  /**
   * Helper để kiểm tra quyền EDIT/UPDATE
   * @param resource - Resource name (vd: 'sach' → kiểm tra 'sach:edit')
   */
  const canEdit = (resource: string): boolean => can(`${resource}:edit`)

  /**
   * Helper để kiểm tra quyền DELETE
   * @param resource - Resource name (vd: 'sach' → kiểm tra 'sach:delete')
   */
  const canDelete = (resource: string): boolean => can(`${resource}:delete`)

  /**
   * Helper để kiểm tra quyền VIEW
   * @param resource - Resource name (vd: 'sach' → kiểm tra 'sach:view')
   */
  const canView = (resource: string): boolean => can(`${resource}:view`)

  /**
   * Helper để kiểm tra quyền APPROVE
   * @param resource - Resource name (vd: 'muon-sach' → kiểm tra 'muon-sach:approve')
   */
  const canApprove = (resource: string): boolean => can(`${resource}:approve`)

  /**
   * Helper để kiểm tra quyền REJECT
   * @param resource - Resource name (vd: 'dat-cho' → kiểm tra 'dat-cho:reject')
   */
  const canReject = (resource: string): boolean => can(`${resource}:reject`)

  return {
    can,
    canCreate,
    canEdit,
    canDelete,
    canView,
    canApprove,
    canReject,
  }
}

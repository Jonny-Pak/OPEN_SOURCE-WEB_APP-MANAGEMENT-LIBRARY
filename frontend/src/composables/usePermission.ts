/**
 * composables/usePermission.ts
 * Composable để kiểm tra quyền (permission) của người dùng hiện tại.
 */
import { useAuthStore } from '@/stores/auth'

export function usePermission() {
  const authStore = useAuthStore()

  const can = (permission: string): boolean => {
    // If the auth store has the method hasPermission, use it
    if (typeof (authStore as any).hasPermission === 'function') {
      return (authStore as any).hasPermission(permission)
    }
    // Fallback: Admin has all permissions, Librarian has most, Doc Gia has none
    if (authStore.isAdmin) return true
    if (authStore.isLibrarian) return true
    return false
  }

  const canCreate = (resource: string): boolean => can(`${resource}:create`)
  const canEdit = (resource: string): boolean => can(`${resource}:edit`)
  const canDelete = (resource: string): boolean => can(`${resource}:delete`)
  const canView = (resource: string): boolean => can(`${resource}:view`)
  const canApprove = (resource: string): boolean => can(`${resource}:approve`)
  const canReject = (resource: string): boolean => can(`${resource}:reject`)

  // Requested English interface
  function hasRole(roleName: string): boolean {
    return authStore.role === roleName || authStore.currentRole === roleName
  }

  function isAdmin(): boolean {
    return authStore.isAdmin
  }

  function isAuthenticated(): boolean {
    return authStore.daXacThuc || authStore.isAuthenticated
  }

  return {
    can,
    canCreate,
    canEdit,
    canDelete,
    canView,
    canApprove,
    canReject,

    // English Interface
    hasRole,
    isAdmin,
    isAuthenticated
  }
}

export default usePermission

/**
 * useModal.ts — Composable quản lý trạng thái Modal.
 */
import { ref } from 'vue'

export function useModal<T = unknown>() {
  const dangMo = ref(false)
  const itemDangSua = ref<T | null>(null)

  function moModalThem(): void {
    itemDangSua.value = null
    dangMo.value = true
  }

  function moModalSua(item: T): void {
    itemDangSua.value = item
    dangMo.value = true
  }

  function dongModal(): void {
    dangMo.value = false
    setTimeout(() => {
      itemDangSua.value = null
    }, 300)
  }

  const dangThem = () => itemDangSua.value === null

  // Requested English interface
  const isOpen = dangMo

  function open(item?: T): void {
    if (item !== undefined) {
      moModalSua(item)
    } else {
      moModalThem()
    }
  }

  function close(): void {
    dongModal()
  }

  async function confirm(onConfirm: () => Promise<void> | void): Promise<void> {
    try {
      await onConfirm()
    } finally {
      close()
    }
  }

  return {
    dangMo,
    itemDangSua,
    dangThem,
    moModalThem,
    moModalSua,
    dongModal,

    // English Interface
    isOpen,
    open,
    close,
    confirm
  }
}

export default useModal

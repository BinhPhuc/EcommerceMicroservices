import { useQueryClient } from '@tanstack/react-query'
import { useCallback } from 'react'

import { authStore } from '@/features/auth/auth-store'

export const useLogout = () => {
  const queryClient = useQueryClient()

  return useCallback(() => {
    authStore.clear()
    queryClient.clear()
  }, [queryClient])
}

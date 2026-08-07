import { useMutation, useQueryClient } from '@tanstack/react-query'

import type { ApiError } from '@/api/api-error'
import type { LoginRequest, LoginResponse } from '@/dto/auth/login'
import { authStore } from '@/features/auth/auth-store'
import { authService } from '@/services/auth.service'

export const useLogin = () => {
  const queryClient = useQueryClient()

  return useMutation<LoginResponse, ApiError, LoginRequest>({
    mutationFn: (body) => authService.login(body),
    onSuccess: (data) => {
      authStore.setTokens({
        accessToken: data.access_token,
        refreshToken: data.refresh_token,
      })
      queryClient.clear()
    },
  })
}

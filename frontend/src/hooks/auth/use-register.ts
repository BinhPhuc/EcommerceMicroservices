import { useMutation } from '@tanstack/react-query'

import type { ApiError } from '@/api/api-error'
import type { RegisterRequest, RegisterResponse } from '@/dto/auth/register'
import { authService } from '@/services/auth.service'

export const useRegister = () =>
  useMutation<RegisterResponse, ApiError, RegisterRequest>({
    mutationFn: (body) => authService.register(body),
  })

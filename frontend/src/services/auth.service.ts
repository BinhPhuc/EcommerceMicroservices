import { API_ENDPOINTS } from '@/api/endpoints'
import { postJson } from '@/api/request'
import {
  loginRequestSchema,
  loginResponseSchema,
  type LoginRequest,
  type LoginResponse,
} from '@/dto/auth/login'
import {
  refreshTokenRequestSchema,
  refreshTokenResponseSchema,
  type RefreshTokenResponse,
} from '@/dto/auth/refresh'
import {
  registerRequestSchema,
  registerResponseSchema,
  type RegisterRequest,
  type RegisterResponse,
} from '@/dto/auth/register'

export const authService = {
  login: (body: LoginRequest, signal?: AbortSignal): Promise<LoginResponse> =>
    postJson({
      url: API_ENDPOINTS.auth.login,
      body,
      requestSchema: loginRequestSchema,
      responseSchema: loginResponseSchema,
      skipAuth: true,
      signal,
    }),

  register: (
    body: RegisterRequest,
    signal?: AbortSignal,
  ): Promise<RegisterResponse> =>
    postJson({
      url: API_ENDPOINTS.auth.register,
      body,
      requestSchema: registerRequestSchema,
      responseSchema: registerResponseSchema,
      skipAuth: true,
      signal,
    }),

  refresh: (
    refreshToken: string,
    signal?: AbortSignal,
  ): Promise<RefreshTokenResponse> =>
    postJson({
      url: API_ENDPOINTS.auth.refresh,
      body: { refresh_token: refreshToken },
      requestSchema: refreshTokenRequestSchema,
      responseSchema: refreshTokenResponseSchema,
      skipAuth: true,
      signal,
    }),
}

import axios from 'axios'

import { ApiError, responseSchemaError, toApiError } from '@/api/api-error'
import { apiResponseSchema } from '@/api/api-response'
import { emitSessionExpired } from '@/api/auth-events'
import { API_ENDPOINTS } from '@/api/endpoints'
import { tokenStorage } from '@/api/token-storage'
import { env } from '@/config/env'
import { refreshTokenResponseSchema } from '@/dto/auth/refresh'
import { authStore } from '@/features/auth/auth-store'

declare module 'axios' {
  export interface AxiosRequestConfig {
    skipAuth?: boolean
    retriedAfterRefresh?: boolean
  }
}

const baseConfig = {
  baseURL: env.apiBaseUrl,
  timeout: env.apiTimeoutMs,
  headers: { 'Content-Type': 'application/json' },
}

export const apiClient = axios.create(baseConfig)

const refreshClient = axios.create(baseConfig)

let pendingRefresh: Promise<string> | null = null

const requestNewAccessToken = async (): Promise<string> => {
  const refreshToken = tokenStorage.getRefreshToken()

  if (!refreshToken) {
    throw new ApiError({
      kind: 'http',
      status: 401,
      message: 'Không có refresh token để làm mới phiên đăng nhập.',
    })
  }

  const response = await refreshClient.post(API_ENDPOINTS.auth.refresh, {
    refresh_token: refreshToken,
  })

  const parsed = apiResponseSchema(refreshTokenResponseSchema).safeParse(
    response.data,
  )

  if (!parsed.success) {
    throw responseSchemaError(API_ENDPOINTS.auth.refresh, parsed.error)
  }

  authStore.setTokens({
    accessToken: parsed.data.data.access_token,
    refreshToken: parsed.data.data.refresh_token,
  })

  return parsed.data.data.access_token
}

const refreshAccessTokenOnce = (): Promise<string> => {
  pendingRefresh ??= requestNewAccessToken().finally(() => {
    pendingRefresh = null
  })
  return pendingRefresh
}

const endSession = () => {
  authStore.clear()
  emitSessionExpired()
}

apiClient.interceptors.request.use((config) => {
  if (config.skipAuth) {
    return config
  }

  const accessToken = tokenStorage.getAccessToken()
  if (accessToken) {
    config.headers.set('Authorization', `Bearer ${accessToken}`)
  }

  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  async (error: unknown) => {
    if (!axios.isAxiosError(error)) {
      return Promise.reject(toApiError(error))
    }

    const config = error.config
    const isUnauthorized = error.response?.status === 401

    if (!isUnauthorized || !config) {
      return Promise.reject(toApiError(error))
    }

    const cannotRetry =
      config.skipAuth === true ||
      config.retriedAfterRefresh === true ||
      !env.authRefreshEnabled

    if (cannotRetry) {
      endSession()
      return Promise.reject(toApiError(error))
    }

    try {
      const accessToken = await refreshAccessTokenOnce()
      config.retriedAfterRefresh = true
      config.headers.set('Authorization', `Bearer ${accessToken}`)
      return await apiClient.request(config)
    } catch (refreshError) {
      endSession()
      return Promise.reject(toApiError(refreshError))
    }
  },
)

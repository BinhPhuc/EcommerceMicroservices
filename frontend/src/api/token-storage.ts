import { env } from '@/config/env'
import {
  readStoredValue,
  removeStoredValue,
  writeStoredValue,
} from '@/lib/browser-storage'

export type AuthTokens = {
  accessToken: string
  refreshToken: string
}

export const tokenStorage = {
  getAccessToken: () => readStoredValue(env.accessTokenStorageKey),
  getRefreshToken: () => readStoredValue(env.refreshTokenStorageKey),
  save: ({ accessToken, refreshToken }: AuthTokens) => {
    writeStoredValue(env.accessTokenStorageKey, accessToken)
    writeStoredValue(env.refreshTokenStorageKey, refreshToken)
  },
  clear: () => {
    removeStoredValue(env.accessTokenStorageKey)
    removeStoredValue(env.refreshTokenStorageKey)
  },
}

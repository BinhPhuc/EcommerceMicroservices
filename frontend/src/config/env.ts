import { z } from 'zod'

const booleanFromString = z
  .enum(['true', 'false'])
  .transform((value) => value === 'true')

const positiveIntFromString = z
  .string()
  .regex(/^\d+$/)
  .transform(Number)
  .pipe(z.number().int().positive())

const nonNegativeIntFromString = z
  .string()
  .regex(/^\d+$/)
  .transform(Number)
  .pipe(z.number().int().nonnegative())

const envSchema = z.object({
  VITE_APP_NAME: z.string().min(1),
  VITE_API_BASE_URL: z.url(),
  VITE_API_TIMEOUT_MS: positiveIntFromString,
  VITE_VALIDATE_API_RESPONSE: booleanFromString,
  VITE_AUTH_ACCESS_TOKEN_KEY: z.string().min(1),
  VITE_AUTH_REFRESH_TOKEN_KEY: z.string().min(1),
  VITE_AUTH_REFRESH_ENABLED: booleanFromString,
  VITE_THEME_STORAGE_KEY: z.string().min(1),
  VITE_THEME_DEFAULT: z.enum(['light', 'dark', 'system']),
  VITE_QUERY_STALE_TIME_MS: nonNegativeIntFromString,
  VITE_QUERY_RETRY: nonNegativeIntFromString,
})

const parsed = envSchema.safeParse(import.meta.env)

if (!parsed.success) {
  const details = z.prettifyError(parsed.error)
  throw new Error(`Invalid environment configuration:\n${details}`)
}

const raw = parsed.data

export const env = {
  appName: raw.VITE_APP_NAME,
  apiBaseUrl: raw.VITE_API_BASE_URL,
  apiTimeoutMs: raw.VITE_API_TIMEOUT_MS,
  validateApiResponse: raw.VITE_VALIDATE_API_RESPONSE,
  accessTokenStorageKey: raw.VITE_AUTH_ACCESS_TOKEN_KEY,
  refreshTokenStorageKey: raw.VITE_AUTH_REFRESH_TOKEN_KEY,
  authRefreshEnabled: raw.VITE_AUTH_REFRESH_ENABLED,
  themeStorageKey: raw.VITE_THEME_STORAGE_KEY,
  themeDefault: raw.VITE_THEME_DEFAULT,
  queryStaleTimeMs: raw.VITE_QUERY_STALE_TIME_MS,
  queryRetry: raw.VITE_QUERY_RETRY,
} as const

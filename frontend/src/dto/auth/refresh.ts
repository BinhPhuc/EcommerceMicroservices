import { z } from 'zod'

import { loginResponseSchema } from '@/dto/auth/login'

export const refreshTokenRequestSchema = z.object({
  refresh_token: z.string().min(1),
})

export const refreshTokenResponseSchema = loginResponseSchema

export type RefreshTokenRequest = z.infer<typeof refreshTokenRequestSchema>
export type RefreshTokenResponse = z.infer<typeof refreshTokenResponseSchema>

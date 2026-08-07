import { z } from 'zod'

export const loginRequestSchema = z.object({
  username: z.string().min(1, 'Tên đăng nhập không được để trống'),
  password: z.string().min(1, 'Mật khẩu không được để trống'),
})

export const loginResponseSchema = z.object({
  access_token: z.string(),
  expires_in: z.number().int().nullable(),
  refresh_expires_in: z.number().int().nullable(),
  refresh_token: z.string(),
})

export type LoginRequest = z.infer<typeof loginRequestSchema>
export type LoginResponse = z.infer<typeof loginResponseSchema>

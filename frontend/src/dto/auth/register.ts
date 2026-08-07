import { z } from 'zod'

import { userRoleSchema } from '@/dto/common/enums'

export const registerRequestSchema = z.object({
  username: z.string().min(1, 'Tên đăng nhập không được để trống'),
  first_name: z.string().min(1, 'Họ không được để trống'),
  last_name: z.string().min(1, 'Tên không được để trống'),
  email: z.email('Email không hợp lệ'),
  password: z.string().min(1, 'Mật khẩu không được để trống'),
  role: userRoleSchema,
})

export const registerResponseSchema = z.string()

export type RegisterRequest = z.infer<typeof registerRequestSchema>
export type RegisterResponse = z.infer<typeof registerResponseSchema>

import { z } from 'zod'

export const orderStatusSchema = z.enum([
  'PENDING',
  'PREPARED',
  'SHIPPED',
  'DELIVERED',
  'CANCELED',
])

export type OrderStatus = z.infer<typeof orderStatusSchema>

export const userRoleSchema = z.enum(['USER', 'ADMIN'])

export type UserRole = z.infer<typeof userRoleSchema>

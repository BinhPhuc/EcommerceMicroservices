import { z } from 'zod'

import { orderStatusSchema } from '@/dto/common/enums'

export const createOrderItemRequestSchema = z.object({
  product_id: z.string().min(1, 'Mã sản phẩm không được để trống'),
  quantity: z
    .number()
    .int('Số lượng phải là số nguyên')
    .positive('Số lượng phải lớn hơn 0'),
})

export const createOrderRequestSchema = z.object({
  customer_id: z.string().min(1),
  order_items: z
    .array(createOrderItemRequestSchema)
    .min(1, 'Đơn hàng cần ít nhất một sản phẩm'),
})

export const createOrderResponseSchema = z.object({
  status: orderStatusSchema,
  total_amount: z.number().int().nullable(),
})

export type CreateOrderItemRequest = z.infer<
  typeof createOrderItemRequestSchema
>
export type CreateOrderRequest = z.infer<typeof createOrderRequestSchema>
export type CreateOrderResponse = z.infer<typeof createOrderResponseSchema>

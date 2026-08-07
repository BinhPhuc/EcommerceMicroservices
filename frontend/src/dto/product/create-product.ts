import { z } from 'zod'

export const createProductRequestSchema = z.object({
  name: z.string().min(1, 'Tên sản phẩm không được để trống'),
  price: z.number().int('Giá phải là số nguyên').positive('Giá phải lớn hơn 0'),
  stock: z
    .number()
    .int('Tồn kho phải là số nguyên')
    .positive('Tồn kho phải lớn hơn 0'),
  category_id: z.string().min(1, 'Danh mục không được để trống'),
})

export const createProductResponseSchema = z.object({
  name: z.string(),
})

export type CreateProductRequest = z.infer<typeof createProductRequestSchema>
export type CreateProductResponse = z.infer<typeof createProductResponseSchema>

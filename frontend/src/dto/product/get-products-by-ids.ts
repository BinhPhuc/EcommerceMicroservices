import { z } from 'zod'

export const getProductsByIdsRequestSchema = z.object({
  product_ids: z.array(z.string().min(1)).min(1, 'Cần ít nhất một mã sản phẩm'),
})

export const productSchema = z.object({
  id: z.string(),
  name: z.string(),
  price: z.number().int().nullable(),
  stock: z.number().int().nullable(),
  category_id: z.string().nullable(),
  is_deleted: z.boolean().nullable(),
})

export const getProductsByIdsResponseSchema = z.array(productSchema)

export type GetProductsByIdsRequest = z.infer<
  typeof getProductsByIdsRequestSchema
>
export type Product = z.infer<typeof productSchema>
export type GetProductsByIdsResponse = z.infer<
  typeof getProductsByIdsResponseSchema
>

import { z } from 'zod'

export const createCategoryRequestSchema = z.object({
  name: z.string().min(1, 'Tên danh mục không được để trống'),
  parent_id: z.string().nullable(),
})

export const createCategoryResponseSchema = z.object({
  name: z.string(),
  parent_id: z.string().nullable(),
})

export type CreateCategoryRequest = z.infer<typeof createCategoryRequestSchema>
export type CreateCategoryResponse = z.infer<typeof createCategoryResponseSchema>

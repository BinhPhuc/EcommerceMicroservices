import { API_ENDPOINTS } from '@/api/endpoints'
import { postJson } from '@/api/request'
import {
  createCategoryRequestSchema,
  createCategoryResponseSchema,
  type CreateCategoryRequest,
  type CreateCategoryResponse,
} from '@/dto/category/create-category'

export const categoryService = {
  create: (
    body: CreateCategoryRequest,
    signal?: AbortSignal,
  ): Promise<CreateCategoryResponse> =>
    postJson({
      url: API_ENDPOINTS.categories.create,
      body,
      requestSchema: createCategoryRequestSchema,
      responseSchema: createCategoryResponseSchema,
      signal,
    }),
}

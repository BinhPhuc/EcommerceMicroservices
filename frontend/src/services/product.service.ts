import { API_ENDPOINTS } from '@/api/endpoints'
import { postJson } from '@/api/request'
import {
  createProductRequestSchema,
  createProductResponseSchema,
  type CreateProductRequest,
  type CreateProductResponse,
} from '@/dto/product/create-product'
import {
  getProductsByIdsRequestSchema,
  getProductsByIdsResponseSchema,
  type GetProductsByIdsResponse,
} from '@/dto/product/get-products-by-ids'

export const productService = {
  create: (
    body: CreateProductRequest,
    signal?: AbortSignal,
  ): Promise<CreateProductResponse> =>
    postJson({
      url: API_ENDPOINTS.products.create,
      body,
      requestSchema: createProductRequestSchema,
      responseSchema: createProductResponseSchema,
      signal,
    }),

  getByIds: (
    productIds: string[],
    signal?: AbortSignal,
  ): Promise<GetProductsByIdsResponse> =>
    postJson({
      url: API_ENDPOINTS.products.getByIds,
      body: { product_ids: productIds },
      requestSchema: getProductsByIdsRequestSchema,
      responseSchema: getProductsByIdsResponseSchema,
      signal,
    }),
}

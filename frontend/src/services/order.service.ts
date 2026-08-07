import { API_ENDPOINTS } from '@/api/endpoints'
import { postJson } from '@/api/request'
import {
  createOrderRequestSchema,
  createOrderResponseSchema,
  type CreateOrderRequest,
  type CreateOrderResponse,
} from '@/dto/order/create-order'

export const orderService = {
  create: (
    body: CreateOrderRequest,
    signal?: AbortSignal,
  ): Promise<CreateOrderResponse> =>
    postJson({
      url: API_ENDPOINTS.orders.create,
      body,
      requestSchema: createOrderRequestSchema,
      responseSchema: createOrderResponseSchema,
      signal,
    }),
}

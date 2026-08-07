import { useMutation, useQueryClient } from '@tanstack/react-query'

import { ApiError } from '@/api/api-error'
import type {
  CreateOrderItemRequest,
  CreateOrderResponse,
} from '@/dto/order/create-order'
import { useAuth } from '@/features/auth/use-auth'
import { queryKeys } from '@/hooks/query-keys'
import { orderService } from '@/services/order.service'

export const useCreateOrder = () => {
  const queryClient = useQueryClient()
  const { user } = useAuth()

  return useMutation<CreateOrderResponse, ApiError, CreateOrderItemRequest[]>({
    mutationFn: (orderItems) => {
      if (!user) {
        return Promise.reject(
          new ApiError({
            kind: 'http',
            status: 401,
            message: 'Cần đăng nhập trước khi tạo đơn hàng.',
          }),
        )
      }

      return orderService.create({
        customer_id: user.id,
        order_items: orderItems,
      })
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.orders.all })
      queryClient.invalidateQueries({ queryKey: queryKeys.products.all })
    },
  })
}

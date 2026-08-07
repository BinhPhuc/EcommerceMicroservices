import { useMutation, useQueryClient } from '@tanstack/react-query'

import type { ApiError } from '@/api/api-error'
import type {
  CreateProductRequest,
  CreateProductResponse,
} from '@/dto/product/create-product'
import { queryKeys } from '@/hooks/query-keys'
import { productService } from '@/services/product.service'

export const useCreateProduct = () => {
  const queryClient = useQueryClient()

  return useMutation<CreateProductResponse, ApiError, CreateProductRequest>({
    mutationFn: (body) => productService.create(body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.products.all })
    },
  })
}

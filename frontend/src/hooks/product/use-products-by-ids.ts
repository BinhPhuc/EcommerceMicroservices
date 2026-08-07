import { useQuery } from '@tanstack/react-query'

import type { ApiError } from '@/api/api-error'
import type { GetProductsByIdsResponse } from '@/dto/product/get-products-by-ids'
import { queryKeys } from '@/hooks/query-keys'
import { productService } from '@/services/product.service'

export const useProductsByIds = (productIds: string[], enabled = true) =>
  useQuery<GetProductsByIdsResponse, ApiError>({
    queryKey: queryKeys.products.byIds(productIds),
    queryFn: ({ signal }) => productService.getByIds(productIds, signal),
    enabled: enabled && productIds.length > 0,
  })

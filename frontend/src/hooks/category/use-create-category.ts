import { useMutation, useQueryClient } from '@tanstack/react-query'

import type { ApiError } from '@/api/api-error'
import type {
  CreateCategoryRequest,
  CreateCategoryResponse,
} from '@/dto/category/create-category'
import { queryKeys } from '@/hooks/query-keys'
import { categoryService } from '@/services/category.service'

export const useCreateCategory = () => {
  const queryClient = useQueryClient()

  return useMutation<CreateCategoryResponse, ApiError, CreateCategoryRequest>({
    mutationFn: (body) => categoryService.create(body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.categories.all })
    },
  })
}

import { QueryClient } from '@tanstack/react-query'

import { isApiError } from '@/api/api-error'
import { env } from '@/config/env'

const shouldRetry = (failureCount: number, error: unknown): boolean => {
  if (isApiError(error) && (error.isClientError || error.kind !== 'http')) {
    return false
  }
  return failureCount < env.queryRetry
}

export const createQueryClient = (): QueryClient =>
  new QueryClient({
    defaultOptions: {
      queries: {
        staleTime: env.queryStaleTimeMs,
        retry: shouldRetry,
        refetchOnWindowFocus: false,
      },
      mutations: {
        retry: false,
      },
    },
  })

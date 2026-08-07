import type { QueryClient } from '@tanstack/react-query'
import {
  createRootRouteWithContext,
  Outlet,
  useRouter,
} from '@tanstack/react-router'
import { useEffect } from 'react'

import { onSessionExpired } from '@/api/auth-events'
import { Toaster } from '@/components/ui/sonner'
import type { AuthState } from '@/features/auth/auth-store'

export type RouterContext = {
  queryClient: QueryClient
  auth: AuthState
}

const RootLayout = () => {
  const router = useRouter()

  useEffect(
    () =>
      onSessionExpired(() => {
        router.navigate({
          to: '/login',
          search: { redirect: router.state.location.href },
          replace: true,
        })
      }),
    [router],
  )

  return (
    <>
      <Outlet />
      <Toaster position="top-right" richColors />
    </>
  )
}

export const Route = createRootRouteWithContext<RouterContext>()({
  component: RootLayout,
})

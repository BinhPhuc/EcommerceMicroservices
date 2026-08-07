import { QueryClientProvider } from '@tanstack/react-query'
import { createRouter, RouterProvider } from '@tanstack/react-router'
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'

import { ThemeProvider } from '@/components/theme-provider'
import { authStore } from '@/features/auth/auth-store'
import { useAuth } from '@/features/auth/use-auth'
import { createQueryClient } from '@/lib/query-client'
import { routeTree } from '@/routeTree.gen'
import '@/styles/globals.css'

const queryClient = createQueryClient()

const router = createRouter({
  routeTree,
  context: { queryClient, auth: authStore.getState() },
  defaultPreload: 'intent',
  scrollRestoration: true,
})

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}

const RouterWithAuthContext = () => {
  const auth = useAuth()
  return <RouterProvider router={router} context={{ auth }} />
}

const rootElement = document.getElementById('root')

if (!rootElement) {
  throw new Error('Không tìm thấy phần tử gốc để mount ứng dụng')
}

createRoot(rootElement).render(
  <StrictMode>
    <ThemeProvider>
      <QueryClientProvider client={queryClient}>
        <RouterWithAuthContext />
      </QueryClientProvider>
    </ThemeProvider>
  </StrictMode>,
)

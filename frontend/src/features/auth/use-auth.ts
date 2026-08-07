import { useSyncExternalStore } from 'react'

import { authStore, type AuthState } from '@/features/auth/auth-store'

export const useAuth = (): AuthState =>
  useSyncExternalStore(authStore.subscribe, authStore.getState)

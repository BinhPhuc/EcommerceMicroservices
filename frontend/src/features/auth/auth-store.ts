import { tokenStorage, type AuthTokens } from '@/api/token-storage'
import { collectJwtRoles, decodeJwtClaims, type JwtClaims } from '@/lib/jwt'

export type AuthUser = {
  id: string
  username: string
  email?: string
  fullName: string
  roles: string[]
}

export type AuthState = {
  accessToken: string | null
  isAuthenticated: boolean
  user: AuthUser | null
}

const toAuthUser = (claims: JwtClaims): AuthUser => {
  const fullName = [claims.given_name, claims.family_name]
    .filter(Boolean)
    .join(' ')
    .trim()

  return {
    id: claims.sub,
    username: claims.preferred_username ?? claims.sub,
    email: claims.email,
    fullName: fullName || (claims.preferred_username ?? claims.sub),
    roles: collectJwtRoles(claims),
  }
}

const UNAUTHENTICATED_STATE: AuthState = {
  accessToken: null,
  isAuthenticated: false,
  user: null,
}

const readState = (): AuthState => {
  const accessToken = tokenStorage.getAccessToken()
  if (!accessToken) {
    return UNAUTHENTICATED_STATE
  }

  const claims = decodeJwtClaims(accessToken)
  if (!claims) {
    return UNAUTHENTICATED_STATE
  }

  return {
    accessToken,
    isAuthenticated: true,
    user: toAuthUser(claims),
  }
}

let state = readState()
const listeners = new Set<() => void>()

const emit = () => {
  for (const listener of listeners) {
    listener()
  }
}

const refreshState = () => {
  state = readState()
  emit()
}

export const authStore = {
  getState: () => state,
  subscribe: (listener: () => void) => {
    listeners.add(listener)
    return () => {
      listeners.delete(listener)
    }
  },
  setTokens: (tokens: AuthTokens) => {
    tokenStorage.save(tokens)
    refreshState()
  },
  clear: () => {
    tokenStorage.clear()
    refreshState()
  },
  syncFromStorage: refreshState,
}

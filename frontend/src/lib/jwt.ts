import { z } from 'zod'

const roleContainerSchema = z.object({ roles: z.array(z.string()) })

const jwtClaimsSchema = z.object({
  sub: z.string(),
  exp: z.number().optional(),
  iat: z.number().optional(),
  email: z.string().optional(),
  preferred_username: z.string().optional(),
  given_name: z.string().optional(),
  family_name: z.string().optional(),
  realm_access: roleContainerSchema.optional(),
  resource_access: z.record(z.string(), roleContainerSchema).optional(),
})

export type JwtClaims = z.infer<typeof jwtClaimsSchema>

const decodeBase64Url = (segment: string): string => {
  const normalized = segment.replace(/-/g, '+').replace(/_/g, '/')
  const paddingLength = (4 - (normalized.length % 4)) % 4
  const binary = atob(normalized + '='.repeat(paddingLength))
  const bytes = Uint8Array.from(binary, (character) => character.charCodeAt(0))
  return new TextDecoder().decode(bytes)
}

export const decodeJwtClaims = (token: string): JwtClaims | null => {
  const segments = token.split('.')
  if (segments.length !== 3) {
    return null
  }

  try {
    const payload: unknown = JSON.parse(decodeBase64Url(segments[1]))
    const parsed = jwtClaimsSchema.safeParse(payload)
    return parsed.success ? parsed.data : null
  } catch {
    return null
  }
}

export const isJwtExpired = (claims: JwtClaims, skewSeconds = 0): boolean => {
  if (claims.exp === undefined) {
    return false
  }
  return claims.exp * 1000 <= Date.now() + skewSeconds * 1000
}

export const collectJwtRoles = (claims: JwtClaims): string[] => {
  const realmRoles = claims.realm_access?.roles ?? []
  const clientRoles = Object.values(claims.resource_access ?? {}).flatMap(
    (container) => container.roles,
  )
  return [...new Set([...realmRoles, ...clientRoles])]
}

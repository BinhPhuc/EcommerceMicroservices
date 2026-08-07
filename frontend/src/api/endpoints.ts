export const API_ENDPOINTS = {
  auth: {
    login: '/api/v1/auth',
    register: '/api/v1/auth/register',
    refresh: '/api/v1/auth/refresh',
  },
  products: {
    create: '/api/v1/products/create',
    getByIds: '/api/v1/products/get-by-ids',
  },
  categories: {
    create: '/api/v1/categories/create',
  },
  orders: {
    create: '/api/v1/orders/create',
  },
} as const

export const PUBLIC_ENDPOINTS: readonly string[] = [
  API_ENDPOINTS.auth.login,
  API_ENDPOINTS.auth.register,
  API_ENDPOINTS.auth.refresh,
]

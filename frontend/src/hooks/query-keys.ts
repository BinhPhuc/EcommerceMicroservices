export const queryKeys = {
  products: {
    all: ['products'] as const,
    byIds: (productIds: string[]) =>
      ['products', 'by-ids', [...productIds].sort()] as const,
  },
  categories: {
    all: ['categories'] as const,
  },
  orders: {
    all: ['orders'] as const,
  },
}

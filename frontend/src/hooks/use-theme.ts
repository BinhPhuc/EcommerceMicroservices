import { use } from 'react'

import {
  ThemeContext,
  type ThemeContextValue,
} from '@/components/theme-context'

export const useTheme = (): ThemeContextValue => {
  const context = use(ThemeContext)

  if (!context) {
    throw new Error('useTheme phải được dùng bên trong ThemeProvider')
  }

  return context
}

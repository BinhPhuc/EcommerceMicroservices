import { useCallback, useEffect, useState } from 'react'

import { ThemeContext, type Theme } from '@/components/theme-context'
import { env } from '@/config/env'
import { readStoredValue, writeStoredValue } from '@/lib/browser-storage'

const isTheme = (value: string | null): value is Theme =>
  value === 'light' || value === 'dark' || value === 'system'

const resolveTheme = (theme: Theme): 'light' | 'dark' => {
  if (theme !== 'system') {
    return theme
  }
  return window.matchMedia('(prefers-color-scheme: dark)').matches
    ? 'dark'
    : 'light'
}

const readInitialTheme = (): Theme => {
  const stored = readStoredValue(env.themeStorageKey)
  return isTheme(stored) ? stored : env.themeDefault
}

export const ThemeProvider = ({ children }: { children: React.ReactNode }) => {
  const [theme, setThemeState] = useState<Theme>(readInitialTheme)

  useEffect(() => {
    const root = window.document.documentElement
    const applyTheme = () => {
      root.classList.toggle('dark', resolveTheme(theme) === 'dark')
    }

    applyTheme()

    if (theme !== 'system') {
      return
    }

    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    mediaQuery.addEventListener('change', applyTheme)
    return () => {
      mediaQuery.removeEventListener('change', applyTheme)
    }
  }, [theme])

  const setTheme = useCallback((next: Theme) => {
    writeStoredValue(env.themeStorageKey, next)
    setThemeState(next)
  }, [])

  return <ThemeContext value={{ theme, setTheme }}>{children}</ThemeContext>
}

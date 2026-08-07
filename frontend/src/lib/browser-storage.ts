export const readStoredValue = (key: string): string | null => {
  try {
    return window.localStorage.getItem(key)
  } catch {
    return null
  }
}

export const writeStoredValue = (key: string, value: string): void => {
  try {
    window.localStorage.setItem(key, value)
  } catch {
    return
  }
}

export const removeStoredValue = (key: string): void => {
  try {
    window.localStorage.removeItem(key)
  } catch {
    return
  }
}

// Helper: convertir snake_case a camelCase
export const toCamelCase = (str) => {
  return str.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase())
}

// Helper: convertir objeto con snake_case a camelCase (shallow)
export const keysToCamelCase = (obj) => {
  if (obj === null || obj === undefined) return obj
  if (Array.isArray(obj)) return obj.map(keysToCamelCase)
  if (typeof obj !== 'object') return obj
  
  return Object.fromEntries(
    Object.entries(obj).map(([key, val]) => [
      toCamelCase(key),
      typeof val === 'object' && val !== null ? keysToCamelCase(val) : val
    ])
  )
}

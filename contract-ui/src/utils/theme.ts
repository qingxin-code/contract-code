export type ThemePalette = {
  50: string
  100: string
  200: string
  300: string
  400: string
  500: string
  600: string
  700: string
  800: string
  900: string
}

export const DEFAULT_THEME_COLOR = '#ec4899'

export const THEME_PRESETS = [
  { name: '玫粉', value: '#ec4899' },
  { name: '胭脂红', value: '#ef4444' },
  { name: '琥珀橙', value: '#f59e0b' },
  { name: '翠绿色', value: '#10b981' },
  { name: '海蓝', value: '#0ea5e9' },
  { name: '靛青', value: '#6366f1' }
]

const SHADE_MIXES = {
  50: 0.95,
  100: 0.88,
  200: 0.75,
  300: 0.55,
  400: 0.25,
  500: 0,
  600: 0.12,
  700: 0.24,
  800: 0.38,
  900: 0.5
} as const

const clamp = (value: number) => Math.max(0, Math.min(255, Math.round(value)))

const normalizeHex = (hex: string) => {
  const clean = hex.trim().replace('#', '')
  if (clean.length === 3) {
    return `#${clean.split('').map((char) => char + char).join('')}`.toLowerCase()
  }
  if (clean.length === 6) {
    return `#${clean}`.toLowerCase()
  }
  return DEFAULT_THEME_COLOR
}

const isValidHex = (hex: string) => /^#([0-9a-fA-F]{6}|[0-9a-fA-F]{3})$/.test(hex.trim())

const hexToRgb = (hex: string) => {
  const normalized = normalizeHex(hex).slice(1)
  return {
    r: parseInt(normalized.slice(0, 2), 16),
    g: parseInt(normalized.slice(2, 4), 16),
    b: parseInt(normalized.slice(4, 6), 16)
  }
}

const rgbToHex = (r: number, g: number, b: number) =>
  `#${[r, g, b].map((item) => clamp(item).toString(16).padStart(2, '0')).join('')}`

const mixWithWhite = (hex: string, amount: number) => {
  const { r, g, b } = hexToRgb(hex)
  return rgbToHex(
    r + (255 - r) * amount,
    g + (255 - g) * amount,
    b + (255 - b) * amount
  )
}

const mixWithBlack = (hex: string, amount: number) => {
  const { r, g, b } = hexToRgb(hex)
  return rgbToHex(r * (1 - amount), g * (1 - amount), b * (1 - amount))
}

export const sanitizeThemeColor = (value?: string | null) =>
  value && isValidHex(value) ? normalizeHex(value) : DEFAULT_THEME_COLOR

export const buildThemePalette = (color: string): ThemePalette => {
  const baseColor = sanitizeThemeColor(color)

  return {
    50: mixWithWhite(baseColor, SHADE_MIXES[50]),
    100: mixWithWhite(baseColor, SHADE_MIXES[100]),
    200: mixWithWhite(baseColor, SHADE_MIXES[200]),
    300: mixWithWhite(baseColor, SHADE_MIXES[300]),
    400: mixWithWhite(baseColor, SHADE_MIXES[400]),
    500: baseColor,
    600: mixWithBlack(baseColor, SHADE_MIXES[600]),
    700: mixWithBlack(baseColor, SHADE_MIXES[700]),
    800: mixWithBlack(baseColor, SHADE_MIXES[800]),
    900: mixWithBlack(baseColor, SHADE_MIXES[900])
  }
}

export const hexToRgbString = (hex: string) => {
  const { r, g, b } = hexToRgb(hex)
  return `${r} ${g} ${b}`
}

export const applyThemeColor = (color: string) => {
  const palette = buildThemePalette(color)
  const root = document.documentElement

  Object.entries(palette).forEach(([shade, hex]) => {
    root.style.setProperty(`--primary-${shade}`, hex)
    root.style.setProperty(`--primary-${shade}-rgb`, hexToRgbString(hex))
  })

  root.style.setProperty('--shadow-primary-soft', `0 4px 20px -2px rgb(${hexToRgbString(palette[500])} / 0.18)`)
  root.style.setProperty('--shadow-primary-glow', `0 0 15px rgb(${hexToRgbString(palette[500])} / 0.3)`)
}

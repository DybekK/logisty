export const parseToLocaleString = (date?: string) => {
  if (!date) return "-"

  const parsed = new Date(date)
  return isNaN(parsed.getTime()) ? "-" : parsed.toLocaleString()
}

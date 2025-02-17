export const createGoogleMapsLink = (
  steps: Array<[number, number]>,
): string => {
  if (steps.length < 2) return ""

  const locations = steps.map(([long, lat]) => `${lat},${long}`)

  if (/iPhone|iPad|iPod|Android/i.test(navigator.userAgent)) {
    return `comgooglemaps://?saddr=${locations[0]}&daddr=${locations[1]}&waypoints=${locations.slice(2).join("|")}&directionsmode=driving`
  }

  return `https://www.google.com/maps/dir/${locations.join("/")}`
}

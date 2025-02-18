import React from "react"
import { Layer, Source } from "react-map-gl"

import { Feature } from "geojson"
import { LineLayout } from "mapbox-gl"

interface SourceLayerProps {
  index: number
  featuresLength: number
  feature: Feature
}

const LAYOUT: LineLayout = {
  "line-join": "round",
  "line-cap": "round",
}

const ROUTE_STYLES = {
  main: {
    color: "#0f53ff",
    borderColor: "#0f26f5",
    width: 6,
    borderWidth: 10,
    opacity: 1,
  },
  alternative: {
    color: "#bccefb",
    borderColor: "#6a83d7",
    width: 4,
    borderWidth: 7,
    opacity: 0.75,
  },
} as const

export const SourceLayer: React.FC<SourceLayerProps> = ({
  index,
  featuresLength,
  feature,
}) => {
  const isMainRoute =
    featuresLength === 1 || (featuresLength > 1 && index === 0)
  const routeStyle = isMainRoute ? ROUTE_STYLES.main : ROUTE_STYLES.alternative
  const id = `route-${index}`

  const paint = {
    "line-color": routeStyle.color,
    "line-width": routeStyle.width,
    "line-opacity": routeStyle.opacity,
  }

  const borderPaint = {
    "line-color": routeStyle.borderColor,
    "line-width": routeStyle.borderWidth,
    "line-opacity": routeStyle.opacity,
  }

  return (
    <Source id={id} type="geojson" data={feature}>
      <Layer
        id={`${id}-border`}
        type="line"
        source={id}
        layout={LAYOUT}
        paint={borderPaint}
        beforeId={id}
      />
      <Layer id={id} type="line" source={id} layout={LAYOUT} paint={paint} />
    </Source>
  )
}

import React from "react"
import { Layer, Source } from "react-map-gl"

import { Feature } from "geojson"
import { LineLayout } from "mapbox-gl"

interface SourceLayerProps {
  index: number
  featuresLength: number
  feature: Feature
}

const layout: LineLayout = { "line-join": "round", "line-cap": "round" }

const mainRouteColor = "#0f53ff"
const mainRouteBorderColor = "#0f26f5"

const alternativeRouteColor = "#bccefb"
const alternativeRouteBorderColor = "#6a83d7"

export const SourceLayer: React.FC<SourceLayerProps> = ({
  index,
  featuresLength,
  feature,
}) => {
  const id = `route-${index}`
  const source = `route-${index}`

  const paint = {
    "line-color":
      featuresLength > 1 && index === 0
        ? alternativeRouteColor
        : mainRouteColor,
    "line-width": 5,
  }

  const borderPaint = {
    "line-color":
      featuresLength > 1 && index === 0
        ? alternativeRouteBorderColor
        : mainRouteBorderColor,
    "line-width": 9,
  }

  return (
    <Source key={index} id={`route-${index}`} type="geojson" data={feature}>
      <Layer
        id={`${id}-border`}
        type="line"
        source={source}
        layout={layout}
        paint={borderPaint}
        beforeId={id}
      />
      <Layer
        id={id}
        type="line"
        source={source}
        layout={layout}
        paint={paint}
      />
    </Source>
  )
}

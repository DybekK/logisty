import React, { useEffect, useState } from "react"
import Map from "react-map-gl/maplibre"

import { Flex, Skeleton } from "antd"

import { FeatureCollection, LineString } from "geojson"

import { OSRMRoute, OSRMWaypoint } from "@/common"
import { SourceLayer } from "@/components"

const { VITE_MAP_GL_STYLE } = import.meta.env

console.log(import.meta.env)

const flexStyle: React.CSSProperties = {
  height: "100%",
  width: "100%",
  justifyContent: "center",
  alignItems: "center",
}

interface Map3DProps {
  id: string
  routes: OSRMRoute[]
  waypoints: OSRMWaypoint[]
}

const transformRoutesToGeoJSON = (
  routes: OSRMRoute[],
): FeatureCollection<LineString> => {
  const features = routes.map(route => {
    return {
      type: "Feature" as const,
      properties: {},
      geometry: route.geometry as LineString,
    }
  })

  return {
    type: "FeatureCollection",
    features,
  }
}

interface Coordinates {
  lat: number
  lon: number
}

export const Map3D: React.FC<Map3DProps> = ({ id, routes }) => {
  const [coordinates, setCoordinates] = useState<Coordinates>({
    lat: 0,
    lon: 0,
  })
  const [loadingCoordinates, setLoadingCoordinates] = useState(true)
  const { features } = transformRoutesToGeoJSON(routes)

  useEffect(() => {
    navigator.geolocation.getCurrentPosition(position => {
      setCoordinates({
        lon: position.coords.longitude,
        lat: position.coords.latitude,
      })
      setLoadingCoordinates(false)
    })
  }, [])

  if (loadingCoordinates) {
    return (
      <Flex style={flexStyle}>
        <Skeleton.Image active={true} />
      </Flex>
    )
  }
  return (
    <Map
      id={id}
      initialViewState={{
        longitude: coordinates.lon,
        latitude: coordinates.lat,
        zoom: 14,
      }}
      mapStyle={VITE_MAP_GL_STYLE}
    >
      {[...features].reverse().map((feature, index) => (
        <SourceLayer
          key={index}
          index={index}
          featuresLength={features.length}
          feature={feature}
        />
      ))}
    </Map>
  )
}

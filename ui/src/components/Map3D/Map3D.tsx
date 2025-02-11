import React, { useEffect, useState } from "react"
import { MapRef, useMap } from "react-map-gl"
import Map from "react-map-gl/maplibre"

import { Flex, Skeleton } from "antd"

import { FeatureCollection, LineString } from "geojson"

import { SourceLayer } from "@/components"

const { VITE_MAP_GL_STYLE } = import.meta.env

const flexStyle: React.CSSProperties = {
  height: "100%",
  width: "100%",
  justifyContent: "center",
  alignItems: "center",
}

interface Coordinates {
  lat: number
  lon: number
}

interface Route {
  coordinates: number[][]
}

interface Map3DProps {
  id: string
  routes: Route[]
}

const transformRoutesToGeoJSON = (
  routes: Route[],
): FeatureCollection<LineString> => {
  const features = routes.map(route => {
    return {
      type: "Feature" as const,
      properties: {},
      geometry: route as LineString,
    }
  })

  return {
    type: "FeatureCollection",
    features,
  }
}

const fitRoutesToBounds = (routes: Route[], mapInstance?: MapRef): void => {
  const allCoords = routes.flatMap(route => route.coordinates)
  if (allCoords.length > 0) {
    const lons = allCoords.map(coord => coord[0])
    const lats = allCoords.map(coord => coord[1])
    const west = Math.min(...lons)
    const east = Math.max(...lons)
    const south = Math.min(...lats)
    const north = Math.max(...lats)
    mapInstance?.fitBounds(
      [
        [west, south],
        [east, north],
      ],
      {
        padding: 20,
        duration: 1000,
      },
    )
  }
}

export const Map3D: React.FC<Map3DProps> = ({ id, routes }) => {
  const [coordinates, setCoordinates] = useState<Coordinates>({
    lat: 0,
    lon: 0,
  })
  const [loadingCoordinates, setLoadingCoordinates] = useState(true)
  const { features } = transformRoutesToGeoJSON(routes)
  const mapInstance = useMap()

  useEffect(() => {
    navigator.geolocation.getCurrentPosition(position => {
      setCoordinates({
        lon: position.coords.longitude,
        lat: position.coords.latitude,
      })
      setLoadingCoordinates(false)
    })
  }, [])

  useEffect(() => {
    fitRoutesToBounds(routes, mapInstance[id])
  }, [mapInstance[id], routes])

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

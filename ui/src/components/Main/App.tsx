import { useState } from "react"
import { Outlet } from "react-router-dom"

import { Grid, Layout, theme } from "antd"

import "maplibre-gl/dist/maplibre-gl.css"

import { LeftSider, MainContent, Top } from "@/components"

export const App = () => {
  const [drawerVisible, setDrawerVisible] = useState(false)
  const { md: isDesktop } = Grid.useBreakpoint()
  const {
    token: { colorBgLayout },
  } = theme.useToken()

  const layoutStyle = {
    background: colorBgLayout,
    height: "100vh",
    overflow: "hidden",
  }

  const handleDrawerOpen = () => setDrawerVisible(true)
  const handleDrawerClose = () => setDrawerVisible(false)

  return (
    <Layout style={layoutStyle}>
      <Top isMobile={!isDesktop} onMenuClick={handleDrawerOpen} />
      <Layout>
        <LeftSider
          isMobile={!isDesktop}
          drawerVisible={drawerVisible}
          onDrawerClose={handleDrawerClose}
          {...(!isDesktop && {
            drawerVisible,
            onDrawerClose: handleDrawerClose,
          })}
        />
        <MainContent isMobile={!isDesktop}>
          <Outlet />
        </MainContent>
      </Layout>
    </Layout>
  )
}

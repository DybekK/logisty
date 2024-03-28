/// <reference types="vite/client" />
interface LocalizationApiEnv {
  readonly VITE_PHOTON_URL: string;
  readonly VITE_OSRM_URL: string;
  readonly VITE_NOMINATIM_URL: string;
}

interface MapGlEnv {
  readonly VITE_MAP_GL_STYLE: string;
}

interface ImportMeta {
  readonly env: LocalizationApiEnv & MapGlEnv;
}

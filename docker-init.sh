#!/bin/bash

if [ ! -f maptiler-osm.mbtiles ]; then
    wget -c https://data.maptiler.com/download/WyI5ODIwZGFmYy0zZTQ3LTRiNWQtYjhkMy05MDI3ZmUzYTc5YmEiLCItMSIsMTY5NzBd.Zj4CAA.BxvKMyguzBV7fRShT9mootRwIWA/maptiler-osm-2020-02-10-v3.11-europe_poland.mbtiles?usage=educational -O poland.mbtiles
fi

docker-compose up -d
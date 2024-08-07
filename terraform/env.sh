#!/bin/bash

if [ -f .env ]; then
    export $(cat .env | sed 's/#.*//g' | xargs)
    echo "Environment variables loaded from .env"
fi
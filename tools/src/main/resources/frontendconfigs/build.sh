#!/bin/bash

# Clean up previous build artifacts
echo "Cleaning up previous build artifacts ..."
rm -rf frontend
# Assemble assets
echo "Assembling assets ..."
npx --legacy-peer-deps openmrs@latest assemble \
  --manifest \
  --mode config \
  --config ./configuration/spa-build-config.json \
  --target ./frontend
# Build assets
echo "Building assets for UgandaEMR+..."
CWD=$(pwd)
npx --legacy-peer-deps openmrs@latest build \
  --build-config ./configuration/spa-build-config.json \
  --target ./frontend \
  --page-title "UgandaEMR+" \
  --support-offline false
# Copy required files
echo "Copying required files ..."
cp -r "${CWD}/assets/" "${CWD}/frontend"
# cp "${CWD}/assets/favicon.ico" "${CWD}/frontend"
cp "${CWD}/configuration/frontend-config.json" "${CWD}/frontend"
mv "${CWD}/frontend/frontend-config.json" "${CWD}/frontend/config.json"
# zip -r frontend.zip frontend/*
# Exit with success status
exit 0
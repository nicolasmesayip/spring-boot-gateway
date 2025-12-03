#!/bin/bash
set -e

if [ -z "$1" ]; then
  echo "Usage: $0 <environment> (uat|prod) [image-tag]"
  exit 1
fi

ENV=$1
IMAGE_TAG=${2:-""}  # optional, fallback to none
APPS_FILE="deployments/apps-$ENV.txt"

if [ ! -f "$APPS_FILE" ]; then
  echo "Apps file for environment '$ENV' not found"
  exit 1
fi

echo "Deploying applications for environment: $ENV"
if [ -n "$IMAGE_TAG" ]; then
  echo "Using image tag: $IMAGE_TAG"
fi

while IFS= read -r app || [[ -n "$app" ]]; do
  APP_DIR="deployments/$app/$ENV"
  if [ -d "$APP_DIR" ]; then
    echo "Deploying $app..."

    # Apply base + environment overlays
    kubectl apply -k "$APP_DIR"

    # Patch Deployment image if IMAGE_TAG is provided
    if [ -n "$IMAGE_TAG" ]; then
      DEPLOYMENT_NAME=$app
      IMAGE_NAME="nicomesa/$app:$IMAGE_TAG"
      echo "Updating $DEPLOYMENT_NAME image to $IMAGE_NAME"
      kubectl set image deployment/$DEPLOYMENT_NAME $DEPLOYMENT_NAME=$IMAGE_NAME
    fi

    echo "$app deployed successfully"
  else
    echo "Warning: directory $APP_DIR not found, skipping..."
  fi
done < "$APPS_FILE"

echo "Deployment finished for environment: $ENV"
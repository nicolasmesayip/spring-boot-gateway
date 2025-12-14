#!/bin/bash
set -e

if [ -z "$1" ]; then
  echo "Usage: $0 <image-tag>"
  exit 1
fi

IMAGE_TAG="$1"
REGISTRY="nicomesa"
APPS_FILE="deployments/apps-prod.txt"

echo "Using image tag: $IMAGE_TAG"

while IFS= read -r app || [[ -n "$app" ]]; do
  app=$(echo "$app" | tr -d '\r')
  echo "Building $app..."
  docker build -f "$app/Dockerfile" -t "$REGISTRY/$app:$IMAGE_TAG" .

  echo "Pushing image: $app:$IMAGE_TAG"
  docker push "$REGISTRY/$app:$IMAGE_TAG"
done < "$APPS_FILE"
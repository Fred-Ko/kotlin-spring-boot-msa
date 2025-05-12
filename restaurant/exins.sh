#!/usr/bin/env bash
set -e

while read extension; do
  [[ -z "$extension" || "$extension" =~ ^# ]] && continue
  windsurf --install-extension "$extension" || true
  echo "Installed $extension"
done < vscode-extensions.txt
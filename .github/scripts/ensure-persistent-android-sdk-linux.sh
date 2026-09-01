#!/usr/bin/env bash
set -Eeuo pipefail

# Persistent Android SDK provisioning for Linux self-hosted runners.
# The SDK lives outside the Actions workspace, so it survives checkout cleanup
# and subsequent jobs. Once the requested packages exist, this script performs
# no downloads and returns immediately.

SDK_ROOT="${ANDROID_SDK_ROOT:-${HOME}/.local/share/android-sdk}"
CMDLINE_TOOLS_REVISION="${ANDROID_CMDLINE_TOOLS_REVISION:-15859902}"
CMDLINE_TOOLS_SHA256="${ANDROID_CMDLINE_TOOLS_SHA256:-4e4c464f145a7512b57d088ac6c278c03c9eea610886b35a5e0804e74eedf583}"
CMDLINE_TOOLS_ZIP="commandlinetools-linux-${CMDLINE_TOOLS_REVISION}_latest.zip"
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/${CMDLINE_TOOLS_ZIP}"
SDKMANAGER="${SDK_ROOT}/cmdline-tools/latest/bin/sdkmanager"

PLATFORM_DIR="${SDK_ROOT}/platforms/android-35"
BUILD_TOOLS_DIR="${SDK_ROOT}/build-tools/35.0.0"
PLATFORM_TOOLS_DIR="${SDK_ROOT}/platform-tools"

log() { printf '\n==> %s\n' "$*"; }

export ANDROID_SDK_ROOT="$SDK_ROOT"
export ANDROID_HOME="$SDK_ROOT"

# Fast path: a persistent runner that already has the required SDK does not
# need sdkmanager, repository metadata, cache extraction, or network access.
if [[ -x "${SDKMANAGER}" \
   && -d "${PLATFORM_DIR}" \
   && -d "${BUILD_TOOLS_DIR}" \
   && -d "${PLATFORM_TOOLS_DIR}" ]]; then
  log "Android SDK 35 is already installed at ${SDK_ROOT}; reusing it."
  exit 0
fi

for command in curl unzip sha256sum; do
  command -v "$command" >/dev/null 2>&1 || {
    echo "Missing required command: $command" >&2
    exit 1
  }
done

mkdir -p "${SDK_ROOT}/cmdline-tools"

if [[ ! -x "$SDKMANAGER" ]]; then
  log "Installing Android command-line tools ${CMDLINE_TOOLS_REVISION} once"
  tmp_dir="$(mktemp -d)"
  trap 'rm -rf "$tmp_dir"' EXIT

  archive="${tmp_dir}/${CMDLINE_TOOLS_ZIP}"
  curl --fail --location --retry 3 --output "$archive" "$CMDLINE_TOOLS_URL"
  printf '%s  %s\n' "$CMDLINE_TOOLS_SHA256" "$archive" | sha256sum --check --status

  unzip -q "$archive" -d "$tmp_dir/unpacked"
  rm -rf "${SDK_ROOT}/cmdline-tools/latest"
  mv "${tmp_dir}/unpacked/cmdline-tools" "${SDK_ROOT}/cmdline-tools/latest"
fi

log "Accepting Android SDK licenses"
# sdkmanager exits once all prompts have been consumed; yes may receive SIGPIPE.
yes | "$SDKMANAGER" --sdk_root="$SDK_ROOT" --licenses >/dev/null 2>&1 || true

log "Installing missing persistent Android SDK packages"
"$SDKMANAGER" --sdk_root="$SDK_ROOT" \
  "platform-tools" \
  "platforms;android-35" \
  "build-tools;35.0.0"

for required in "$PLATFORM_DIR" "$BUILD_TOOLS_DIR" "$PLATFORM_TOOLS_DIR"; do
  [[ -d "$required" ]] || {
    echo "Android SDK provisioning did not create expected path: $required" >&2
    exit 1
  }
done

log "Persistent Android SDK is ready at ${SDK_ROOT}"

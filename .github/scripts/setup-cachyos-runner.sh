#!/usr/bin/env bash
set -Eeuo pipefail

# One-time host bootstrap for a CachyOS/Arch Linux x64 runner in shdwmnrchbks.
# Heavy toolchains live outside the Actions workspace and are reused by future
# jobs instead of being restored/downloaded for every run.

log() { printf '\n\033[1;34m==>\033[0m %s\n' "$*"; }
ok() { printf '\033[1;32m✓\033[0m %s\n' "$*"; }
die() { printf '\033[1;31mERROR:\033[0m %s\n' "$*" >&2; exit 1; }

[[ "$EUID" -ne 0 ]] || die "Run this as your normal runner user, not root."
command -v pacman >/dev/null 2>&1 || die "This script expects CachyOS/Arch Linux."
command -v sudo >/dev/null 2>&1 || die "sudo is required."

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
ANDROID_SETUP="${SCRIPT_DIR}/ensure-persistent-android-sdk-linux.sh"

log "Authenticating sudo"
sudo -v

log "Installing persistent CI host dependencies"
sudo pacman -S --needed --noconfirm \
  ca-certificates \
  curl \
  git \
  github-cli \
  gzip \
  jq \
  tar \
  unzip \
  zip \
  docker \
  jdk17-openjdk

if [[ -f "$ANDROID_SETUP" ]]; then
  log "Provisioning persistent Android SDK 35"
  chmod +x "$ANDROID_SETUP"
  "$ANDROID_SETUP"
  ok "Android SDK is persistent at $HOME/.local/share/android-sdk"
else
  printf '\033[1;33m!\033[0m %s was not found; CI can provision the SDK on its first run.\n' "$ANDROID_SETUP" >&2
fi

log "Verifying persistent JDK 17"
JAVA17=/usr/lib/jvm/java-17-openjdk
[[ -x "$JAVA17/bin/java" ]] || die "Expected JDK 17 at $JAVA17"
"$JAVA17/bin/java" -version
ok "JDK 17 is installed persistently"

log "Enabling Docker"
sudo systemctl enable --now docker

GROUP_CHANGED=0
if ! id -nG "$USER" | tr ' ' '\n' | grep -Fxq docker; then
  sudo usermod -aG docker "$USER"
  GROUP_CHANGED=1
  ok "Added $USER to the docker group"
fi

RUNNER_SERVICE="$(systemctl list-unit-files --type=service --no-legend 'actions.runner.*.service' 2>/dev/null | awk 'NR==1 {print $1}')"
if [[ -n "$RUNNER_SERVICE" ]]; then
  log "Restarting GitHub Actions runner service"
  sudo systemctl restart "$RUNNER_SERVICE"
  sudo systemctl --no-pager --full status "$RUNNER_SERVICE" || true
  ok "Restarted $RUNNER_SERVICE"
else
  printf '\033[1;33m!\033[0m No actions.runner.* service was found; restart your runner after this script.\n' >&2
fi

log "Verifying Docker daemon"
sudo docker version >/dev/null
ok "Docker is running"

cat <<EOF

CachyOS runner host dependencies are ready.

Persistent Dayloop toolchain:
  JDK 17:      /usr/lib/jvm/java-17-openjdk
  Android SDK: $HOME/.local/share/android-sdk
  Platform:    Android 35
  Build Tools: 35.0.0
  Gradle:      reused from the runner user's persistent ~/.gradle

On succeeding Dayloop jobs the Linux workflow checks these local paths first.
When they are present, Android SDK provisioning exits immediately without
network access or GitHub cache restore/upload work.

ShadowGarden verify is cross-platform. Its Playwright E2E and release jobs
require a Linux x64 self-hosted runner; Docker on this host satisfies that pool.

Required built-in labels for this host:
  self-hosted, Linux, X64
EOF

if (( GROUP_CHANGED )); then
  echo
  echo "Your interactive shell will see docker-group membership after logout/login."
  echo "The restarted GitHub Actions systemd service already receives the new group."
fi

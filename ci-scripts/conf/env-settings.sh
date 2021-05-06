#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail


# APP Section
APP_NAME='InvoiceAnalytics'
APP_ROOT="${HOME}/projects/CDF_InvoiceAnalytics"

# Build section
BUILD_ID="${BUILD_ID:-1}"

# PKG Section
PKG_REPO='/mnt/cdf/DPR/CDF_InvoiceAnalytics/builds'
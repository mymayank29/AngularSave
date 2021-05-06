#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail


#-------------------------------------------------------------------------------
#   Constants section
#-------------------------------------------------------------------------------
readonly SCRIPT_NAME="$(basename "${0}")"
readonly SCRIPT_PATH="$(readlink -f "${0}")"
readonly BASE_DIR="$(dirname "${SCRIPT_PATH}")"
readonly PROJ_DIR="$(dirname "${BASE_DIR}")"


#-------------------------------------------------------------------------------
#   Import section
#-------------------------------------------------------------------------------
source "${BASE_DIR}/func/kerberos/authentication/krb_auth_keytab"
source "${BASE_DIR}/func/output/print_separator"


#-------------------------------------------------------------------------------
#   Import section
#-------------------------------------------------------------------------------
print_separator '-'
krb_auth_keytab
print_separator '-'
echo "Not implemented error"
print_separator '-'
exit 1
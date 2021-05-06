#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail
set +o xtrace
set -x

#-------------------------------------------------------------------------------
#   Constants section
#-------------------------------------------------------------------------------
readonly PROJECT_NAME="CDF_InvoiceAnalytics_2"
readonly SCRIPT_NAME=$(basename "${0}")
readonly LOCATION=$(readlink -f "${0}")
readonly BASE_DIR=$(dirname "${LOCATION}")
readonly PKG_ROOT=$(dirname "${BASE_DIR}")
readonly PKG_META="${PKG_ROOT}/META.INF"
readonly CONF_DIR="${HOME}/projects/${PROJECT_NAME}/config"
# ----------  end of constants section  ----------


#-------------------------------------------------------------------------------
#   Modules import section
#-------------------------------------------------------------------------------
source "${BASE_DIR}/func/kerberos/authentication/krb_auth_keytab"
source "${BASE_DIR}/func/maven/mvn_get_version"
source "${BASE_DIR}/func/maven/mvn_strip_project_version"
source "${BASE_DIR}/func/output/print_separator"

#-------------------------------------------------------------------------------
#   Settings import section
#-------------------------------------------------------------------------------
source "${BASE_DIR}/conf/env-settings.sh"
# ----------  end of import section  ----------


#-------------------------------------------------------------------------------
#   Default settings section
#-------------------------------------------------------------------------------
APPS_GRP=${APPS_GRP:-"all"}
script_list=()
# ----------  end of Default settings section  ----------


function usage () {
cat << EOF
--------------------------------------------------------------------------------
Usage: ${SCRIPT_NAME} [OPTIONS]

Options
 -h, --help             show this help (-h works with no other options)
 -d, --app-root         set application root folder
 -g, --apps-group       apps group to be installed:
                             all         fetchers and producers (default)
                             fetch       fetchers only
                             produce     producers only


examples:
    ${SCRIPT_NAME} -d ~/projects/CDF_InvoiceAnalytics

    or

    ${SCRIPT_NAME} --app-root ~/projects/CDF_InvoiceAnalytics --apps-group fetch

--------------------------------------------------------------------------------
EOF
}   # ----------  end of function usage  ----------


#-------------------------------------------------------------------------------
#   Options section
#-------------------------------------------------------------------------------
while (( "${#}" )); do
        case "${1}" in
        -d|--app-root)
            readonly APP_ROOT=${2}
            shift 2
            ;;
        -g|--apps-group)
            APPS_GRP=${2}
            shift 2
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        *)
           echo "${SCRIPT_NAME}: invalid option -- '${1}'"
           echo "Try \`${SCRIPT_NAME} --help' for more information."
           exit 1
           ;;
    esac    # --- end of case ---
done
# ----------  end of Options section  ----------


#-------------------------------------------------------------------------------
#   Phase 0 - DEBUG INFO
#-------------------------------------------------------------------------------
cat << EOF
--------------------------------------------------------------------------------
Application root directory  : ${APP_ROOT}
Apps group to be installed  : ${APPS_GRP}
--------------------------------------------------------------------------------
EOF

# check and print META.INF for particular version
if ! cat "${PKG_META}" 2>/dev/null; then
    echo "${0} : Failed. No such file: ${PKG_META}"
    #exit 1
fi


#-------------------------------------------------------------------------------
#   Phase 1 - Deploy phase
#-------------------------------------------------------------------------------
rm -rf "${APP_ROOT}~"
[[ -d "${APP_ROOT}" ]] && mv -fb "${APP_ROOT}" "${APP_ROOT}~"

if ! cp -r "${PKG_ROOT}" "${APP_ROOT}" ; then
    rm -rf "${APP_ROOT}"
    mv -fb "${APP_ROOT}~" "${APP_ROOT}"
    exit 1
fi

# check selected application group
case "${APPS_GRP}" in
    all)
        bash "${BASE_DIR}/deploy-application.sh" selector
        bash "${BASE_DIR}/create-application-properties.sh" selector
        bash "${BASE_DIR}/deploy-application.sh" producer
        bash "${BASE_DIR}/create-application-properties.sh" producer
        bash "${BASE_DIR}/deploy-application.sh" publisher
        bash "${BASE_DIR}/create-application-properties.sh" publisher
        ;;
    fetch)
        bash "${BASE_DIR}/deploy-application.sh" selector
        bash "${BASE_DIR}/create-application-properties.sh" selector
        ;;
    produce)
        bash "${BASE_DIR}/deploy-application.sh" publisher
        bash "${BASE_DIR}/create-application-properties.sh" publisher
        bash "${BASE_DIR}/deploy-application.sh" producer
        bash "${BASE_DIR}/create-application-properties.sh" producer
        ;;
    publish)
        bash "${BASE_DIR}/deploy-application.sh" publisher
        bash "${BASE_DIR}/create-application-properties.sh" publisher
        bash "${BASE_DIR}/deploy-application.sh" producer
        bash "${BASE_DIR}/create-application-properties.sh" producer
        ;;
    *)
        usage
        exit 1
        ;;
esac    # --- end of case ---

# loop over script_list
cp -v "${PKG_META}" "${APP_ROOT}/"
# ----------  end of Phase 1  ----------

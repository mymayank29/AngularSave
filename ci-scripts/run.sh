#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail
set +o xtrace


#-------------------------------------------------------------------------------
#   Constants section
#-------------------------------------------------------------------------------
readonly PROJECT_NAME="CDF_InvoiceAnalytics_v2"
readonly SCRIPT_NAME="$(basename "${0}")"
readonly SCRIPT_PATH="$(readlink -f "${0}")"
readonly BASE_DIR="$(dirname "${SCRIPT_PATH}")"
readonly PROJ_DIR="$(dirname "${BASE_DIR}")"
readonly CONF_DIR="${HOME}/projects/${PROJECT_NAME}/config"

#-------------------------------------------------------------------------------
#   Modules import section
#-------------------------------------------------------------------------------
source "${BASE_DIR}/func/kerberos/authentication/krb_auth_keytab"
source "${BASE_DIR}/func/output/print_separator"

#-------------------------------------------------------------------------------
#   Settings import section
#-------------------------------------------------------------------------------
source "${BASE_DIR}/conf/env-settings.sh"
# ----------  end of import section  ----------


function usage () {
cat << EOF
--------------------------------------------------------------------------------
Usage: ${SCRIPT_NAME} [OPTIONS]

Options
 -h, --help             show this help (-h works with no other options)
     --url              oozie url
     --job              oozie job name:
                             selector
                             producer
                             publisher


example:
    ${SCRIPT_NAME} --url "http://localhost:11000/oozie/" --job fetch-EC

--------------------------------------------------------------------------------
EOF
}   # ----------  end of function usage  ----------


#-------------------------------------------------------------------------------
#   Options section
#-------------------------------------------------------------------------------
while (( "${#}" )); do
        case "${1}" in
        --url)
            OOZIE_URL=${2}
            shift 2
            ;;
        --job)
            OOZIE_JOB=${2}
            shift 2
            ;;
        --tests)
            TESTS_PARAM=${2}
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
#   Default settings section
#-------------------------------------------------------------------------------
OOZIE_URL=${OOZIE_URL:-'http://localhost:11000/oozie/'}
NAME_NODE="$(hdfs getconf -confKey fs.defaultFS)"

# ----------  end of Default settings section  ----------


#-------------------------------------------------------------------------------
#   Main phase
#-------------------------------------------------------------------------------
case "${OOZIE_JOB}" in
    selector)
        JOB_CONFIG="${CONF_DIR}/selector.properties"
        ;;
    producer)
        JOB_CONFIG="${CONF_DIR}/producer.properties"
        ;;
    publisher)
        JOB_CONFIG="${CONF_DIR}/publisher.properties"
        ;;
    *)
        usage
        exit 1
        ;;
esac    # --- end of case ---


print_separator "-"

TESTS_PARAM="${TESTS_PARAM:=}"
# Check if tests should be executed
if [ -z ${TESTS_PARAM} ]
then
    cmd="oozie job -oozie ${OOZIE_URL} -config ${JOB_CONFIG} -run -DnameNode=${NAME_NODE} -DtestMode=db"
else 
    cmd="oozie job -oozie ${OOZIE_URL} -config ${JOB_CONFIG} -run -DnameNode=${NAME_NODE} -DtestMode=test"
fi
echo cmd
echo "${cmd}"
echo
${cmd} | awk '{print $2}'

print_separator "-"
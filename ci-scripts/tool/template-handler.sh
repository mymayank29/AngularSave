#!/usr/bin/env bash
#_shellcheck -e SC1090 "${0}"

set +x
set -o errexit
#set -o nounset
set -o pipefail

#-------------------------------------------------------------------------------
#   Constants section
#-------------------------------------------------------------------------------
readonly SCRIPT_NAME=$(basename "${0}")


#-------------------------------------------------------------------------------
#   Functions section
#-------------------------------------------------------------------------------
function usage () {
cat << EOF
--------------------------------------------------------------------------------
Usage: ${SCRIPT_NAME} [OPTIONS]

Options
 -h, --help                 show this help (-h works with no other options)
 -p, --print                print results of the variable expansion(s)
 -f, --env-file FILE        use environment variables from file
 -t, --cfg-file FILE        use configuration template from file


Examples:
    ${SCRIPT_NAME} -t ${HOME}/template.cfg -f ${HOME}/variables-file.env

    or

    ${SCRIPT_NAME} \\
        --env-file ${HOME}/variables-file.env \\
        --cfg-file ${HOME}/template.cfg \\
        --print

--------------------------------------------------------------------------------
EOF
}

function strip_brackets () {
    sed -e 's/^{{//' -e 's/}}$//'
}

function get_keys() {
    local -r RE='\{{[a-zA-Z_]+[0-9a-zA-Z_]+\}}'
    if [ -r "${1}" ]; then
        grep -oE "${RE}" "${1}" | sort -u | strip_brackets
    else
        echo "${1}" | grep -oE "${RE}" | sort -u | strip_brackets
    fi
}

function get_val() {
    local val

    val=$(set | grep "^${1}=" | cut -d'=' -f2)

    if [ -z "${val}" ]; then
        val="<None>"
    fi
    echo "${val}"
}

function rebase_str() {
    local str key keys val

    str="${1}"
    keys="$(get_keys "${str}")" || true

    for key in ${keys[*]}; do
        val=$(get_val "${key}")
        str="${str//'{{'${key}'}}'/${val}}"
    done
    echo "${str}"
}

function print_separator() {
    for (( i=1; i<=80; i++ )); do
       echo -ne "${1}"
    done
    echo -ne "\n"
}


#-------------------------------------------------------------------------------
#   Main section
#-------------------------------------------------------------------------------
while (( "${#}" )); do
    case "${1}" in
        -p|--print)
            PRINT=true
            shift 1
            ;;
        -t|--cfg-file)
            readonly CFG_FILE="${2}"
            shift 2
            ;;
        -f|--env-file)
            ENV_FILE="${2}"
            shift 2
            ;;
        -h|--help)
            { usage; exit 0; }
            ;;
        *)
           echo "${SCRIPT_NAME}: invalid option -- '${1}'"
           echo "Try \`${SCRIPT_NAME} --help' for more information."
           exit 1
           ;;
    esac
done

PRINT=${PRINT:-false}
ENV_FILE=${ENV_FILE:-false}

# check if configuration template readable
test -r "${CFG_FILE}" || { usage; exit 1; }

# import variables if env file readable
test -r "${ENV_FILE}" && { source "${ENV_FILE}"; }

if "${PRINT}"; then
    print_separator "-"
    printf " %-25s : %-25s \n" "Key" "Value"
    print_separator "-"
    for key in $(get_keys "${CFG_FILE}"); do
        val=$(get_val "${key}")
        printf " %-25s : %-25s \n" "${key}" "${val}"
    done
    print_separator "-"
else
    #    Replace all {{VAR}} with variable value in ${CFG_FILE}
    while read -r line ; do
        rebase_str "${line}"
    done < "${CFG_FILE}"
fi
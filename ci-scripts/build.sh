#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o pipefail
set +o xtrace

#-------------------------------------------------------------------------------
#   Constants section
#-------------------------------------------------------------------------------
readonly SCRIPT_NAME="$(basename "${0}")"
readonly SCRIPT_PATH="$(readlink -f "${0}")"
readonly BASE_DIR="$(dirname "${SCRIPT_PATH}")"
readonly PROJ_DIR="$(dirname "${BASE_DIR}")"
pushd "${PROJ_DIR}" > /dev/null
#-------------------------------------------------------------------------------
#   Modules import section
#-------------------------------------------------------------------------------
source "${BASE_DIR}/func/git/git_get_src"
source "${BASE_DIR}/func/git/git_rm_from"
source "${BASE_DIR}/func/kerberos/authentication/krb_auth_keytab"
source "${BASE_DIR}/func/maven/mvn_get_version"
source "${BASE_DIR}/func/maven/mvn_strip_project_version"
print_separator() { echo '--------------------------------------------------------------------------------'; }
#-------------------------------------------------------------------------------
#   Settings import section
#-------------------------------------------------------------------------------
source "${BASE_DIR}/conf/env-settings.sh"
#-------------------------------------------------------------------------------
#   Build section
#-------------------------------------------------------------------------------
print_separator
echo "Build phase started"
print_separator
#-------------------------------------------------------------------------------
#cd ..
export BUILD_VER="$(mvn_strip_project_version)${BUILD_ID}"
export BUILD_TAG="v${BUILD_VER}"
export GIT_URL="$(git config --get remote.origin.url)"
export GIT_BRANCH="$(git branch | awk '{print $2}')"
export GIT_COMMIT="$(git log -1 --format=%H)"
export GIT_MESSAGE="$(git log -1 --format=%s)"
export GIT_TIME="$(git log -1 --format=%ad)"
export GIT_AUTHOR="$(git log -1 --format=%an)"


echo "Installing the parent pom only with non-recursive option with current version"
echo
mvn clean install --non-recursive

echo "Updating project version to ${BUILD_VER}"
echo
mvn versions:set -DnewVersion="${BUILD_VER}"

echo "Installing the parent pom only with updated version"
echo
mvn clean install --non-recursive

#Update version
#mvn versions:set -DnewVersion="${BUILD_VER}" > /dev/null

echo "Build certificate path ${HOME}/buildcert"
echo
mvn clean install 

#Save TAG to env_file for future Jenkins Variable Injection and Tagging build
echo TAG="${BUILD_TAG}" > ./env_file

#mvn versions:set -DnewVersion="${BUILD_VER}" > /dev/null
#mvn versions:set -DnewVersion="${BUILD_VER}
#mvn clean package -Dmaven.test.skip=true -Ddpr-location="${PROJ_DIR}"

bash "${BASE_DIR}/tool/template-handler.sh" \
    --cfg-file "${BASE_DIR}/conf/meta.inf.template" \
    --env-file "${BASE_DIR}/conf/env-settings.sh" \
    | sed -e "s/'\+/'/g" -e "s/\"\+/'/g" > ${PROJ_DIR}/META.INF

cp -v ${PROJ_DIR}/META.INF ${PKG_REPO}/${BUILD_VER}/
#-------------------------------------------------------------------------------

# remove sed temp files
#find "${PROJ_DIR}" -type f -name "sed*" -exec rm -f {} &>/dev/null \;

# copy package to repo
#test -d "${PKG_REPO}" || mkdir -p "${PKG_REPO}"
#rm -rf "${PKG_REPO:?}/${BUILD_VER}"
#mkdir -p "${PKG_REPO}/${BUILD_VER}/ci-scripts"
#cp -rf "${PROJ_DIR}" "${PKG_REPO}/${BUILD_VER}"
#mv -f "${PKG_REPO}/${BUILD_VER}/ci-scripts/fetch_files" "${PKG_REPO}/${BUILD_VER}"
#mv -f "${PKG_REPO}/${BUILD_VER}/ci-scripts/selector" "${PKG_REPO}/${BUILD_VER}"
#mv -f "${PKG_REPO}/${BUILD_VER}/ci-scripts/producer" "${PKG_REPO}/${BUILD_VER}"
#mv -f "${PKG_REPO}/${BUILD_VER}/ci-scripts/publisher" "${PKG_REPO}/${BUILD_VER}"
#cp "deploy-application.sh" "${PKG_REPO}/${BUILD_VER}/"
#cp "create-application-properties.sh" "${PKG_REPO}/${BUILD_VER}/"
#-------------------------------------------------------------------------------
print_separator
echo "Build phase complete"
print_separator
#cat "${PKG_REPO}/${BUILD_VER}/META.INF"
#mv -f "${PKG_REPO}/${BUILD_VER}/ci-scripts/META.INF" "${PKG_REPO}/${BUILD_VER}"
print_separator
#-------------------------------------------------------------------------------
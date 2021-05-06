#!/usr/bin/env bash

readonly SCRIPT_PATH="$(readlink -f "${0}")"
readonly BASE_DIR="$(dirname "${SCRIPT_PATH}")"
readonly PKG_ROOT=$(dirname "${BASE_DIR}")
readonly PKG_META="${PKG_ROOT}/META.INF"

PROJECT=CDF_InvoiceAnalytics
if [ $1 ];
	then
		APPLICATION=$1;
	else
		echo "Specify application name for deployment! exit...";
		exit;
fi

DEST_SANDBOX=${USER}

FILE=~/conf/user-settings.cfg
while read LINE; do
    eval $LINE
done < $FILE

DPR_LOCATION=${NASDIR}/DPR

APPLICATION_LOCAL_LOCATION="${PKG_ROOT}/${APPLICATION}"
APPLICATION_HDFS_LOCATION="hdfs://nameservice1/user/${DEST_SANDBOX}/${PROJECT}_v2/app/${APPLICATION}"

echo "Deploying ${APPLICATION_LOCAL_LOCATION} to ${APPLICATION_HDFS_LOCATION}"

echo "Cleaning ${APPLICATION_HDFS_LOCATION}..."
hdfs dfs -rm -r -f -skipTrash ${APPLICATION_HDFS_LOCATION}

echo "Recreating HDFS directory..."
hdfs dfs -mkdir -p ${APPLICATION_HDFS_LOCATION}/

echo "Copying files..."
echo "Copy from ${APPLICATION_LOCAL_LOCATION} to ${APPLICATION_HDFS_LOCATION}"

hdfs dfs -put -f ${APPLICATION_LOCAL_LOCATION}/* ${APPLICATION_HDFS_LOCATION}/
hdfs dfs -put -f /etc/hive/conf/hive-site.xml ${APPLICATION_HDFS_LOCATION}/

hdfs dfs -put -f ${DPR_LOCATION}/${PROJECT}/models ${APPLICATION_HDFS_LOCATION}/models
hdfs dfs -put -f ${DPR_LOCATION}/${PROJECT}/testSource ${APPLICATION_HDFS_LOCATION}/testSource
hdfs dfs -put -f ${DPR_LOCATION}/${PROJECT}/supplemental ${APPLICATION_HDFS_LOCATION}/supplemental
hdfs dfs -put -f ${DPR_LOCATION}/${PROJECT}/resources ${APPLICATION_HDFS_LOCATION}/resources

cloderaPath=`find /opt/cloudera/parcels -name "CDH-*cdh*" | head -1`
slf4jPath=`find ${cloderaPath}/lib/spark/python/lib -name "py4j*.zip" | head -1`
hdfs dfs -put ${slf4jPath} ${APPLICATION_HDFS_LOCATION}/py4j.zip

echo "...done"

#!/bin/bash

set -x

PROJECT_NAME=CDF_InvoiceAnalytics_v2
if [ $1 ];
	then
		APPLICATION_NAME=$1;
	else
		echo "Specify application name! exit...";
		exit;
fi

if [ $2 ];
	then
		DB_NAME='udap_pond';
	else
		DB_NAME='_invoiceanalytics_v2';
fi

readonly CONF_DIR="${HOME}/projects/${PROJECT_NAME}/config"


FILE=~/conf/user-settings.cfg
while read LINE; do
    eval $LINE
done < $FILE

FILE=~/conf/env-settings.cfg
while read LINE; do
    eval $LINE
done < $FILE

mkdir -p ${CONF_DIR}

NAMENODE=`hdfs getconf -confKey fs.defaultFS`
HIVE_USER_POSTFIX='_dwepcostreconciliation_'`echo $USER | awk -F "_" '{print $NF}'`

#create fetch-reports properties file
cat > $CONF_DIR/$APPLICATION_NAME.properties << EOF


# basic
sandboxName=${USER}
nameNode=${NAMENODE}
jobTracker=${JOBTRACKER}
oozieURI=${OOZIEURI}
rcpt=${RCPT}
PWKS_KEY=${PWKS_KEY}
PWKS_FILE=${PWKS_FILE}
queueName=${YARNQUEUE}
keytab=${KEYTAB_FILE}
principal=${USER}
StgPostfix=${POSTFIX}
hivessl = ${HIVESSL}

jdbcUsername=${targetGOMICAJdbcUsername}
echo "jdbcUsername"
echo ${targetGOMICAJdbcUsername}

jdbcPassAlias=${targetGOMICAJdbcPassAlias}
echo "jdbcPassAlias"
echo ${targetGOMICAJdbcPassAlias}

jdbcConnection=${targetGOMICAJdbcConnection}
echo "jdbcConnection"
echo ${targetGOMICAJdbcConnection}

#jdbcUsername=${targetGOMICAJdbcUsername}
#jdbcPassAlias=${targetGOMICAJdbcPassAlias}
#jdbcConnection=${targetGOMICAJdbcConnection}
schema=gomica

# spark
sparkMaster=yarn-cluster
sparkMode=cluster
sparkDriverMemory=6g
sparkDriverCores=2
sparkNumExecutors=10
sparkExecutorMemory=8g
sparkExecutorCores=2

# hive
jdbcURL=jdbc:hive2://${hiveUri}/default;${HIVESSL}
jdbcPrincipal=${hivePrincipal}
lakeHiveDB=${maskLakeHiveDB}${DB_NAME}${hiveSchemaPostfix}
stageHiveDB=${maskStageHiveDB}${DB_NAME}${hiveSchemaPostfix}
rawSAPBWDB=${maskRawHiveDB}_SAPBW${hiveSchemaPostfix}
rawSimSmartHiveDB=${maskRawHiveDB}_simsmart_inc${hiveSchemaPostfix}
rawWellViewHiveDB=${maskRawHiveDB}_wellview${hiveSchemaPostfix}
rawSimSmartHiveDB_New = ${maskRawHiveDB}_simsmart${hiveSchemaPostfix}

# oozie
workflowPath=${NAMENODE}/user/${USER}/${PROJECT_NAME}/app/${APPLICATION_NAME}/oozie
oozie.wf.application.path=${NAMENODE}/user/${USER}/${PROJECT_NAME}/app/${APPLICATION_NAME}/oozie
oozie.use.system.libpath=true
oozie.wf.validate.ForkJoin=false
oozie.wf.rerun.failnodes=true
oozie.libpath=${NAMENODE}/user/oozie/share/lib/hive,${NAMENODE}/user/${USER}/${PROJECT_NAME}/app/${APPLICATION_NAME}/lib

oozie.action.sharelib.for.sqoop=hive,hcatalog,sqoop
oozie.action.sharelib.for.hive=hive,hcatalog,sqoop
# directories
sandboxDir=${NAMENODE}/user/\${sandboxName}
commonDir=${NAMENODE}/user/${USER}/CDF_Common
commonLibDir=${NAMENODE}/user/${USER}/${PROJECT_NAME}/app/${APPLICATION_NAME}/lib
projectDir=${NAMENODE}/user/${USER}/${PROJECT_NAME}
applicationDir=${NAMENODE}/user/${USER}/${PROJECT_NAME}/app/${APPLICATION_NAME}
appPyScriptDir=\${applicationDir}/pyscript
reporterWorkflowDir=${NAMENODE}/user/${USER}/${PROJECT_NAME}/app/${APPLICATION_NAME}/reporter
appPreDir=${PREPAREDDIR}/${PROJECT_NAME}
appStgDir=${STAGEDIR}/${PROJECT_NAME}
applicationRawDataDir=${NAMENODE}${DATARAWDIR}/${PROJECT_NAME}
contractOcrSrcDir=/data/cdf_drop/EDAP_GOMICA/Inbound/DEV/
contractOcrDestDir=${STAGEDIR}/${PROJECT_NAME}/ocr_res



# inv metadata
metastoreUri=${METASTOREURI}
metastorePrincipal=${METASTOREPRINCIPAL}
hiveMetastoreUris=${HIVEURI}
hiveMetastorePrincipal=${HIVEPRINCIPAL}
hiveUri=${hiveUri}
impalaHostInv=${impalaHost}

#project properties
testMode=false

hadoopUri={{NAMENODE}}
hadoopResources=/etc/hadoop/conf/core-site.xml;/etc/hadoop/conf/hdfs-site.xml

EOF



echo "....done"



echo "properties file ${CONF_DIR}/${APPLICATION_NAME}.properties generated"
echo "lakeHiveDB:"
grep "lakeHiveDB" ${CONF_DIR}/${APPLICATION_NAME}.properties
echo "stageHiveDB:"
grep "stageHiveDB" ${CONF_DIR}/${APPLICATION_NAME}.properties
echo "appStgDir:"
grep "appStgDir" ${CONF_DIR}/${APPLICATION_NAME}.properties
echo "applicationDir:"
grep "applicationDir" ${CONF_DIR}/${APPLICATION_NAME}.properties

grep "rawSimSmartHiveDB" ${CONF_DIR}/${APPLICATION_NAME}.properties
grep "rawWellViewHiveDB" ${CONF_DIR}/${APPLICATION_NAME}.properties
grep "jdbc" ${CONF_DIR}/${APPLICATION_NAME}.properties
grep "Application Directory HIVE-site.xml" ${applicationDir}
echo "properties file ${CONF_DIR}/${APPLICATION_NAME}.properties generated"



echo "queueName:"
grep "queueName" ${CONF_DIR}/${APPLICATION_NAME}.properties
echo "principal:"
grep "principal" ${CONF_DIR}/${APPLICATION_NAME}.properties
echo "keytab:"
grep "keytab" ${CONF_DIR}/${APPLICATION_NAME}.properties





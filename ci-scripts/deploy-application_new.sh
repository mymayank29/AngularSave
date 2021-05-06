#!/bin/bash

USER_PROPERTIES_MASK=$1
ENV_PROPERTIES_MASK=$2

# functions
readJobProperty() {
    PROPERTY_VALUE=$(grep "$1=" $CURRENT_DIR/oozie/job.properties | cut -f2 -d=)

    echo "$PROPERTY_VALUE"
}

initPaths() {
    # variables
    CURRENT_DIR=$(cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd)

    PROJECT_NAME=$(readJobProperty projectName)
    APP_NAME=$(readJobProperty appName)

    HDFS_SANDBOX_PATH=/user/$USER
    HDFS_PROJECT_DIR=$HDFS_SANDBOX_PATH/$PROJECT_NAME
    HDFS_APP_DIR=$HDFS_PROJECT_DIR/$APP_NAME

    USER_SETTINGS_FILE=/home/$USER/conf/user-settings.cfg
    DATA_STAGE_DIR_VALUE=`cat ${USER_SETTINGS_FILE} | grep STAGEDIR | cut -f2- -d=`
    DATA_STAGE_DIR=$(eval echo $DATA_STAGE_DIR_VALUE)
    DATA_RAW_DIR_VALUE=`cat ${USER_SETTINGS_FILE} | grep DATARAWDIR | cut -f2- -d=`
    DATA_RAW_DIR=$(eval echo $DATA_RAW_DIR_VALUE)
}

reloadApplicationFiles() {
    LIBRARIES=$1
    SCRIPTS=$2

    # application-specific files
    echo "Reloading application files in $HDFS_SANDBOX_PATH..."

    hdfs dfs -rm -r -skipTrash $HDFS_APP_DIR

    hdfs dfs -mkdir -p $HDFS_APP_DIR/oozie \
        $HDFS_APP_DIR/lib

    hdfs dfs -put $CURRENT_DIR/oozie/*.xml $HDFS_APP_DIR/oozie &
    hdfs dfs -put $LIBRARIES $HDFS_APP_DIR/lib &

    if [ -d "$CURRENT_DIR/hive" ]; then
        hdfs dfs -mkdir $HDFS_APP_DIR/hive

        hdfs dfs -put $CURRENT_DIR/hive/*.hql $HDFS_APP_DIR/hive &
    fi

    if [ -d "$CURRENT_DIR/oozie/sub-workflows" ]; then
        hdfs dfs -mkdir $HDFS_APP_DIR/oozie/sub-workflows

        hdfs dfs -put $CURRENT_DIR/oozie/sub-workflows/*.xml $HDFS_APP_DIR/oozie/sub-workflows &
    fi

    hdfs dfs -mkdir $HDFS_APP_DIR/shell
    hdfs dfs -put $SCRIPTS $HDFS_APP_DIR/shell &


    if [ -d "$CURRENT_DIR/shell" ]; then
        hdfs dfs -put $CURRENT_DIR/shell/* $HDFS_APP_DIR/shell &
    fi

    if [ -d "$CURRENT_DIR/python" ]; then
        hdfs dfs -mkdir $HDFS_APP_DIR/python

        hdfs dfs -put $CURRENT_DIR/python/* $HDFS_APP_DIR/python &
    fi
}

createConfigProperty() {
    PROPERTY="    <property>\n"
    PROPERTY+="        <name>$1</name>\n"
    PROPERTY+="        <value>$2</value>\n"
    PROPERTY+="    </property>\n\n"

    echo "$PROPERTY"
}

reloadConfiguration() {
    echo "Composing config-default.xml and hive-config.xml..."

    # basic settings
    USER_SETTINGS_FILE=/home/$USER/conf/user-settings.cfg
    ENV_SETTINGS_FILE=/home/$USER/conf/env-settings.cfg

    JOB_TRACKER=$(cat /etc/hadoop/conf/yarn-site.xml | grep -A 1 yarn.resourcemanager.address | sed -n '2p' | tr -d ' ' | sed "s|<value>||" | sed "s|</value>||")
    HIVE_URI=$(cat "${USER_SETTINGS_FILE}" | grep -A 0 HIVEURI | sed -e 's#.*=\(\)#\1#')
    HIVE_PRINCIPAL=$(cat "${USER_SETTINGS_FILE}" | grep -A 0 HIVEPRINCIPAL | sed -e 's#.*=\(\)#\1#')
    PWKS_KEY=$(cat "${USER_SETTINGS_FILE}" | grep -A 0 PWKS_KEY | awk -F '"' '{print $2}')
    PWKS_FILE=$(cat "${USER_SETTINGS_FILE}" | grep -A 0 PWKS_FILE | awk -F '"' '{print $2}')

    CONFIG_DEFAULT_XML="<configuration>\n\n"
    CONFIG_DEFAULT_XML+=$( createConfigProperty jobTracker $JOB_TRACKER )
    CONFIG_DEFAULT_XML+=$( createConfigProperty USER $USER )
    CONFIG_DEFAULT_XML+=$( createConfigProperty HIVEPRINCIPAL ${HIVE_PRINCIPAL})
    CONFIG_DEFAULT_XML+=$( createConfigProperty HIVEURI ${HIVE_URI})
    CONFIG_DEFAULT_XML+=$( createConfigProperty PWKS_KEY $PWKS_KEY )
    CONFIG_DEFAULT_XML+=$( createConfigProperty PWKS_FILE $PWKS_FILE )

    # user settings
    if [ -f "$USER_SETTINGS_FILE" ]; then
        while read LINE
        do
            if [ "$LINE" != "" ] && [[ $LINE =~ ^[^#] ]]; then
                PROPERTY_NAME=$(echo $LINE | cut -f1 -d=)
                if [[ $PROPERTY_NAME =~ ^$USER_PROPERTIES_MASK ]]; then
                    PROPERTY_VALUE=$(eval echo $LINE | cut -f2- -d=)

                    #remove double quotes if there are any
                    PROPERTY_VALUE="${PROPERTY_VALUE%\"}"
                    PROPERTY_VALUE="${PROPERTY_VALUE#\"}"

                    CONFIG_DEFAULT_XML+=$( createConfigProperty $PROPERTY_NAME "$PROPERTY_VALUE" )
                fi
            fi
        done < $USER_SETTINGS_FILE
    else
        echo "Warning! File $USER_SETTINGS_FILE was not found."
    fi

    # environment settings
    if [ -f "$ENV_SETTINGS_FILE" ]; then
        while read LINE
        do
            if [ "$LINE" != "" ] && [[ $LINE =~ ^[^#] ]]; then
                PROPERTY_NAME=$(echo $LINE | cut -f1 -d=)
                if [[ $PROPERTY_NAME =~ ^$ENV_PROPERTIES_MASK ]]; then
                    PROPERTY_VALUE=$(echo $LINE | cut -f2- -d=)

                    #remove double quotes if there are any
                    PROPERTY_VALUE="${PROPERTY_VALUE%\"}"
                    PROPERTY_VALUE="${PROPERTY_VALUE#\"}"

                    CONFIG_DEFAULT_XML+=$( createConfigProperty $PROPERTY_NAME "$PROPERTY_VALUE" )
                fi
            fi
        done < $ENV_SETTINGS_FILE
    else
        echo "Warning! File $ENV_SETTINGS_FILE was not found."
    fi

    CONFIG_DEFAULT_XML+="</configuration>\n"

    # reload config-default.xml file
    echo -e "$CONFIG_DEFAULT_XML" | hdfs dfs -put -f - $HDFS_APP_DIR/oozie/config-default.xml &

    # reload hive-config.xml file
    HIVE_CONFIG_XML="<configuration>\n\n"
    HIVE_CONFIG_XML+=$( createConfigProperty mapred.job.queue.name hive )
    HIVE_CONFIG_XML+=$( createConfigProperty hive.server2.authentication kerberos )
    HIVE_CONFIG_XML+=$( createConfigProperty hive.metastore.sasl.enabled true )
    HIVE_CONFIG_XML+=$( createConfigProperty hive.metastore.kerberos.principal hive/_HOST@CT.CHEVRONTEXACO.NET )
    HIVE_CONFIG_XML+=$( createConfigProperty hive.server2.authentication.kerberos.principal hive/_HOST@CT.CHEVRONTEXACO.NET )
    HIVE_CONFIG_XML+=$( createConfigProperty hive.variable.substitute.depth 1000 )

    HIVE_CONFIG_XML+="</configuration>\n"

    echo -e "$HIVE_CONFIG_XML" | hdfs dfs -put -f - $HDFS_APP_DIR/conf/hive-config.xml &
}

reloadApplicationFilesNew(){
    hdfs dfs -rm -r -skipTrash $HDFS_APP_DIR
    hdfs dfs -mkdir -p $HDFS_APP_DIR
    hdfs dfs -put $CURRENT_DIR/* $HDFS_APP_DIR
}

# main work
echo ">>"

initPaths
reloadApplicationFilesNew
reloadConfiguration ${USER_PROPERTIES_MASK} ${ENV_PROPERTIES_MASK}
#reloadConfiguration "RCPT|hiveSchemaPostfix|DATARAWDIR|STAGEDIR|PREPAREDDIR|PUBLISHEDDIR|YARNQUEUE|HIVE_RELOADABLE_AUX_JARS_PATH|METASTOREURI|METASTOREPRINCIPAL" "targetPulse"

wait

echo "Application $APP_NAME has been deployed."

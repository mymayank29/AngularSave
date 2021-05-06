#!/usr/bin/env bash

spark2-submit --class ${MAIN_CLASS} \
                         --master yarn \
                         --queue "${QUEUE_NAME}" \
                         --principal "${PRINCIPAL}" \
                         --keytab "${KEYTAB}" \
                         --executor-memory 9g \
                         --num-executors 12 \
                         --driver-memory 5g \
                         --conf spark.dynamicAllocation.enabled=false \
                         --driver-class-path . \
                         --verbose \
                         producer.jar "${TEST_MODE}" "${OUTPUT_PATH}" "${LAKE_DATABASE}" "${STG_PATH}" "${STG_DATABASE}" "${APPLICATION_DIR}"

#don't add hive-site.xml in --files, already added through oozie action
#!/bin/bash

spark2-submit --master "${SPARK_MASTER}"  \
            --deploy-mode "${DEPLOY_MODE}" \
            --py-files "${SPARK_JOB_ARCHIVE}" \
            --files hive-site.xml \
            --queue "${QUEUE_NAME}" \
            --principal "${PRINCIPAL}" \
            --keytab "${KEYTAB}" \
            --driver-memory "${DRIVER_MEMORY}" \
            --executor-memory "${EXECUTOR_MEMORY}" \
            --executor-cores "${EXECUTOR_CORES}" \
            --num-executors "${NUM_OF_EXECUTORS}" \
            --conf spark.pyspark.python="${PYTHON_ENV_ROOT_PATH}"/bin/python3 \
            --conf spark.dynamicAllocation.enabled=false \
            --conf spark.yarn.am.memory="${DRIVER_MEMORY}" \
            --conf spark.default.parallelism="${SPARK_DEFAULT_PARALLELISM}" \
            --driver-class-path . \
        ocr_pyspark.py "${SPARK_MASTER}" "${SOURCE_DB}" "${OUTPUT_PATH}" "${NAS_DIR}"
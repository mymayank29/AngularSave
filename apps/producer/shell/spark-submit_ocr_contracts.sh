#!/usr/bin/env bash

spark2-submit --class ${MAIN_CLASS} \
                         --master yarn \
                         --queue "${QUEUE_NAME}" \
                         --principal "${PRINCIPAL}" \
                         --keytab "${KEYTAB}" \
                         --executor-memory 14g \
                         --num-executors 12 \
                         --driver-memory 4g \
                         --conf spark.dynamicAllocation.enabled=false \
                         --driver-class-path . \
                         --verbose \
                         --jars text-scraping-ocr-parser-jar.jar,javacpp.jar,leptonica.jar,leptonica_lin_x64.jar,opencv.jar,opencv_lin_x64.jar,tesseract.jar,tesseract_lin_x64.jar \
                         producer.jar "${APPLICATION_DIR}" "${CONTRACTS_SRC_DIR}" "${CONTRACTS_DEST_DIR}"

#don't add hive-site.xml in --files, already added through oozie action
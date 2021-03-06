DROP TABLE IF EXISTS ${DESTINATION_DB}.WVJOBREPORT;

CREATE TABLE ${DESTINATION_DB}.WVJOBREPORT
STORED AS parquet LOCATION "${LOCATION_PATH}/WVJOBREPORT"
AS
SELECT
    jr.*
FROM ${SOURCE_DB}.WVJOBREPORT jr
WHERE jr.idrecparent IN (SELECT j.idrec FROM ${DESTINATION_DB}.WVJOB j)
;
ALTER TABLE ${DESTINATION_DB}.WVJOBREPORT SET TBLPROPERTIES('EXTERNAL'='TRUE');
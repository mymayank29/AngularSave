DROP TABLE IF EXISTS ${DESTINATION_DB}.WVJOBOFFLINEDATESTIMELOG;

CREATE TABLE ${DESTINATION_DB}.WVJOBOFFLINEDATESTIMELOG
STORED AS parquet LOCATION "${LOCATION_PATH}/WVJOBOFFLINEDATESTIMELOG"
AS
SELECT
    j.*
FROM ${SOURCE_DB}.WVJOBOFFLINEDATESTIMELOG j
WHERE TRIM(UPPER(j.idwell)) IN (SELECT TRIM(UPPER(h.idwell)) FROM ${DESTINATION_DB}.WVWELLHEADER h);


ALTER TABLE ${DESTINATION_DB}.WVJOBOFFLINEDATESTIMELOG SET TBLPROPERTIES('EXTERNAL'='TRUE');

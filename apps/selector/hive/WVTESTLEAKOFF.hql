DROP TABLE IF EXISTS ${DESTINATION_DB}.WVTESTLEAKOFF;

CREATE TABLE ${DESTINATION_DB}.WVTESTLEAKOFF
STORED AS parquet LOCATION "${LOCATION_PATH}/WVTESTLEAKOFF"
AS
SELECT
    j.*
FROM ${SOURCE_DB}.WVTESTLEAKOFF j
WHERE TRIM(UPPER(j.idwell)) IN (SELECT TRIM(UPPER(h.idwell)) FROM ${DESTINATION_DB}.WVWELLHEADER h);


ALTER TABLE ${DESTINATION_DB}.WVTESTLEAKOFF SET TBLPROPERTIES('EXTERNAL'='TRUE');

DROP TABLE IF EXISTS ${DESTINATION_DB}.WVWELLBOREDIRSURVEYDATA;

CREATE TABLE ${DESTINATION_DB}.WVWELLBOREDIRSURVEYDATA
STORED AS parquet LOCATION "${LOCATION_PATH}/WVWELLBOREDIRSURVEYDATA"
AS
SELECT
    j.*
FROM ${SOURCE_DB}.WVWELLBOREDIRSURVEYDATA j
WHERE TRIM(UPPER(j.idwell)) IN (SELECT TRIM(UPPER(h.idwell)) FROM ${DESTINATION_DB}.WVWELLHEADER h);


ALTER TABLE ${DESTINATION_DB}.WVWELLBOREDIRSURVEYDATA SET TBLPROPERTIES('EXTERNAL'='TRUE');

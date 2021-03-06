DROP TABLE IF EXISTS ${DESTINATION_DB}.WVWELLBOREDIRSURVEY;

CREATE TABLE ${DESTINATION_DB}.WVWELLBOREDIRSURVEY
STORED AS parquet LOCATION "${LOCATION_PATH}/WVWELLBOREDIRSURVEY"
AS
SELECT
    j.*
FROM ${SOURCE_DB}.WVWELLBOREDIRSURVEY j
WHERE TRIM(UPPER(j.idwell)) IN (SELECT TRIM(UPPER(h.idwell)) FROM ${DESTINATION_DB}.WVWELLHEADER h);


ALTER TABLE ${DESTINATION_DB}.WVWELLBOREDIRSURVEY SET TBLPROPERTIES('EXTERNAL'='TRUE');

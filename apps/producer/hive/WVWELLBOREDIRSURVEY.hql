DROP TABLE IF EXISTS ${DESTINATION_DB}.WVWELLBOREDIRSURVEY;
CREATE TABLE ${DESTINATION_DB}.WVWELLBOREDIRSURVEY
STORED AS parquet
LOCATION "${LOCATION_PATH}/WVWELLBOREDIRSURVEY"
AS
SELECT * FROM ${SOURCE_DB}.WVWELLBOREDIRSURVEY T;

ALTER TABLE ${DESTINATION_DB}.WVWELLBOREDIRSURVEY SET TBLPROPERTIES('EXTERNAL'='TRUE');
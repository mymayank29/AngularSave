DROP TABLE IF EXISTS ${DESTINATION_DB}.WVWELLBOREDIRSURVEYDATA;
CREATE TABLE ${DESTINATION_DB}.WVWELLBOREDIRSURVEYDATA
STORED AS parquet
LOCATION "${LOCATION_PATH}/WVWELLBOREDIRSURVEYDATA"
AS
SELECT * FROM ${SOURCE_DB}.WVWELLBOREDIRSURVEYDATA T;

ALTER TABLE ${DESTINATION_DB}.WVWELLBOREDIRSURVEYDATA SET TBLPROPERTIES('EXTERNAL'='TRUE');

DROP TABLE IF EXISTS ${DESTINATION_DB}.WVWELLBOREFORMATION;
CREATE TABLE ${DESTINATION_DB}.WVWELLBOREFORMATION
STORED AS parquet
LOCATION "${LOCATION_PATH}/WVWELLBOREFORMATION"
AS
SELECT * FROM ${SOURCE_DB}.WVWELLBOREFORMATION T;

ALTER TABLE ${DESTINATION_DB}.WVWELLBOREFORMATION SET TBLPROPERTIES('EXTERNAL'='TRUE');

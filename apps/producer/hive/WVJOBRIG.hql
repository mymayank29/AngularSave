DROP TABLE IF EXISTS ${DESTINATION_DB}.WVJOBRIG;
CREATE TABLE ${DESTINATION_DB}.WVJOBRIG
STORED AS parquet
LOCATION "${LOCATION_PATH}/WVJOBRIG"
AS
SELECT * FROM ${SOURCE_DB}.WVJOBRIG T;

ALTER TABLE ${DESTINATION_DB}.WVJOBRIG SET TBLPROPERTIES('EXTERNAL'='TRUE');

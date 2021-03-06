DROP TABLE IF EXISTS ${DESTINATION_DB}.WVJOBDRILLBIT;
CREATE TABLE ${DESTINATION_DB}.WVJOBDRILLBIT
STORED AS parquet
LOCATION "${LOCATION_PATH}/WVJOBDRILLBIT"
AS
SELECT * FROM ${SOURCE_DB}.WVJOBDRILLBIT T;

ALTER TABLE ${DESTINATION_DB}.WVJOBDRILLBIT SET TBLPROPERTIES('EXTERNAL'='TRUE');

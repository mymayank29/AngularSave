DROP TABLE IF EXISTS ${DESTINATION_DB}.WVJOBDRILLSTRING;
CREATE TABLE ${DESTINATION_DB}.WVJOBDRILLSTRING
STORED AS parquet
LOCATION "${LOCATION_PATH}/WVJOBDRILLSTRING"
AS
SELECT * FROM ${SOURCE_DB}.WVJOBDRILLSTRING T;

ALTER TABLE ${DESTINATION_DB}.WVJOBDRILLSTRING SET TBLPROPERTIES('EXTERNAL'='TRUE');
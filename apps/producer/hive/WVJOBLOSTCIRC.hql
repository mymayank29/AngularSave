DROP TABLE IF EXISTS ${DESTINATION_DB}.WVJOBLOSTCIRC;
CREATE TABLE ${DESTINATION_DB}.WVJOBLOSTCIRC
STORED AS parquet
LOCATION "${LOCATION_PATH}/WVJOBLOSTCIRC"
AS
SELECT * FROM ${SOURCE_DB}.WVJOBLOSTCIRC T;

ALTER TABLE ${DESTINATION_DB}.WVJOBLOSTCIRC SET TBLPROPERTIES('EXTERNAL'='TRUE');

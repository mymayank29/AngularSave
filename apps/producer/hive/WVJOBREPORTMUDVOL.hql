DROP TABLE IF EXISTS ${DESTINATION_DB}.WVJOBREPORTMUDVOL;
CREATE TABLE ${DESTINATION_DB}.WVJOBREPORTMUDVOL
STORED AS parquet
LOCATION "${LOCATION_PATH}/WVJOBREPORTMUDVOL"
AS
SELECT * FROM ${SOURCE_DB}.WVJOBREPORTMUDVOL T;

ALTER TABLE ${DESTINATION_DB}.WVJOBREPORTMUDVOL SET TBLPROPERTIES('EXTERNAL'='TRUE');

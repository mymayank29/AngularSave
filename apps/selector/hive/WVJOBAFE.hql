DROP TABLE IF EXISTS ${DESTINATION_DB}.WVJOBAFE;

CREATE TABLE ${DESTINATION_DB}.WVJOBAFE
STORED AS parquet LOCATION "${LOCATION_PATH}/WVJOBAFE"
AS
SELECT
    ja.*
FROM ${SOURCE_DB}.WVJOBAFE ja
WHERE ja.idrecparent IN (SELECT j.idrec FROM ${DESTINATION_DB}.WVJOB j) AND
    TRIM(UPPER(REGEXP_REPLACE(ja.afenumber,"-",""))) RLIKE 'U[A-Z0-9]{9}.*?'
;
ALTER TABLE  ${DESTINATION_DB}.WVJOBAFE SET TBLPROPERTIES('EXTERNAL'='TRUE');
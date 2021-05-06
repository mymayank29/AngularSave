DROP TABLE IF EXISTS ${DESTINATION_DB}.WVJOBCONTACT;

CREATE TABLE ${DESTINATION_DB}.WVJOBCONTACT
STORED AS parquet LOCATION "${LOCATION_PATH}/WVJOBCONTACT"
AS
SELECT
    j.*
FROM ${SOURCE_DB}.WVJOBCONTACT j
WHERE TRIM(UPPER(j.idwell)) IN (SELECT TRIM(UPPER(h.idwell)) FROM ${DESTINATION_DB}.WVWELLHEADER h);


ALTER TABLE ${DESTINATION_DB}.WVJOBCONTACT SET TBLPROPERTIES('EXTERNAL'='TRUE');

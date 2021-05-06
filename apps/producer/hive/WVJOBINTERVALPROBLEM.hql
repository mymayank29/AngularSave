DROP TABLE IF EXISTS ${DESTINATION_DB}.WVJOBINTERVALPROBLEM;
CREATE TABLE ${DESTINATION_DB}.WVJOBINTERVALPROBLEM
STORED AS parquet
LOCATION "${LOCATION_PATH}/WVJOBINTERVALPROBLEM"
AS
SELECT *,
        to_date(from_unixtime(cast(to_utc_timestamp(dttmstart,'CST') as bigint))) as dttmstartdate,
        to_date(from_unixtime(cast(to_utc_timestamp(dttmend,'CST') as bigint))) as dttmenddate
FROM ${SOURCE_DB}.WVJOBINTERVALPROBLEM T;

ALTER TABLE ${DESTINATION_DB}.WVJOBINTERVALPROBLEM SET TBLPROPERTIES('EXTERNAL'='TRUE');

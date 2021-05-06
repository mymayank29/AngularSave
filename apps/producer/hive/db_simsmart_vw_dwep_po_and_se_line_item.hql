DROP TABLE IF EXISTS ${DESTINATION_DB}.SIMSMART_VW_DWEP_PO_AND_SE_LINE_ITEM;
CREATE TABLE ${DESTINATION_DB}.SIMSMART_VW_DWEP_PO_AND_SE_LINE_ITEM
STORED AS parquet
LOCATION "${LOCATION_PATH}/SIMSMART_VW_DWEP_PO_AND_SE_LINE_ITEM"
AS
SELECT T.*,
end_date - start_date as days,
to_date(from_unixtime(unix_timestamp(cast(end_date as string), "yyyyMMdd"))) as end_date_formated,
to_date(from_unixtime(unix_timestamp(cast(start_date as string), "yyyyMMdd"))) as start_date_formated

FROM ${SOURCE_DB}.SIMSMART_VW_DWEP_PO_AND_SE_LINE_ITEM T;

ALTER TABLE ${DESTINATION_DB}.SIMSMART_VW_DWEP_PO_AND_SE_LINE_ITEM SET TBLPROPERTIES('EXTERNAL'='TRUE');
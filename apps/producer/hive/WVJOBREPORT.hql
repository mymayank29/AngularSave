DROP TABLE IF EXISTS ${DESTINATION_DB}.WVJOBREPORT;
CREATE TABLE ${DESTINATION_DB}.WVJOBREPORT
STORED AS parquet
LOCATION "${LOCATION_PATH}/WVJOBREPORT"
AS
SELECT idrec, idrecparent, idrecwellborecalc, dttmstart, reportnocalc, durationtimelogcumspudcalc, durationtimelogtotcumcalc,depthprogressdpcalc ,
rpttmactops, usertxt1, durationsincerptinc, durationsinceltinc, rigtime, rigtimecumcalc, tmrotatingcalc, headcountcalc, durationpersonneltotcalc,
summaryops, statusend, plannextrptops, remarks, gasbackgroundavg, gasconnectionavg, gasdrillavg, gastripavg,
to_date(from_unixtime(cast(to_utc_timestamp(dttmstart,'CST') as bigint))) as dttmstartdate
FROM ${SOURCE_DB}.WVJOBREPORT T;

ALTER TABLE ${DESTINATION_DB}.WVJOBREPORT SET TBLPROPERTIES('EXTERNAL'='TRUE');

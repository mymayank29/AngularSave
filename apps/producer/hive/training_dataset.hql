DROP TABLE IF EXISTS ${DESTINATION_DB}.training_dataset;
CREATE TABLE ${DESTINATION_DB}.training_dataset
STORED AS parquet
LOCATION "${LOCATION_PATH}/training_dataset"
AS
job_rig as (
select
jrig.idrecparent job_id,
concat_ws(", ",collect_set(jrig.rigno)) rigno,
concat_ws(", ",collect_set(jrig.contractor)) jrig_contractor
from ${SOURCE_DB}.wvjobrig jrig
group by jrig.idrecparent
),

timelog_splited as (
select *
from ${SOURCE_DB}.wvjobreporttimelog jrtl
lateral view explode(split(jrtl.refnoproblemcalc,',')) refnos as splitedrefno
)

select
npt.*,
inv.contract_title,
inv.contract_title_no_vendor,
inv.contract_id,
inv.submit_date,
inv.approved_date,
inv.inv_start_date,
inv.inv_end_date,
inv.is_date_valid,
inv.supplier_name_without_id,
inv.aribaDocId,
inv.invoice_number,
inv.code,
inv.price,
inv.supplier_id,
inv.wbs_element_id,
inv.parent_vendor,
inv.description,
tls.usertxt1,
tls.code1,
tls.com timelog_com,
tls.splitedrefno,
tls.depthstartdpcalc,
tls.depthenddpcalc,
tls.ropcalc,
tls.inclstartcalc,
tls.inclendcalc,
tls.inclmaxcalc,
tls.formationcalc,
jip.costrecov,
jip.problemsystem2,
jip.idreclastrigcalc,
jip.idreclastrigcalctk,
jip.idrecjobprogramphasecalc,
jip.idrecjobprogramphasecalctk,
j_rig.rigno,
j_rig.jrig_contractor,
j.responsiblegrp1
from ${DESTINATION_DB}.interval_problem_full_with_link_sf npt
left join ${DESTINATION_DB}.invoice inv on npt.invoice_id = inv.invoice_id
left join job_rig j_rig on npt.job_id = j_rig.job_id
left join ${SOURCE_DB}.wvjob j on npt.job_id = j.idrec
left join ${SOURCE_DB}.wvjobintervalproblem jip on split(npt.interval_problem_id,'_')[0] = jip.idrec
left join timelog_splited tls on jip.refno = tls.splitedrefno and tls.idrecjobprogramphasecalc = jip.idrecjobprogramphasecalc and
from_unixtime(cast(to_utc_timestamp(tls.dttmstartcalc,'CST') as bigint)) = from_unixtime(cast(to_utc_timestamp(jip.dttmstart,'CST') as bigint))
where npt.bytitleimprpercents = 75 OR npt.bytitleimprpercents = 100;

ALTER TABLE ${DESTINATION_DB}.training_dataset SET TBLPROPERTIES('EXTERNAL'='TRUE');
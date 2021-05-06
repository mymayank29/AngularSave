IF OBJECT_ID("gomica.afe_detail", "U") IS NOT NULL DROP TABLE gomica.afe_detail;
CREATE TABLE gomica.afe_detail(
    afe varchar(max));
	
IF OBJECT_ID("gomica.amendments_to_contracts", "U") IS NOT NULL DROP TABLE gomica.amendments_to_contracts;
CREATE TABLE gomica.amendments_to_contracts(
    amendment_contract_id varchar(max)
	,contract_hierarchy varchar(max)
	,contract_id varchar(max)
	,contract_status varchar(max)
	,evergreen_y_n varchar(max)
	,intercompany_y_n varchar(max)
	,payment_terms varchar(max)
	,transactable_y_n varchar(max));
	
IF OBJECT_ID("gomica.interval_problem", "U") IS NOT NULL DROP TABLE gomica.interval_problem;
CREATE TABLE gomica.interval_problem(
    interval_problem_id varchar(450)
        , afenumber varchar(max)
        , servicetyp varchar(max)
        , rigno varchar(max)
        , idwell varchar(max)
        , idrecntervalProblem varchar(max)
        , job_id varchar(max)
        , npt_date_start varchar(max)
        , npt_date_end varchar(max)
        , parent_name varchar(max)
        , cost float
        , estcostoverride float
        , status varchar(max)
        , refno varchar(max)
        , duration float
        , typ varchar(max)
        , typdetail varchar(max)
        , com varchar(max)
        , internalProblemType varchar(max)
        , gl_code_str varchar(max)
        , costgen_des varchar(max)
        , activitydescr varchar(max)
        , npt_type_detail_description varchar(max)
        , npt_type_typedetail_concat varchar(max)
        , npt_event_no varchar(max)
        , isWether bit
);

	
IF OBJECT_ID("gomica.interval_problem_full_sf", "U") IS NOT NULL DROP TABLE gomica.interval_problem_full_sf;
CREATE TABLE gomica.interval_problem_full_sf(
    activitydescr varchar(max)
	,afe varchar(max)
	,com varchar(max)
	,costgen_des varchar(max)
	,duration float
	,gl_code_str varchar(max)
	,internalProblemType varchar(max)
	,job_end_date varchar(max)
	,job_id varchar(max)
    ,npt_date_end varchar(max)
	,npt_date_start varchar(max)
	,npt_type_detail_description varchar(max)
	,typ varchar(max)
	,typdetail varchar(max)
	,wv_job_start_date varchar(max));
	
IF OBJECT_ID("gomica.interval_problem_full_with_link_sf", "U") IS NOT NULL DROP TABLE gomica.interval_problem_full_with_link_sf;
CREATE TABLE gomica.interval_problem_full_with_link_sf(
    id varchar(12)
    	,ariba_doc_id varchar(max)
    	,job_id varchar(max)
    	,com varchar(max)
    	,typ varchar(max)
    	,npt_event_no varchar(max)
    	,npt_date_end varchar(max)
    	,npt_date_start varchar(max)
    	,duration float
    	,npt_type_detail_description varchar(max)
	CONSTRAINT interval_problem_full_pk PRIMARY KEY (id));

IF OBJECT_ID("gomica.invoice", "U") IS NOT NULL DROP TABLE gomica.invoice;
CREATE TABLE gomica.invoice(
    ariba_doc_id varchar(max)
	,contract_id varchar(max)
	,contract_title varchar(max)
	,contract_title_no_vendor varchar(max)
	,inv_end_date varchar(max)
	,inv_start_date varchar(max)
	,invoice_number varchar(max)
	,is_date_valid bit
	,supplier_id varchar(max)
	,supplier_name_without_id varchar(max));

IF (OBJECT_ID("gomica.FK_InvoiceId", "F") IS NOT NULL)
BEGIN
    ALTER TABLE gomica.writeback_invoice_spent_leakage DROP CONSTRAINT FK_InvoiceId
END


IF OBJECT_ID("gomica.invoice_for_full_sf", "U") IS NOT NULL DROP TABLE gomica.invoice_for_full_sf;
CREATE TABLE gomica.invoice_for_full_sf(
    afe varchar(max)
    ,wbs_element varchar(max)
    , supplier_name_without nvarchar(2000)
    , contract_owner_name nvarchar(1000)
	,ariba_doc_id varchar(200)
	,id varchar(12)
	,isWether bit
	,contract_id nvarchar(500)
	,jobtyp varchar(500)
	,npt_event_no varchar(500)
	,npt_date_start varchar(100)
	,npt_date_end varchar(100)
	,npt_duration float
	,parent_vendor varchar(500)
	,price float
	,pt_spent_leakage_npt float
	,submit_date varchar(100)
	,wellname nvarchar(200)
	,bydate bit
	,bytitle bit
	,byglcode varchar(50)
	,byglcodepercents int
    ,rigno nvarchar(500)
	,refno varchar(100)
	,servicetyp varchar(2000)
	,npt_type_detail_description nvarchar(5000)
    ,npt_type_typedetail_concat varchar(2000)
    ,npt_com nvarchar(5000)
    ,contract_title nvarchar(1000)
    ,inv_start_date varchar(100)
    ,inv_end_date varchar(100)
    ,approved_date datetime    
    --,INDEX idx_invoice_full NONCLUSTERED (bydate, isWether, bytitle, id)
    ,INDEX idx_invoice_full NONCLUSTERED (bydate, isWether, id)
	CONSTRAINT invoice_full_pk PRIMARY KEY (id));

IF OBJECT_ID("gomica.cia_invoice_to_npt_link_detailed", "U") IS NOT NULL DROP TABLE gomica.cia_invoice_to_npt_link_detailed;
CREATE TABLE gomica.cia_invoice_to_npt_link_detailed(
        ariba_doc_id varchar(max)
        , isWether bit
        , interval_problem_id bigint
        , npt_sub_group_duration float
--        , cost_gen_id varchar(max)
        , byCode bit
        , byDate bit
        , byTitle bit
        , byGlCode varchar(max)
        , byGlCodePercents bigint
        , byTitleImpr varchar(max)
        , byTitleImprPercents bigint
        , byInvoiceDescription varchar(max)
        , isExceptionalMatchCase bit
        , isEmptyDate bit
        , id varchar(12)
        , afenumber varchar(max)
        , servicetyp varchar(max)
        , rigno varchar(max)
        , idwell varchar(max)
        , idrecntervalProblem varchar(max)
        , job_id varchar(max)
        , npt_date_start varchar(max)
        , npt_date_end varchar(max)
        , parent_name varchar(max)
        , cost float
        , estcostoverride float
        , status varchar(max)
        , refno varchar(max)
        , group_refno varchar(max)
        , duration float
        , typ varchar(max)
        , typdetail varchar(max)
        , com varchar(max)
        , internalProblemType varchar(max)
        , gl_code_str varchar(max)
        , costgen_des varchar(max)
        , activitydescr varchar(max)
        , npt_type_detail_description varchar(max)
        , npt_type_typedetail_concat varchar(max)
        , npt_event_no varchar(max)
        , is_date_valid bit
        , contract_title varchar(max)
        , contract_id varchar(max)
        , inv_start_date varchar(max)
        , inv_end_date varchar(max)
        , submit_date varchar(max)
        , approved_date datetime
        , supplier_name_without_id varchar(max)
        , invoice_number varchar(max)
        , supplier_id varchar(max)
        , wbs_element_id varchar(max)
        , supplier_name_without varchar(max)
        , contract_owner_name varchar(max)
        , wbs_element varchar(max)
        , parent_vendor varchar(max)
        , price float
        , commodity_ids varchar(max)
        , description varchar(max)
        , contract_title_no_vendor varchar(max)
        , npt_duration_date_related float
        , duration_total bigint
        , cost_per_hour float
        , pt_spent_leakage_npt float
        , pt_spent_leakage_total float
        , pt_spent_leakage_date_related float
        , afe varchar(max)
CONSTRAINT cia_invoice_to_npt_link_detailed_pk PRIMARY KEY (id));


IF OBJECT_ID("gomica.writeback_invoice_spent_leakage", "U") IS NULL
CREATE TABLE gomica.writeback_invoice_spent_leakage(
	id varchar(12)
	,invoice_status varchar(max)
	,npt_event_no varchar(max)
	,comment varchar(max)
	,modifyed_by varchar(max)
	,modifyed_date datetime
	,spent_leakage_confirmed float
	,recovered float
	CONSTRAINT invoice_writeback_pk PRIMARY KEY (id));

IF (OBJECT_ID("gomica.FK_InvoiceId", "F") IS NULL)
BEGIN
    ALTER TABLE gomica.writeback_invoice_spent_leakage WITH NOCHECK
    ADD CONSTRAINT FK_InvoiceId FOREIGN KEY (id)
    REFERENCES gomica.invoice_for_full_sf(id)
END

IF OBJECT_ID("gomica.invoice_to_npt_link", "U") IS NOT NULL DROP TABLE gomica.invoice_to_npt_link;
CREATE TABLE gomica.invoice_to_npt_link(
    id varchar(12)
                , ariba_doc_id varchar(max)
        , interval_problem_id bigint
        , byDate bit
        , byTitle bit
        , byGlCode varchar(max)
        , byGlCodePercents bigint
        , byTitleImpr varchar(max)
        , byTitleImprPercents bigint
        , byInvoiceDescription varchar(max)
        , isEmptyDate bit
        , isExceptionalMatchCase bit
        , byCode bit
CONSTRAINT invoice_to_npt_link_pk PRIMARY KEY (id));

	
IF OBJECT_ID("gomica.overcharge_detail", "U") IS NOT NULL DROP TABLE gomica.overcharge_detail;
CREATE TABLE gomica.overcharge_detail(
        contract_id varchar(20)
        ,line_item_desc varchar(max)
        ,commodity_id_set varchar(max)
        ,mat_item_number_set varchar(max)
        ,num_mult_line_items bigint
        ,number_unique bigint
        ,uom_llf_distinct_count bigint
        ,sum_cost_total float
        ,mean_unit_price float
        ,min_unit_price float
        ,max_unit_price float
        ,n bigint
        ,commondity_id varchar(max)
        ,supplier_id bit
        ,material_item_number varchar(max)
        ,line_item_desc_length int
        ,line_item_desc_misclabor bit
        ,curskew int
        ,range_unit_price float
        ,weighted_sd_unit_price float
        ,skew_cumulative_total float
);
	
IF OBJECT_ID("gomica.pdf_contract_info", "U") IS NOT NULL DROP TABLE gomica.pdf_contract_info;
CREATE TABLE gomica.pdf_contract_info(
    contains_amendment bit
	,contract_id varchar(max));
	
IF OBJECT_ID("gomica.simsmart_vw_dwep_po_and_se_line_item", "U") IS NOT NULL DROP TABLE gomica.simsmart_vw_dwep_po_and_se_line_item;
CREATE TABLE gomica.simsmart_vw_dwep_po_and_se_line_item(
    business_key varchar(450)
    	,ariba_doc_id varchar(100)
        ,contract_name varchar(max)
    	,part_number varchar(max)
    	,description varchar(max)
    	,unit_price_llf varchar(max)
    	,quantity_llf varchar(max)
    	,discount varchar(max)
    	,invoice_net_amount_usd varchar(max)
    	,end_date bigint
    	,start_date bigint
	CONSTRAINT simsmart_line_item_pk PRIMARY KEY (business_key));
	CREATE INDEX simsmart_vw_dwep_po_and_se_line_item_ariba_doc_i ON [gomica].simsmart_vw_dwep_po_and_se_line_item (ariba_doc_id);



IF OBJECT_ID("gomica.time_log_detail", "U") IS NOT NULL DROP TABLE gomica.time_log_detail;
CREATE TABLE gomica.time_log_detail(
    activitycode varchar(max)
	,duration varchar(max)
	,end_date varchar(max)
	,is_npt bit);

IF OBJECT_ID("gomica.wvjobreport", "U") IS NOT NULL DROP TABLE gomica.wvjobreport;
CREATE TABLE gomica.wvjobreport(
    idrec varchar(450)
	, idrecparent varchar(max)
	, durationpersonneltotcalc varchar(max)
	, durationsinceltinc varchar(max)
	, durationsincerptinc varchar(max)
	, gasbackgroundavg varchar(max)
	, gasconnectionavg varchar(max)
	, gasdrillavg varchar(max)
	, gastripavg varchar(max)
	, headcountcalc varchar(max)
	, idrecwellborecalc varchar(max)
	, plannextrptops varchar(max)
	, remarks varchar(max)
	, rigtime varchar(max)
	, rigtimecumcalc varchar(max)
	, rpttmactops varchar(max)
	, statusend varchar(max)
	, summaryops varchar(max)
	, tmrotatingcalc varchar(max)
	, usertxt1 varchar(max)
	, depthprogressdpcalc varchar(max)
	, dttmstart bigint
	, durationtimelogcumspudcalc varchar(max)
	, durationtimelogtotcumcalc varchar(max)
	, reportnocalc varchar(max)
	, dttmstartdate datetime
CONSTRAINT wvjobreport_pk PRIMARY KEY (idrec));

IF OBJECT_ID("gomica.wvwellbore", "U") IS NOT NULL DROP TABLE gomica.wvwellbore;
CREATE TABLE gomica.wvwellbore(
    idrec varchar(450)
                , des varchar(max)
        , idrecparent varchar(max)
        , profiletyp varchar(max)
        , wellboreida varchar(max)
        , wellboreidb varchar(max)
CONSTRAINT wvwellbore_pk PRIMARY KEY (idrec));


IF OBJECT_ID("gomica.wvjob", "U") IS NOT NULL DROP TABLE gomica.wvjob;
CREATE TABLE gomica.wvjob(
    idrec varchar(450)
                , idwell varchar(max)
        , dttmend bigint
        , dttmstart bigint
        , jobtyp varchar(max)
        , wvtyp varchar(max)
CONSTRAINT wvjob_pk PRIMARY KEY (idrec));


IF OBJECT_ID("gomica.wvjobsafetychk", "U") IS NOT NULL DROP TABLE gomica.wvjobsafetychk;
CREATE TABLE gomica.wvjobsafetychk(
    idrec varchar(450)
                , idrecparent varchar(max)
        , com varchar(max)
        , des varchar(max)
        , inspector varchar(max)
        , result varchar(max)
        , typ varchar(max)
        , dttmdate datetime
CONSTRAINT wvjobsafetychk_pk PRIMARY KEY (idrec));


IF OBJECT_ID("gomica.wvwellheader", "U") IS NOT NULL DROP TABLE gomica.wvwellheader;
CREATE TABLE gomica.wvwellheader(
    idwell varchar(250)
	, division varchar(max)
	, elvorigkb varchar(max)
	, fieldname varchar(max)
	, kbtocascalc varchar(max)
	, kbtomudcalc varchar(max)
	, kbtotubcalc varchar(max)
	, lease varchar(max)
	, stateprov varchar(max)
	, waterdepth varchar(max)
	, wellida varchar(max)
	, wellidc varchar(max)
	, wellidd varchar(max)
	, wellname varchar(max)
CONSTRAINT wvwellheader_pk PRIMARY KEY (idwell));

IF OBJECT_ID("gomica.wvjobreporttimelog", "U") IS NOT NULL DROP TABLE gomica.wvjobreporttimelog;
CREATE TABLE gomica.wvjobreporttimelog(
    idrec varchar(450)
	, idrecparent varchar(max)
	, code1 varchar(max)
	, com varchar(max)
	, phase varchar(max)
	, dttmstartcalc bigint
	, durationnoprobtimecalc varchar(max)
	, durationproblemtimecalc varchar(max)
	, refnoproblemcalc varchar(max)
CONSTRAINT wvjobreporttimelog_pk PRIMARY KEY (idrec));

IF OBJECT_ID("gomica.wvjobintervalproblem", "U") IS NOT NULL DROP TABLE gomica.wvjobintervalproblem;
CREATE TABLE gomica.wvjobintervalproblem(
    idrec varchar(450)
        , idrecparent varchar(max)
        , accountablepty varchar(max)
        , com varchar(max)
        , depthend varchar(max)
        , depthstart varchar(max)
        , des varchar(max)
        , durationnetcalc varchar(max)
        , estcostoverride varchar(max)
        , refno varchar(max)
        , status varchar(max)
        , typ varchar(max)
        , typdetail varchar(max)
        , dttmstartdate datetime
        , dttmenddate datetime
CONSTRAINT wvjobintervalproblem_pk PRIMARY KEY (idrec));

IF OBJECT_ID("gomica.jobreport_npt_related", "V") IS NOT NULL DROP VIEW gomica.jobreport_npt_related;
create view gomica.jobreport_npt_related as
select concat(jr.idrec,l.id, cast(l.id as varchar)) as id,jr.idrec,l.id as link_id
from gomica.wvjobreport jr
inner join gomica.interval_problem_full_with_link_sf l on l.job_id=jr.idrecparent
where
jr.dttmstartdate between cast(l.npt_date_start as date) and cast(l.npt_date_end as date);

IF OBJECT_ID("gomica.invoice_for_full_sf_view", "V") IS NOT NULL DROP VIEW gomica.invoice_for_full_sf_view;





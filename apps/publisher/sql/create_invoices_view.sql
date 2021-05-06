CREATE VIEW gomica.invoice_for_full_sf_view
	AS
	SELECT 
	i.afe  AS afe
    ,i.wbs_element AS wbs_element
    ,i.supplier_name_without AS supplier_name_without
    ,i.contract_owner_name AS contract_owner_name
	,i.ariba_doc_id AS ariba_doc_id
	,i.id AS id
	,i.isWether AS isWether
	,i.contract_id AS contract_id
	,i.jobtyp AS jobtyp
	,i.npt_event_no AS npt_event_no
	,i.npt_date_start AS npt_date_start
	,i.npt_date_end AS npt_date_end
	,i.npt_duration AS npt_duration
	,i.parent_vendor AS parent_vendor
	,i.price AS price
	,i.pt_spent_leakage_npt AS pt_spent_leakage_npt
	,i.submit_date AS submit_date
	,i.wellname AS wellname
	,i.bydate AS bydate
	,i.bytitle AS bytitle
	,i.byglcode AS byglcode
	,i.byglcodepercents AS byglcodepercents
    ,i.rigno AS rigno
	,i.refno AS refno
	,i.servicetyp AS servicetyp
	,i.npt_type_detail_description AS npt_type_detail_description
    ,i.npt_type_typedetail_concat AS npt_type_typedetail_concat
    ,i.npt_com AS npt_com
    ,i.contract_title AS contract_title
    ,i.inv_start_date AS inv_start_date
    ,i.inv_end_date AS inv_end_date
    ,i.approved_date AS approved_date
	--writeback columns
    ,w.invoice_status AS invoice_status
	--,w.npt_event_no AS npt_event_no
	,w.comment AS comment
	,w.modifyed_by AS modifyed_by
	,w.modifyed_date AS modifyed_date
	,w.spent_leakage_confirmed AS spent_leakage_confirmed
	,w.recovered AS recovered
	FROM gomica.invoice_for_full_sf i
	LEFT JOIN gomica.writeback_invoice_spent_leakage w ON i.id = w.id;
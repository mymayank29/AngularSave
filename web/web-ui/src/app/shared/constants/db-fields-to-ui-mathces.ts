export const ROW_FIELDS_DB_TO_UI_MATCH = {
    contract_id: 'Contract ID',
    contract_owner_name: 'Contract Owner',
    ariba_doc_id: 'Ariba Doc ID',
    id: 'ID',
    wellname: 'Well',
    afe: 'WBS #',
    wbs_element: 'WBS#',
    jobtyp: 'Job Type',
    npt_event_no: 'NPT',
    parent_vendor: 'Supplier',
    submit_date: 'Submit Date',
    price: 'Invoice Total',
    status: 'Status',
    pt_spent_leakage_npt: 'Leakage Identified',
    // spent_leakage_confirmed: 'Leakage Confirmed',
    recovered: 'Leakage Recovered',
    npt_date_start: 'NPT Start',
    npt_date_end: 'NPT End',
    npt_ref_no: 'NPT #',
    npt_duration: 'NPT (Hr)',
    comment: 'Comments',
    modified_by: 'Modified By',
    modified_date: 'Modified Date',
    work_start_date: 'Invoice Start',
    work_end_date: 'Invoice End',
    by_date: 'By Date',
    is_weather: 'By Weather',
    contract_title: 'Contract Title'
};

export const SEARCHING_COLUMNS = [
  'ariba_doc_id',
  'id',
  'contract_id',
  'wellname',
  'contract_owner_name',
  'wbs_element',
  'jobtyp',
  'parent_vendor',
  'submit_date',
  'price',
  'status',
  'npt_event_no',
  'npt_ref_no',
  'pt_spent_leakage_npt',
//   'spent_leakage_confirmed',
  'recovered',
  'npt_date_start',
  'npt_date_end',
  'npt_duration',
  'comment',
  'modified_by',
  'work_start_date',
  'work_end_date',
  'contract_title'
];

export const FILTERING_COLUMNS = [];

export const NPT_FIELDS_DB_TO_UI_MATCH = {
    ref_no: 'NPT#',
    typ: 'NPT Type',
    npt_date_start: 'Start',
    npt_date_end: 'End',
    duration: 'Duration (Hr)',
    npt_type_detail_description: 'Related Restriction',
    com: 'Description',
    invoice_no: 'invoice#'
};

export const LINE_ITEMS_FIELDS_DB_TO_UI_MATCH = {
    part_number: 'Part#',
    description: 'Description',
    unit_price_llf: 'Unit Price',
    quantity_llf: 'QTY',
    discount: 'Discount',
    invoice_net_amount_usd: 'Net Price',
    start_date: 'Work Start',
    end_date: 'Work Stop',
    duration: 'Duration',
    invoice_no: 'invoice#' 
};

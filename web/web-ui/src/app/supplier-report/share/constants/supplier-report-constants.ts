export const FLAGGED_INVOICES_TO_UI = {
  wellname: 'Well/Job',
  contract_owner_name: 'Category Manager',
  parent_vendor: 'Supplier',
  id: 'ID',
  npt_ref_no: 'NPT RefNo',
  servicetyp: 'NPT Activity',
  npt_desc: 'NPT Description',
  pt_spent_leakage_npt: 'Sum of Estimated NPT Spend',
  submit_date: 'date',
  
  modified_by: 'modifiedBy',
  days_since_npt: 'Days Since NPT',
  rig_no: 'Rig Number',
  npt_duration: 'NPT Duration'
};

export const SR_COLUMNS: string[] = [
  'select',
  'contract_owner_name',
  'parent_vendor',
  'wellname',
  // 'id',
  'npt_ref_no',
  'servicetyp',
  'npt_desc',
  'rig_no',
  'npt_duration',
  //'days_since_npt',
  'pt_spent_leakage_npt',
  'modified_by'
];

export const SR_TO_MARGING_CELL: string[] = [
  'wellname',
  'parent_vendor',
  'npt_ref_no'
];

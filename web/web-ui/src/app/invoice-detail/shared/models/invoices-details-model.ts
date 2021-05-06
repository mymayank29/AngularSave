export interface InvoiceDetailsItem {
  id?: string;
  contract_id: string;
  ariba_doc_id: string;
  wellname: string;
  afe: string;
  jobtyp?: string;
  npt_event_no: string;
  parent_vendor: string;
  submit_date: string;
  price: number;
  status: string;
  npt_date_start?: string;
  npt_date_end?: string;
  npt_duration?: number;
  npt_ref_no?: number;
  pt_spent_leakage_npt: number;
  spent_leakage_confirmed?: number;
  recovered: number;
  comment?: string;
  modifiedBy?: string;
}

import { Action } from '@ngrx/store';
import { InvoiceItem } from '../../shared/models/invoice-item-model';


export enum InvoicesDetailsActionTypes {
  LOAD_INVOICES_DETAILS = '[InvoicesDetails] LOAD_INVOICES_DETAILS',
  SET_INVOICES_DETAILS = '[InvoicesDetails] SET_INVOICES_DETAILS'
}
export class LoadInvoicesDetails implements Action {
  readonly type = InvoicesDetailsActionTypes.LOAD_INVOICES_DETAILS;
}

export class SetInvoicesDetails implements Action {
  readonly type = InvoicesDetailsActionTypes.SET_INVOICES_DETAILS;
  constructor(public payload: InvoiceItem[]) {}
}

export type InvoicesDetailsActions =
  | LoadInvoicesDetails
  | SetInvoicesDetails;

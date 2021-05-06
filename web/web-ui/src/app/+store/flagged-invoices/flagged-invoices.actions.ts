import { Action } from '@ngrx/store';
import {FlaggedInvoiceItem} from '../../shared/models/flagged-invoice-item';


export enum FlaggedInvoicesActionTypes {
  GET_FLAGGED_INVOICES = '[FlaggedInvoices] GET_FLAGGED_INVOICES',
  GET_FLAGGED_INVOICES_SUCCESS = '[FlaggedInvoices] GET_FLAGGED_INVOICES_SUCCESS',
  GET_FLAGGED_INVOICES_ERROR = '[FlaggedInvoices] GET_FLAGGED_INVOICES_ERROR'
}

export class GetFlaggedInvoices implements Action {
  readonly type = FlaggedInvoicesActionTypes.GET_FLAGGED_INVOICES;
  constructor(public payload: any[]) {}
}

export class GetFlaggedInvoicesSuccess implements Action {
  readonly type = FlaggedInvoicesActionTypes.GET_FLAGGED_INVOICES_SUCCESS;
  constructor(public payload: FlaggedInvoiceItem[]) {}
}

export class GetFlaggedInvoicesError implements Action {
  readonly type = FlaggedInvoicesActionTypes.GET_FLAGGED_INVOICES_ERROR;
  constructor(public payload: Error | string) {}
}

// reducer will accept action of type InvoicesAction
export type FlaggedInvoicesActions =
  | GetFlaggedInvoices
  | GetFlaggedInvoicesSuccess
  | GetFlaggedInvoicesError;

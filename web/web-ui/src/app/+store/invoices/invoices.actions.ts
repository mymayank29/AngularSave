import { Action } from '@ngrx/store';
import { InvoiceItem } from '../../shared/models/invoice-item-model';

export enum InvoicesActionTypes {
  GET_INVOICES = '[Invoices] GET_INVOICES',
  GET_INVOICES_SUCCESS = '[Invoices] GET_INVOICES_SUCCESS',
  GET_INVOICES_ERROR = '[Invoices] GET_INVOICES_ERROR',
  UPDATE_INVOICE = '[Invoices] UPDATE_INVOICE',
  UPDATE_INVOICE_SUCCESS = '[Invoices] UPDATE_INVOICE_SUCCESS',
  UPDATE_INVOICE_ERROR = '[Invoices] UPDATE_INVOICE_ERROR',
}

export class GetInvoices implements Action {
  readonly type = InvoicesActionTypes.GET_INVOICES;
  constructor(public payload: any[]) {}
}

export class GetInvoicesSuccess implements Action {
  readonly type = InvoicesActionTypes.GET_INVOICES_SUCCESS;
  constructor(public payload: InvoiceItem[]) {}
}

export class GetInvoicesError implements Action {
  readonly type = InvoicesActionTypes.GET_INVOICES_ERROR;
  constructor(public payload: Error | string) {}
}

export class UpdateInvoice implements Action {
  readonly type = InvoicesActionTypes.UPDATE_INVOICE;
  constructor(public payload: InvoiceItem[]) {}
}

export class UpdateInvoiceSuccess implements Action {
  readonly type = InvoicesActionTypes.UPDATE_INVOICE_SUCCESS;
  constructor(public payload: any) {}
}

export class UpdateInvoiceError implements Action {
  readonly type = InvoicesActionTypes.UPDATE_INVOICE_ERROR;
  constructor(public payload: Error | string) {}
}

// reducer will accept action of type InvoicesAction
export type InvoicesActions =
  | GetInvoices
  | GetInvoicesSuccess
  | GetInvoicesError
  | UpdateInvoice
  | UpdateInvoiceSuccess
  | UpdateInvoiceError;

import {FlaggedInvoiceItem} from '../../shared/models/flagged-invoice-item';

export interface FlaggedInvoicesState {
  data: ReadonlyArray<FlaggedInvoiceItem>;
  readonly loading: boolean;
  readonly loaded: boolean;
  readonly error: Error | string;
}

export const initialFlaggedInvoicesState: FlaggedInvoicesState = {
  data: [],
  loading: false,
  loaded: false,
  error: null
};

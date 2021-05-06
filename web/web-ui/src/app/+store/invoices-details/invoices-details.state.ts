import { InvoiceItem } from '../../shared/models/invoice-item-model';

export interface InvoicesDetailsState {
    data: ReadonlyArray<InvoiceItem>;
    readonly loading: boolean;
    readonly loaded: boolean;
    readonly error: Error | string;
}

export const initialInvoicesDetailsState: InvoicesDetailsState = {
    data: [],
    loading: false,
    loaded: false,
    error: null
};

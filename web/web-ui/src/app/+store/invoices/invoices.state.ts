import { InvoiceItem } from '../../shared/models/invoice-item-model';

export interface InvoicesState {
    data: ReadonlyArray<InvoiceItem>;
    readonly loading: boolean;
    readonly loaded: boolean;
    readonly error: Error | string;
    updatedData: Array<InvoiceItem>
}

export const initialInvoicesState: InvoicesState = {
    data: [],
    loading: false,
    loaded: false,
    error: null,
    updatedData: []
};

import { InvoicesState } from './invoices/invoices.state';
import { FiltersState } from './filters/filters.state';
import { FlaggedInvoicesState } from './flagged-invoices/flagged-invoices.state';
import { InvoicesDetailsState } from './invoices-details/invoices-details.state';
import { TogglesState } from '../shared/models/toggles-interface';

export interface AppState {
    invoices: InvoicesState;
    invoicesDetails: InvoicesDetailsState;
    filters: FiltersState;
    toggles: TogglesState;
    flaggedInvoices: FlaggedInvoicesState;
}

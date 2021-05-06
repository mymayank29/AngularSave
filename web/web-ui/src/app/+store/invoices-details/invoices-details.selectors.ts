import { createFeatureSelector, createSelector } from '@ngrx/store';
import { InvoicesDetailsState } from './invoices-details.state';

// return state
// якщо у селесктора не змінюються параметри, то вони не перевиконуються,
// а повертають ті ж значення
export const getInvoicesDetailsState = createFeatureSelector<InvoicesDetailsState>('invoicesDetails');

export const getInvoicesDetailsData = createSelector(
    getInvoicesDetailsState,
    (state: InvoicesDetailsState) => state.data
);

export const getInvoicesDetailsError = createSelector(
    getInvoicesDetailsState,
    (state: InvoicesDetailsState) => state.error
);

export const getInvoicesDetailsLoading = createSelector(
    getInvoicesDetailsState,
    (state: InvoicesDetailsState) => state.loading
);

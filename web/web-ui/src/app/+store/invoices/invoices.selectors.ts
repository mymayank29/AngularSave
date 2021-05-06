import { createFeatureSelector, createSelector } from '@ngrx/store';

import { InvoicesState } from './invoices.state';

// return state
// якщо у селесктора не змінюються параметри, то вони не перевиконуються,
// а повертають ті ж значення
export const getInvoicesState = createFeatureSelector<InvoicesState>('invoices');

export const getInvoicesData = createSelector(
    getInvoicesState,
    (state: InvoicesState) => state.data
);

export const getInvoicesError = createSelector(
    getInvoicesState,
    (state: InvoicesState) => state.error
);

export const getInvoicesLoading = createSelector(
    getInvoicesState,
    (state: InvoicesState) => state.loading
);

export const getUpdatedInvoices = createSelector(
    getInvoicesState,
    (state: InvoicesState) => state.updatedData
);

import { createFeatureSelector, createSelector } from '@ngrx/store';
import { FlaggedInvoicesState } from './flagged-invoices.state';

export const getFlaggedInvoicesState = createFeatureSelector<FlaggedInvoicesState>('flaggedInvoices');

export const getFlaggedInvoicesData = createSelector(
  getFlaggedInvoicesState,
  (state: FlaggedInvoicesState) => state.data
);

export const getFlaggedInvoicesError = createSelector(
  getFlaggedInvoicesState,
  (state: FlaggedInvoicesState) => state.error
);

export const getFlaggedInvoicesLoading = createSelector(
  getFlaggedInvoicesState,
  (state: FlaggedInvoicesState) => state.loading
);

export const getFlaggedInvoicesLoaded = createSelector(
  getFlaggedInvoicesState,
  (state: FlaggedInvoicesState) => state.loaded
);

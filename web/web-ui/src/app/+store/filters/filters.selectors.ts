import { createFeatureSelector, createSelector } from '@ngrx/store';

import { FiltersState } from './filters.state';
import { FilterState } from '../../shared/models/filter-state-interface';

export const getFiltersState = createFeatureSelector<FiltersState>('filters');

export const getFiltersData = createSelector(
    getFiltersState,
    (state: FiltersState) => state.data
);

export const getFiltersSelectedFilter = createSelector(
    getFiltersState,
    (state: FiltersState) => state.lastSelectedFilter
);



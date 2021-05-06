import { Action } from '@ngrx/store';
import { Filters } from '../../shared/models/filter-interface';

export enum FiltersActionTypes {
  LOAD_FILTERS = '[Filters] LOAD_FILTERS',
  SET_FILTERS_DATA = '[Filters] SET_FILTERS_DATA',
  UPDATE_FILTERS = '[Filters] UPDATE_FILTERS',
  GET_FILTER_VALUE = '[Filters] GET_FILTER_VALUE',
  SET_EMPTY_FILTERS_STATE = '[Filters] SET_EMPTY_FILTERS_STATE'
}

export class LoadFilters implements Action {
  readonly type = FiltersActionTypes.LOAD_FILTERS;
}

export class SetFiltersData implements Action {
  readonly type = FiltersActionTypes.SET_FILTERS_DATA;
  constructor(public payload: Filters) {}
}

export class UpdateFilters implements Action {
  readonly type = FiltersActionTypes.UPDATE_FILTERS;
  constructor(public payload: Filters) {}
}

export class GetFilterValue implements Action {
  readonly type = FiltersActionTypes.GET_FILTER_VALUE;
  constructor(public payload: string) {}
}

export class SetEmptyFiltersState implements Action {
  readonly type = FiltersActionTypes.SET_EMPTY_FILTERS_STATE;
  constructor(public payload?: string) {}
}

// reducer will accept action of type FiltersAction
export type FiltersActions =
  | LoadFilters
  | SetFiltersData
  | UpdateFilters
  | GetFilterValue
  | SetEmptyFiltersState;

import { FilterState } from './../../shared/models/filter-state-interface';
import { FiltersActions, FiltersActionTypes } from './filters.actions';
import { emptyFiltersState, FiltersState, initialFiltersState } from './filters.state';
import { Filters } from '../../shared/models/filter-interface';

export function filtersReducer(state = initialFiltersState, action: FiltersActions): FiltersState {
    switch (action.type) {
      case FiltersActionTypes.LOAD_FILTERS: {
        return {
          ...state
         };
      }
      case FiltersActionTypes.SET_FILTERS_DATA: {
        const data = {...(<Filters>action.payload) };
        return {
          ...state,
          data
         };
      }
      case FiltersActionTypes.UPDATE_FILTERS: {
        const data = {...state.data, ...(<Filters>action.payload) };
        const filterName = Object.keys(action.payload)[0];
        const filtersState = {...state.filtersState, ...{[filterName]: action.payload[filterName][0]} };
        const previousSelectedFilter = state.lastSelectedFilter;
         return {
          data,
          lastSelectedFilter: filterName,
          filtersState,
          previousSelectedFilter
         };
      }
      case FiltersActionTypes.SET_EMPTY_FILTERS_STATE: {
        let filterState = emptyFiltersState;
        const filterName = action.payload;
        if (action.payload) {
          if (action.payload !== 'date') {
          filterState = {...state.filtersState, ...{[filterName]: ''}};
        } else {
          filterState = {...state.filtersState, ...{['submit_date']: '', ['date_second']: ''}};
        }
      }
        return {
          ...state,
          lastSelectedFilter: null,
          filtersState: filterState
         };
      }
      default:
        return state;
    }
}

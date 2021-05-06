import { Filters } from '../../shared/models/filter-interface';
import { FilterState } from '../../shared/models/filter-state-interface';

export interface FiltersState {
    data: Readonly<Filters>;
    previousSelectedFilter: string;
    lastSelectedFilter: string;
    filtersState: {};
}

const emptyFilters: Filters = {
    contract_owner_name: [],
    wellname: [],
    parent_vendor: [],
    submit_date: [],
    date_second: [],
    status: []
};

export const emptyFiltersState: FilterState = {
    contract_owner_name: '',
    wellname: '',
    parent_vendor: '',
    submit_date: '',
    date_second: '',
    status: '',
    searching: ''
};

export const initialFiltersState: FiltersState = {
    data: emptyFilters,
    previousSelectedFilter: null,
    lastSelectedFilter: null,
    filtersState: emptyFiltersState
};

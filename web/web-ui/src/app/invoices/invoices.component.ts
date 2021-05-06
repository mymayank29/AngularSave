import { FilterState } from './../shared/models/filter-state-interface';
import { Component, OnInit, OnDestroy, ViewChild, AfterViewInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material';
import { InvoiceItem } from '../shared/models/invoice-item-model';
import { InvoiceTableComponent} from './invoice-table/invoice-table.component';
import { FiltersComponent } from '../shared/components/filters/filters.component';

import { Subscription } from 'rxjs';

import { Store, select } from '@ngrx/store';
import { AppState, getInvoicesData, getInvoicesLoading, getFiltersState, getUpdatedInvoices, FiltersState } from '../+store';
import * as InvoicesActions from '../+store/invoices/invoices.actions';
import * as FiltersActions from '../+store/filters/filters.actions';
import * as TogglesActions from '../+store/toggles/toggles.actions';
import { getTogglesState } from '../+store/toggles/toggles.selectors';
import { TogglesState } from '../shared/models/toggles-interface';
import { isNullOrUndefined } from 'util';

@Component({
  selector: 'app-invoices',
  templateUrl: './invoices.component.html',
  styleUrls: ['./invoices.component.css']
})
export class InvoicesComponent implements OnInit, OnDestroy, AfterViewInit {
  private subFiltersState: Subscription;
  private subTogglesState: Subscription;
  private subInvoiceStatusChange: Subscription;
  private subInvoicesData: Subscription;
  private subInvoicesLoading: Subscription;
  private subInvoicesUpdated: Subscription;

  public filterTheme: string = 'homepage';

  @ViewChild('invoiceTable') invoiceTable: InvoiceTableComponent;
  @ViewChild('filters') filters: FiltersComponent;

  public loading: boolean = true;
  public dataSource = new MatTableDataSource([]);

  private wellname: Set<string> = new Set();
  private contract_owner_name: Set<string> = new Set();
  private parent_vendor: Set<string> = new Set();
  private submit_date: Set<string> = new Set();
  private date_second: Set<string> = new Set();
  private status: Set<string> = new Set();

  private wellsSortedArray: string[];
  private contractOwnerNameSortedArray: string[];
  private vendorsSortedArray: string[];
  private datesSortedArray: string[];
  private datesSecondSortedArray: string[];
  private statusesSortedArray: string[];

  private filtersSet: Set<Set<string>> = new Set();

  public toggleStateData: TogglesState;
  public filtersState: any;
  public maxNptDuration;
  constructor(private store: Store<AppState>) {
    this.initSetOfFilters();
  }

  ngOnInit() {
    this.subTogglesState = this.store.pipe(select(getTogglesState))
      .subscribe((togglesState: TogglesState) => {
        this.toggleStateData = togglesState;
        this.store.dispatch(new InvoicesActions.GetInvoices([togglesState.isweather, togglesState.ismatchbydate, togglesState.ismatchbycontract, togglesState.nptdurationlow, togglesState.nptdurationhigh]));
      });
      this.subInvoicesData = this.store.pipe(select(getInvoicesData))
      .subscribe(data => {
        const nptDurationArr = this.generateNptArray(data);
        this.maxNptDuration = this.getMaxNptDuration(nptDurationArr);

          data.forEach(el => this.sliceDate(el, 10));
          this.dataSource.data = [...data];
          this.emptyFitersSets();
          this.dataSource.filteredData.forEach(el => this.initFiltersSets(el));
          this.initFiltersArrays();
          this.initSetOfFilters();
          this.initFiltersData();
          this.dispatchFiltersState();




       
             /*
        custom code for npt finish value in UI for npt duration inside filter component
        */ 
      // console.log(this.toggleStateData.nptdurationhigh);
       if (this.toggleStateData.nptdurationhigh === 1000 || this.toggleStateData.nptdurationhigh === 10000) {
        this.filters.nptFinishValue = this.maxNptDuration;
       } else {
         setTimeout(() => {
          this.filters.nptFinishValue = this.toggleStateData.nptdurationhigh;
        //  console.log( this.filters.nptFinishValue);
         });
       
       }
         // console.log(this.maxNptDuration);
      });
    this.subInvoicesLoading = this.store.pipe(select(getInvoicesLoading))
      .subscribe(loading => this.loading = loading);
    
    this.subInvoicesUpdated = this.store.pipe(select(getUpdatedInvoices))
      .subscribe(data => {
        if(!isNullOrUndefined(data)) {
          data.forEach(rec => {
            const invToUpdate = this.dataSource.data.find(x => {
              return x.id === rec.id;
            });

            if (invToUpdate) {
              invToUpdate.modified_by = rec.modified_by;
              invToUpdate.modified_date = rec.modified_date;
            }
          });
      }
    });
  }

  ngAfterViewInit() {
    this.subFiltersState = this.store.pipe(select(getFiltersState))
      .subscribe((filterState: FiltersState) => {
        setTimeout(() => {
          this.filtersState = filterState.filtersState;
        });
        this.invoiceTable.applyFilterPredicate();
        this.invoiceTable.applyFilter(filterState.filtersState);
         // changed here
        setTimeout(() => {
          this.filters.setControls(filterState.filtersState);
        });
       
        this.dataSource.filteredData.forEach(el => this.initFiltersSets(el));
        this.initFiltersArrays();
        this.initSetOfFilters();
        this.filtersSet.delete(this[filterState.lastSelectedFilter]);
        this.initFiltersData();
        }
      );
      this.dispatchFiltersState();
       // changed here
      // this.clearFilters();
  }

  ngOnDestroy() {
    this.subFiltersState.unsubscribe();
    this.subTogglesState.unsubscribe();
    this.subInvoicesData.unsubscribe();
    this.subInvoicesLoading.unsubscribe();
    if (this.subInvoicesUpdated) {
      this.subInvoicesUpdated.unsubscribe();
    }
    if (this.subInvoiceStatusChange) {
      this.subInvoiceStatusChange.unsubscribe();
    }
  }

  public setNptFinishDefault() {
    this.filters.nptFinishValue = this.toggleStateData.nptdurationhigh;
  }
  private generateNptArray(data) {
    const nptDurationArr = [];
    data.map(invoice => {
      nptDurationArr.push(invoice.npt_duration);
    });
    return nptDurationArr;
  }

  private getMaxNptDuration(arr) {
    let len = arr.length;
    let max = -Infinity;

    while (len--) {
        max = arr[len] > max ? arr[len] : max;
    }
    return max;
  }

  private slideToggleAction(): void {
    this.invoiceTable.clear();
    this.loading = true;
  }

  public toggleNPT(value: boolean): void {
    this.store.dispatch(new TogglesActions.UpdateToggles({isweather: value}));
    this.slideToggleAction();
  }

  public changeNptDuration(data): void {
    this.store.dispatch(new TogglesActions.UpdateToggles({nptdurationlow: data.nptdurationlow, nptdurationhigh: data.nptdurationhigh}));
    this.slideToggleAction();
  }

  public toggleMatchByDate(value: boolean): void {
    this.store.dispatch(new TogglesActions.UpdateToggles({ismatchbydate: value}));
    this.slideToggleAction();
  }

  public toggleMatchByContract(value: boolean): void {
    this.store.dispatch(new TogglesActions.UpdateToggles({ismatchbycontract: value}));
    this.slideToggleAction();
  }

  public onInvoiceUpdate(invoices: InvoiceItem[]): void {
    this.store.dispatch(new InvoicesActions.UpdateInvoice(invoices));
  }

  private emptyFitersSets(): void {
    this.filtersSet.forEach(filter => filter.clear());
  }

  private initFiltersSets(invoice: InvoiceItem): void {
    this.contract_owner_name.add(invoice.contract_owner_name);
    this.wellname.add(invoice.wellname);
    this.parent_vendor.add(invoice.parent_vendor);
    this.submit_date.add(invoice.submit_date);
    this.date_second.add(invoice.submit_date);
    this.status.add(invoice.status);
  }

  private initSetOfFilters(): void {
    this.filtersSet.add(this.contract_owner_name);
    this.filtersSet.add(this.wellname);
    this.filtersSet.add(this.parent_vendor);
    this.filtersSet.add(this.submit_date);
    this.filtersSet.add(this.date_second);
    this.filtersSet.add(this.status);
  }

  private sliceDate(invoice: InvoiceItem, position: number): void {
    if (invoice.submit_date) {
      invoice.submit_date = invoice.submit_date.slice(0, position);
    }
    if (invoice.npt_date_end) {
      invoice.npt_date_end = invoice.npt_date_end.slice(0, position);
    }
    if (invoice.npt_date_start) {
      invoice.npt_date_start = invoice.npt_date_start.slice(0, position);
    }
  }

  private loadingImitation() {
    this.loading = true;
    setTimeout(() => {
      this.loading = false;
    }, 500);
  }

  public dispatchFiltersState(): void {
    const filterState = {
      contract_owner_name: [...this.contractOwnerNameSortedArray],
      wellname: [...this.wellsSortedArray],
      parent_vendor: [...this.vendorsSortedArray],
      submit_date: [...this.datesSortedArray],
      date_second: [...this.datesSecondSortedArray],
      status: [...this.statusesSortedArray]
    };
    const nptDurationArr = this.generateNptArray(this.dataSource.filteredData);
    this.maxNptDuration = this.getMaxNptDuration(nptDurationArr);
    this.loadingImitation();
    this.store.dispatch(new FiltersActions.SetFiltersData(filterState));
  }

  private toSortArray(set: Set<string>): string[] {
    const sortedArr = Array.from(set).sort();
    if (set === this.submit_date || set === this.date_second) {
      sortedArr.reverse();
      sortedArr.unshift('');
    }
    return sortedArr;
  }

  private initFiltersArrays() {
    this.contractOwnerNameSortedArray = this.toSortArray(this.contract_owner_name);
    this.wellsSortedArray = this.toSortArray(this.wellname);
    this.vendorsSortedArray = this.toSortArray(this.parent_vendor);
    this.datesSortedArray = this.toSortArray(this.submit_date);
    this.datesSecondSortedArray = this.toSortArray(this.date_second);
    this.statusesSortedArray = this.toSortArray(this.status);
  }
  private initFiltersData() {
    this.emptyFitersSets();
    if (this.filtersSet.has(this.wellname)) {
      this.dataSource.filteredData.forEach(filter => this.wellname.add(filter.wellname));
      this.wellsSortedArray = this.toSortArray(this.wellname);
    }
    if (this.filtersSet.has(this.contract_owner_name)) {
      this.dataSource.filteredData.forEach(filter => this.contract_owner_name.add(filter.contract_owner_name));
      this.contractOwnerNameSortedArray = this.toSortArray(this.contract_owner_name);
    }
    if (this.filtersSet.has(this.parent_vendor)) {
      this.dataSource.filteredData.forEach(filter => this.parent_vendor.add(filter.parent_vendor));
      this.vendorsSortedArray = this.toSortArray(this.parent_vendor);
    }
    if (this.filtersSet.has(this.submit_date)) {
      this.dataSource.filteredData.forEach(filter => this.submit_date.add(filter.submit_date));
      this.datesSortedArray = this.toSortArray(this.submit_date);
    }
    if (this.filtersSet.has(this.date_second)) {
      this.dataSource.filteredData.forEach(filter => this.date_second.add(filter.submit_date));
      this.datesSecondSortedArray = this.toSortArray(this.date_second);
    }
    if (this.filtersSet.has(this.status)) {
      this.dataSource.filteredData.forEach(filter => this.status.add(filter.status));
      this.statusesSortedArray = this.toSortArray(this.status);
    }
  }

  public applySearch(data): void {
    this.filters.publishFilter(data.value, data.name);
  }

  public clearFilters(): void {
    this.store.dispatch(new FiltersActions.SetEmptyFiltersState());
    this.dataSource.data.forEach(el => {
      this.sliceDate(el, 10);
      this.initFiltersSets(el);
    });
    this.initSetOfFilters();
    this.initFiltersArrays();
    this.dispatchFiltersState();
  }

  public clearFiltersAndNpt(): void {
    this.clearFilters();
    this.filters.resetNptDuration();
  }

  public resetFilter(name): void {
    this.store.dispatch(new FiltersActions.SetEmptyFiltersState(name));
    this.dataSource.data.forEach(el => {
      this.sliceDate(el, 10);
      this.initFiltersSets(el);
    });
    this.initSetOfFilters();
    this.initFiltersArrays();
    this.dispatchFiltersState();
  }
}

import { getFlaggedInvoicesData, getFlaggedInvoicesLoading } from './../+store/flagged-invoices/flagged-invoices.selectors';
import { SupplierReportTableComponent } from './supplier-report-table/supplier-report-table.component';
import { FiltersComponent } from '../shared/components/filters/filters.component';
import { Component, ViewChild, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { BehaviorSubject, combineLatest, Subscription } from 'rxjs';
import { MatTableDataSource } from '@angular/material';
import { InvoiceItem } from '../shared/models/invoice-item-model';

import { Store, select } from '@ngrx/store';
import { AppState, getFiltersState, FiltersState } from '../+store';
import * as FlaggedInvoicesActions from '../+store/flagged-invoices/flagged-invoices.actions';
import * as FiltersActions from '../+store/filters/filters.actions';
import * as TogglesActions from '../+store/toggles/toggles.actions';
import {SupplierReportService} from './share/services/supplier-report.service';
import { FlaggedInvoice } from '../shared/models/flagged-invoice-model';
import { getTogglesState } from '../+store/toggles/toggles.selectors';
import { TogglesState } from '../shared/models/toggles-interface';

@Component({
  selector: 'app-supplier-report',
  templateUrl: './supplier-report.component.html',
  styleUrls: ['./supplier-report.component.css']
})
export class SupplierReportComponent implements OnInit, OnDestroy, AfterViewInit {
  private subFiltersState: Subscription;
  private subTogglesState: Subscription;
  private subFlaggedInvoices: Subscription;
  private subFlaggedInvoicesLoading: Subscription;
  private subCombinedWeatherLowImpact: Subscription;

  public loading = true;
  public dataSource = new MatTableDataSource([]);

  private wellname: Set<string> = new Set();
  private parent_vendor: Set<string> = new Set();
  private submit_date: Set<string> = new Set();
  private date_second: Set<string> = new Set();
  private contract_owner_name: Set<string> = new Set();
  private status: Set<string> = new Set();

  private filtersSet: Set<Set<string>> = new Set();

  private contractOwnerNameSortedArray: string[];
  private wellsSortedArray: string[];
  private vendorsSortedArray: string[];
  private datesSortedArray: string[];
  private datesSecondSortedArray: string[];
  private statusesSortedArray: string[];

  public maxNptDuration;

  @ViewChild('suplierTable') suplierTable: SupplierReportTableComponent;
  @ViewChild('filters') filters: FiltersComponent;

  public togglesStateData: TogglesState;
  public filtersState: any;

  constructor(private store: Store<AppState>,
              private supplierReportService: SupplierReportService) {
    this.initSetOfFilters();
  }

  ngOnInit() {
   this.subTogglesState = this.store.pipe(select(getTogglesState)).subscribe((togglesState: TogglesState) => {
      this.togglesStateData = togglesState;
      this.store.dispatch(new FlaggedInvoicesActions.GetFlaggedInvoices([this.togglesStateData.isweather, this.togglesStateData.ismatchbydate, this.togglesStateData.ismatchbycontract, togglesState.nptdurationlow, togglesState.nptdurationhigh]));
    });

    this.subFlaggedInvoicesLoading = this.store.pipe(select(getFlaggedInvoicesLoading))
      .subscribe(loading => this.loading = loading);

    this.subFlaggedInvoices = this.store.pipe(select(getFlaggedInvoicesData))
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
        }
    );
  }
  ngAfterViewInit() {
    this.subFiltersState = this.store.pipe(select(getFiltersState))
      .subscribe((filterState: FiltersState) => {
        setTimeout(() => {
          this.filtersState = filterState.filtersState;
        });
          this.suplierTable.applyFilterPredicate();
          this.suplierTable.applyFilter(filterState.filtersState);
          // changed here
         // setTimeout(() => {
            /**
             * commented this just to remove console error
             */
           // this.filters.setControls(filterState.filtersState);
          //});
          this.dataSource.filteredData.forEach(el => this.initFiltersSets(el));
          this.initFiltersArrays();
          this.initSetOfFilters();
          this.filtersSet.delete(this[filterState.lastSelectedFilter]);
          this.initFiltersData();
        }
      );
       // changed here
    //  this.clearFilters();
  }
  ngOnDestroy() {
    this.subFiltersState.unsubscribe();
    this.subTogglesState.unsubscribe();
     this.subFlaggedInvoices.unsubscribe();
     this.subFlaggedInvoicesLoading.unsubscribe();
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

  updateNpt(invoices: FlaggedInvoice[]) {
     this.supplierReportService.updateflaggedInvoices(invoices).subscribe();
     this.dispatchFiltersState();
  }
  updateInvoicesStatus(ids: string[]) {
    this.supplierReportService.updateInvoicesStatus(ids).subscribe();
  }
  private slideToggleAction(): void {
    this.loading = true;
  }

  toggleNPT(value: boolean) {
    this.store.dispatch(new TogglesActions.UpdateToggles({isweather: value}));
    this.slideToggleAction();
  }

  public changeNptDuration(data): void {
    this.store.dispatch(new TogglesActions.UpdateToggles({nptdurationlow: data.nptdurationlow, nptdurationhigh: data.nptdurationhigh}));
    this.slideToggleAction();
  }

  toggleMatchByDate(value: boolean) {
    this.store.dispatch(new TogglesActions.UpdateToggles({ismatchbydate: value}));
    this.slideToggleAction();
  }

  toggleMatchByContract(value: boolean) {
    this.store.dispatch(new TogglesActions.UpdateToggles({ismatchbycontract: value}));
    this.slideToggleAction();
  }

  private sliceDate(el: any, position: number) {
    if (el.submit_date) {
      el.submit_date = el.submit_date.slice(0, position);
    }
    if (el.npt_date_end) {
      el.npt_date_end = el.npt_date_end.slice(0, position);
    }
    if (el.npt_date_start) {
      el.npt_date_start = el.npt_date_start.slice(0, position);
    }
  }
  private emptyFitersSets(): void {
    this.filtersSet.forEach(filter => filter.clear());
  }

  private initFiltersSets(invoice: InvoiceItem) {
    this.contract_owner_name.add(invoice.contract_owner_name);
    this.wellname.add(invoice.wellname);
    this.parent_vendor.add(invoice.parent_vendor);
    this.submit_date.add(invoice.submit_date);
    this.date_second.add(invoice.submit_date);
    this.status.add(invoice.status);
  }

  private initSetOfFilters() {
    this.filtersSet.add(this.contract_owner_name);
    this.filtersSet.add(this.wellname);
    this.filtersSet.add(this.parent_vendor);
    this.filtersSet.add(this.submit_date);
    this.filtersSet.add(this.date_second);
    this.filtersSet.add(this.status);
  }

  private loadingImitation() {
    this.loading = true;
    setTimeout(() => {
      this.loading = false;
    }, 500);
  }

  dispatchFiltersState() {
    const filterStateObject = {
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
    this.store.dispatch(new FiltersActions.SetFiltersData(filterStateObject));
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
      this.dataSource.filteredData.forEach(el => this.wellname.add(el.wellname));
      this.wellsSortedArray = this.toSortArray(this.wellname);
    }
    if (this.filtersSet.has(this.contract_owner_name)) {
      this.dataSource.filteredData.forEach(el => this.contract_owner_name.add(el.contract_owner_name));
      this.contractOwnerNameSortedArray = this.toSortArray(this.contract_owner_name);
    }
    if (this.filtersSet.has(this.parent_vendor)) {
      this.dataSource.filteredData.forEach(el => this.parent_vendor.add(el.parent_vendor));
      this.vendorsSortedArray = this.toSortArray(this.parent_vendor);
    }
    if (this.filtersSet.has(this.submit_date)) {
      this.dataSource.filteredData.forEach(el => this.submit_date.add(el.submit_date));
      this.datesSortedArray = this.toSortArray(this.submit_date);
    }
    if (this.filtersSet.has(this.date_second)) {
      this.dataSource.filteredData.forEach(el => this.date_second.add(el.submit_date));
      this.datesSecondSortedArray = this.toSortArray(this.date_second);
    }
    if (this.filtersSet.has(this.status)) {
      this.dataSource.filteredData.forEach(el => this.status.add(el.status));
      this.statusesSortedArray = this.toSortArray(this.status);
    }
  }
  clearFilters() {
    this.store.dispatch(new FiltersActions.SetEmptyFiltersState());
    this.dataSource.data.forEach(el => {
      this.sliceDate(el, 10);
      this.initFiltersSets(el);
    });
    this.initSetOfFilters();
    this.initFiltersArrays();
    this.dispatchFiltersState();
    this.filters.resetToDefaultNPTs();
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

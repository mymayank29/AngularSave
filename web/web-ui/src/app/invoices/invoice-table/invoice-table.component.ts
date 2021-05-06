import { ChangeDetectionStrategy, Component, Input, ViewChild, Output, EventEmitter, OnInit, AfterViewInit, AfterViewChecked, DoCheck, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material';
import { MatSnackBar, MatSort, MatPaginator } from '@angular/material';
import { InvoiceItem } from '../../shared/models/invoice-item-model';
import { Status } from '../../shared/models/status-interface';
import { STATUSES } from '../../shared/constants/statuses';
import { INVOICES_VISIBLE_COLUMNS, INVOICES_VISIBLE_COLUMNS_OBJECT } from '../shared/const/invoices-visible-columns';
import { ROW_FIELDS_DB_TO_UI_MATCH, SEARCHING_COLUMNS } from '../../shared/constants/db-fields-to-ui-mathces';
import { ConvertToCSVService } from '../../shared/services/convert-to-csv.service';
import { SelectedInvoiceCommService } from '../../shared/services/selected-invoice-comm.service';
import * as XLSX from 'xlsx';
import { AppState, getInvoicesLoading, getInvoicesData } from 'src/app/+store';
import { Store, select } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { SelectionModel } from '@angular/cdk/collections';
import { DecimalPipe } from '@angular/common';
import { isNullOrUndefined } from 'util';
@Component({
  selector: 'app-invoice-table',
  templateUrl: './invoice-table.component.html',
  styleUrls: ['./invoice-table.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InvoiceTableComponent implements OnInit, AfterViewInit, AfterViewChecked, DoCheck, OnDestroy {

  @Input() dataSource: MatTableDataSource<any>;
 // @Input() loading: boolean;
  @Input() togglesState: any;

  @Output() resetFiltersWhenSearch: EventEmitter<any> = new EventEmitter<any>();
  @Output() updateInvoice: EventEmitter<InvoiceItem[]> = new EventEmitter<InvoiceItem[]>();
  @Output() searching: EventEmitter<any> = new EventEmitter<any>();
  @Output() nptFinishSetDefault: EventEmitter<any> = new EventEmitter<any>();
  showNoDatamessage= false;
  public loading: boolean;
  private previousInputValues: string[] = [];
  private needChangeStatus: boolean = false;
  private timer;
  private currentLoading: boolean = true;
  private selectedAll: boolean = false;
  private originalPredicate: any;
  public fromButton: number = 0;
  public leakageIdentifiedMax: number = 0;
  public leakageRecoveredMax: number = 0;
  public totalLeakage: number = 0;
  public totalRecovered: number = 0;
  private subInvoicesLoading: Subscription;
  public filters: any;
  public readonly tableHeaders = {...ROW_FIELDS_DB_TO_UI_MATCH};
  public displayedColumns: string[] = [...INVOICES_VISIBLE_COLUMNS];
  public editRecovered = false;
  public editComment = false;
  public selectValue: string = 'Select All';
  public invoiceStatusSelected: any = null;
  public fullscreen: boolean = false;
  public statuses: Status[] = STATUSES;
  public selectedRows: Set<InvoiceItem> = new Set();

  public localData: any;
  private timeout_1: any;
  private timeout_2: any;
  selection = new SelectionModel<InvoiceItem>(true, []);
  private previousValue: number;
  public openMatCard: boolean = false;
  public selectedColumns = INVOICES_VISIBLE_COLUMNS_OBJECT.map(obj => ({...obj}));
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(private convertToCSVService: ConvertToCSVService,
              private selectedInvoiceCommService: SelectedInvoiceCommService,
              private router: Router,
              public snackBar: MatSnackBar,
              private store: Store<AppState>,
              private decimalPipe: DecimalPipe
              ) { }

  ngOnInit() {
    this.originalPredicate = this.dataSource.filterPredicate;
    this.subInvoicesLoading = this.store.pipe(select(getInvoicesLoading))
      .subscribe(loading => this.loading = loading);
    this.changeDefault();

  
  }

  ngDoCheck() {
    this.resetLeakage();
    this.setMaxProgresBars();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  ngAfterViewChecked() {
    if(this.dataSource.filteredData.length === 0 && !this.loading){
      this.showNoDatamessage = true;
      this.nptFinishSetDefault.emit()

    } else if(this.dataSource.filteredData.length >0 && !this.loading){
      this.showNoDatamessage = false;
    }
    if (this.currentLoading !== this.loading) {
      this.currentLoading = this.loading;
      this.dataSource.sort = this.sort;
    }
  }
  ngOnDestroy() {
    this.resetLeakage();
  }

  configureTable() {
    this.openMatCard = !this.openMatCard;
    this.fromButton = 1;
   // console.log("inside configure table click");
  }
  onClickedOutside() {
    if(this.fromButton === 0) {
      this.openMatCard = !this.openMatCard;
    }
   // console.log("this.fromButton");
    this.fromButton = 0;
  }

  changeDefault() {
    let k = 0;
    const defaultInvObj = INVOICES_VISIBLE_COLUMNS_OBJECT.map(obj => ({...obj}));
    for (const invObj of defaultInvObj) {
     this.selectedColumns[k++].checked = invObj.checked;
    }
    this.applyChanges();
  }
  applyChanges() {
    this.displayedColumns = ['select'];
    for (let i = 0; i < this.selectedColumns.length; i++) {
      // console.log(this.selectedColumns[i].name + ' ' + this.selectedColumns[i].checked);
      if(this.selectedColumns[i].checked) {
        this.displayedColumns.push(this.selectedColumns[i].value);
      }
    }
  }

  checkColumn(col) {
     col.checked = !col.checked;
  }
  
  displayColumns(col) {
      col.checked = !col.checked;
      const includingColumn = col.value;
      if (col.checked) {
        let position = col.position;
        let found = false;
        if (position !== 1) {
          for (let i = position - 1; i >= 0; i--) {
            if (this.displayedColumns.indexOf(this.selectedColumns[i].value) !== -1) {
              position = this.displayedColumns.indexOf(this.selectedColumns[i].value) + 1;
              found = true;
              break;
            }
          }
          if (!found) {
            position = 1;
          }
        }
        this.displayedColumns.splice(position, 0, includingColumn);
      } else {
       let tempArray = this.displayedColumns.filter(c => {
         return includingColumn.indexOf(c);
        });
        this.displayedColumns = tempArray;
      }
  }

/** Whether the number of selected elements matches the total number of rows. */
isAllSelected() {
  const numSelected = this.selection.selected.length;
  const numRows = this.dataSource.data.length;
  return numSelected === numRows;
}

/** Selects all rows if they are not all selected; otherwise clear selection. */
masterToggle() {
  this.selectAll();
  this.isAllSelected() ?
      this.selection.clear() :
      this.dataSource.data.forEach(row => this.selection.select(row));
}
  public resetLeakage() {
    this.leakageIdentifiedMax = 0;
    this.totalLeakage = 0;
    this.leakageRecoveredMax = 0;
    this.totalRecovered = 0;
  }
  public setMaxProgresBars() {
    if ( this.dataSource.filteredData.length) {
      this.dataSource.filteredData.map((invoice) => {
        if (invoice.status !== 'Not Reviewed') {
          this.leakageIdentifiedMax += invoice.pt_spent_leakage_npt;
        }
        if (invoice.status === 'SCM Recovered/Closed') {
          this.leakageRecoveredMax += invoice.recovered;
         // console.log(this.leakageRecoveredMax + ' ' + invoice.recovered + typeof invoice.recovered );
        }
        this.increaseTotals(invoice);
      });
    }
  }

  public publishFilter(value, name): void {
    const dataSearch = {
      value: value,
      name: name
    };
    this.searching.emit(dataSearch);
  }
  public onSearch(event): void {
    this.searching.emit(event);
  }

  private searchHelper(): void {
    this.paginator.pageIndex = 0;
    this.selectedAll = false;
    this.selectValue = 'Select All';
    this.selectedRows.clear();
  }

  private checkFilterValue(item, filter, name?: string): boolean {
    if (name === 'multiselect') {
      if (filter.length === 0) {
        return item.toString().trim().toLowerCase().indexOf(filter.trim().toLowerCase()) !== -1;
      } else {
        return filter.indexOf(item) !== -1;
      }
    } else {
      if (item) {
        return item.toString().trim().toLowerCase().indexOf(filter.trim().toLowerCase()) !== -1;
      }
    }
  }

  private matchBySearch(data, filter): boolean[] {
    const searchTerms = JSON.parse(filter);
    return SEARCHING_COLUMNS.map(key => {
      if (data[key]) {
        return data[key].toString().trim().toLowerCase().indexOf(searchTerms['searching'].trim().toLowerCase()) !== -1;
      }
    });
  }

  private tableFilter(): (data: any, filter: string) => any {
    const filterFunc = ((data, filter) => {
      const searchTerms = JSON.parse(filter);
      const searchValue = this.matchBySearch(data, filter);
      return this.checkFilterValue(data['wellname'], searchTerms['wellname'], 'multiselect')
        && this.checkFilterValue(data['parent_vendor'], searchTerms['parent_vendor'], 'multiselect')
        && this.checkFilterValue(data['contract_owner_name'], searchTerms['contract_owner_name'], 'multiselect')
        && data['submit_date'] >= searchTerms['submit_date']
        && (searchTerms['date_second'] ? (data['submit_date'] <= searchTerms['date_second']) : (data['submit_date'] >= searchTerms['submit_date']))
        && this.checkFilterValue(data['status'], searchTerms['status'], 'multiselect')
        && (searchValue.indexOf(true) !== -1) ? true : false;
    });
    return filterFunc;
  }

  public increaseTotals(invoice) {
   // if (invoice.status === 'NPT Out of Scope') {
      this.totalLeakage += invoice.pt_spent_leakage_npt;
   // }
    if (invoice.status !== 'Not Reviewed' && invoice.status !== 'NPT Out of Scope') {
      this.totalRecovered += invoice.pt_spent_leakage_npt;
    }
  }

  public onRowClicked(invoice: InvoiceItem): void {
    // this.selection.toggle(invoice);
    this.selectedRows.has(invoice) ? this.selection.deselect(invoice) : this.selection.select(invoice);
    this.selectedRows.has(invoice) ? this.selectedRows.delete(invoice) : this.selectedRows.add(invoice);
  }

  public onItemSelect(invoice, e) {
    if ((e.buttons === 1 || e.buttons === 2) && this.selectedRows.has(invoice)) {
      this.selectedRows.delete(invoice);
      this.selection.deselect(invoice);
     } else if ((e.buttons === 1 || e.buttons === 2) && !this.selectedRows.has(invoice)) {
      this.selectedRows.add(invoice);
      this.selection.select(invoice);
     }
  }

  public expand(): void {
    this.fullscreen = !this.fullscreen;
  }

  public setSelectedRow(invoice: InvoiceItem) {
    this.invoiceStatusSelected = null;
    if (this.selectedRows.has(invoice)) {
      this.selection.select(invoice);
     
    } else {
      this.selection.deselect(invoice);
    }
  
    return {
      selected: this.selectedRows.has(invoice)
    };
  }

  public selectAll(): void {
    this.selectedAll = !this.selectedAll;
    if (this.selectedAll) {
      this.dataSource.filteredData.forEach(el => {
        this.selectedRows.add(el);
        this.totalLeakage += el.pt_spent_leakage_npt;
        this.totalRecovered += el.recovered;
      });
    } else {
      this.selectedRows.clear();
    }
    this.selectValue = this.selectedAll ? 'Unselect All' : 'Select All';
    this.invoiceStatusSelected = null;
  }

  public changeStatus(value: string): void {
    if (!this.selectedRows.size) {
      this.openSnackBar('Please select invoices!', 1300);
    } else if (this.selectedRows.size >= 300) {
      this.openSnackBar('Please select invoices lessthan 300)', 5300);
    } else {
      this.selectedRows.forEach(el => {
        this.updateLeakagesByStatus(value, el);
        el.status = value;
      });
     this.updateInvoice.emit(Array.from(this.selectedRows));
      this.clear();
    }
  }

  private updateLeakagesByStatus(status: string, item) {
    if ((status === 'Flagged for Review') && !(item.spent_leakage_confirmed >= 0)) {
      const leakage = item.pt_spent_leakage_npt;
      item.spent_leakage_confirmed = leakage;
    } else if (status === 'NPT Out of Scope') {
      item.spent_leakage_confirmed = 0;
      item.recovered = 0;
    } else if (status === 'Not Reviewed') {
      item.spent_leakage_confirmed = null;
      item.recovered = null;
    }

  }

  public clear(): void {
    this.selectedRows.clear();
    this.selectedAll = false;
    this.selectValue = 'Select All';
  }

  public applyFilter(filterStateObj: {}): void {
    const filtersValues = [];
    for (const key in filterStateObj) {
      if (filterStateObj.hasOwnProperty(key)) {
        filtersValues.push(filterStateObj[key]);
      }
    }
    if (filtersValues.every(el => el === '')) {
      this.dataSource.filter = null;
    } else {
      this.dataSource.filter = JSON.stringify(filterStateObj);
    }
    setTimeout(() => {
      this.filters = filterStateObj;
    });
    this.searchHelper();
  }

  public applyFilterPredicate(): void {
    this.dataSource.filterPredicate = this.tableFilter();
  }

  // public saveCsvFile(): void {
  //   const csvArray = this.convertToCSVService.convertJsonToCSV(this.dataSource.filteredData);
  //   const blob = new Blob([csvArray], { type: 'text/csv' });
  //   this.convertToCSVService.saveFile('invoices.csv', blob);
  // }

  private createInvoicesTabSelection(data) {
    const invoices = [];
   console.log("inside invoices tab selection");
    for(let i=0 ;i<data.length;i++) {
      let invoice={};
     for(let j=0;j<this.selectedColumns.length;j++){
       if(this.selectedColumns[j].checked == true) {
       // console.log(this.selectedColumns[j].value+"----"+this.selectedColumns[j].name+" ---- "+ data[i][this.selectedColumns[j].value]);
      //  invoice[this.selectedColumns[j].name] = data[i][this.selectedColumns[j].value];
        invoice[this.selectedColumns[j].name] = (this.selectedColumns[j].value == 'key') ? data[i]['id'] : data[i][this.selectedColumns[j].value];
       }
     }
     invoices.push(invoice);
    }
    return invoices;
  }

  private createInvoicesTab(data) {
    const invoices = data.map((obj) => ({
      'Supplier': obj['parent_vendor'],
      'Contract ID': obj['contract_id'],
      'Contract Owner': obj['contract_owner_name'],
      'WBS #': obj['afe'],
      'Job Type': obj['jobtyp'],
      'Ariba Doc ID': obj['ariba_doc_id'],
      'Submit Date': obj['submit_date'],
      'Npt Start': obj['npt_date_start'],
      'Npt End': obj['npt_date_end'],
      'NPT (hr)': obj['npt_duration'],
      'Leakage Identified': obj['pt_spent_leakage_npt'],
      // 'Leakage Confirmed': obj['spent_leakage_confirmed'],
      'Leakage Recovered': obj['recovered'],
      'Status': obj['status'],
      'Comments': obj['comment'],
      'Modified By': obj['modified_by'],
      'Modified Date': obj['modified_date'],
      'ID': obj['id'],
    }));
    console.log('inside normal invoicestab');
    console.log(invoices);
    return invoices;
  }

  public saveCsvFile(): void {
  //  const invoicesData = this.createInvoicesTab(this.dataSource.filteredData);
    const invoicesData2 = this.createInvoicesTabSelection(this.dataSource.filteredData);
    const invoices_sheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(invoicesData2);
    invoices_sheet['!cols'] = [{wpx: 150}];
    const workbook: XLSX.WorkBook = {
      Sheets: {
        'Invoices': invoices_sheet
      },
      SheetNames: ['Invoices']
    };
    XLSX.writeFile(workbook, 'Invoices.xlsx');
  }


  public navigateToInvoicesDetail(): void {
    if (!this.selectedRows.size) {
      this.openSnackBar('Please select invoices!', 1300);
    } else {
      this.selectedInvoiceCommService.publishSelectedInvoices(Array.from(this.selectedRows));
      sessionStorage.setItem('invoicesDetails', JSON.stringify(Array.from(this.selectedRows)));
      sessionStorage.setItem('invoicesDetailsToggles', JSON.stringify({ isWeather: this.togglesState.isweather, isMatchByDate: this.togglesState.ismatchbydate }));
      this.router.navigateByUrl('/invoice-detail');
    }
  }

  public navigateFromNPT(): void {
    this.router.navigateByUrl('/npt');
  }

  private openSnackBar(message: string, duration: number): void {
    this.snackBar.open(message, '', {
      verticalPosition: 'top',
      duration: duration,
      panelClass: 'default-snackbar'
    });
  }

  // public modelChange(event) {
  //   console.log('model'+ event);
  //   return event;
  // }
  public onInputFocus1(event): void {
    let y = parseFloat(event.target.value .replace(/,/g, ''));
    event.target.value = y;
    if (event.target.value === '0' || event.target.value === '0.00' || isNaN(event.target.value)) {
      event.target.value = '';
    }
    let inputElement = <HTMLInputElement>event.target;
    // console.log(inputElement+' '+typeof inputElement);
    inputElement.setSelectionRange(0, inputElement.value.length);
    
    this.previousValue = event.target.value;
  }
  public enterText(event) {
    event.target.blur();
  }
 public isValid(n): boolean {
    // console.log("isValid" + n + isNaN(n));
     if(isNaN(n)) {
       return false;
     }
    else {
      return true;
    }
 }
  public save1(event, invoice): void {
    const target = event.target;
    // let b = this.isValid(target.value);
    // console.log("boolean "+ b);
    if(!this.isValid(target.value)) {
     // console.log("Not a valid number");
      target.value = '';
      invoice.recovered = null;
    }
    invoice.recovered = +invoice.recovered;
    // console.log(target.value+" "+invoice.recovered+" "+typeof invoice.recovered);
    this.needChangeStatus = true;
    if(this.previousValue === target.value) {
      this.needChangeStatus = false;
    }
    if (target.value === '') {
      target.value = '0.00';
      this.needChangeStatus = false;
    }
    if (this.needChangeStatus) {
      invoice.status = this.statuses[3].value;
    }
    if (invoice.recovered == null || invoice.recovered == '') {
      invoice.recovered = 0.00;
    }
    this.updateInvoice.emit([invoice]);
    target.value = this.decimalPipe.transform(target.value, '.2-2');
  }
  public onInputFocus2(event): void {

  }
  public save2(event, invoice): void {
    this.updateInvoice.emit([invoice]);
   
  }
  public checkSort(sort: MatSort) {
    if(isNullOrUndefined(sort)){
      return false;
    }
    if(sort.direction == 'asc' || sort.direction == 'desc') {
      return false;
    }
    return true;
  }
}

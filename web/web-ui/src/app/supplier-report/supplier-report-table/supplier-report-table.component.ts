import { FlaggedInvoice } from './../../shared/models/flagged-invoice-model';
import { Component, Input, OnInit, DoCheck, Output, EventEmitter, OnDestroy, OnChanges } from '@angular/core';
import { MatTableDataSource, MatSnackBar } from '@angular/material';
import { Subscription } from 'rxjs';
import * as XLSX from 'xlsx';
import { SR_COLUMNS, FLAGGED_INVOICES_TO_UI, SR_TO_MARGING_CELL } from '../share/constants/supplier-report-constants';
import { NPT_SPEND_DATA_SHEET, SUPPLIER_REPORT_SHEET } from '../share/constants/supplier-report-workbook';
import { Status } from '../../shared/models/status-interface';
import { LineItemsService } from '../../invoice-detail/shared/services/line-items.service';
import { Store, select } from '@ngrx/store';
import { AppState, getFlaggedInvoicesLoaded, getFlaggedInvoicesLoading } from 'src/app/+store';
import { isNullOrUndefined } from 'util';
import { SelectionModel } from '@angular/cdk/collections';

@Component({
  selector: 'app-supplier-report-table',
  templateUrl: './supplier-report-table.component.html',
  styleUrls: ['./supplier-report-table.component.css']
})

export class SupplierReportTableComponent implements OnInit, OnChanges, OnDestroy {

  @Input() dataSource: MatTableDataSource<any>;
 // @Input() loading: boolean;

  @Output() updateNpt: EventEmitter<FlaggedInvoice[]> = new EventEmitter<FlaggedInvoice[]>();
  @Output() updateInvoicesStatus: EventEmitter<string[]> = new EventEmitter<string[]>();

  private subFlaggedInvoicesState: Subscription;
  private subFlaggedInvoicesLoading: Subscription;
  private nullMessage: string = '';
  private lineItemsData: any;
  private subLineItems: Subscription;
  private originalPredicate;
  private lineItemsIdsSet: Set<string> = new Set();

  public fullscreen: boolean = false;
  public displayedColumns: string[] = SR_COLUMNS;
  public dbToUiMatches = {...FLAGGED_INVOICES_TO_UI};
  public selectedRows: Set<FlaggedInvoice> = new Set();
  public loaded: boolean;
  public loading: boolean;
  // private toMargingCells = SR_TO_MARGING_CELL;
  // private mergedCells = [];
  // private nptSpendDataFields: string[] = NPT_SPEND_DATA_SHEET;
  // private supplierReportFields: string[] = SUPPLIER_REPORT_SHEET;
  selection = new SelectionModel<FlaggedInvoice>(true, []);
  private selectedAll: boolean = false;
  constructor(public snackBar: MatSnackBar,
              private store: Store<AppState>,
              private lineItemsService: LineItemsService) {}

  ngOnInit() {
    this.originalPredicate = this.dataSource.filterPredicate;
    this.subFlaggedInvoicesState =  this.store.pipe(select(getFlaggedInvoicesLoaded))
                                    .subscribe((loaded) => 
                                    this.loaded = loaded);
    this.subFlaggedInvoicesLoading = this.store.pipe(select(getFlaggedInvoicesLoading))
                                    .subscribe((loading) => {
                                     
                                      this.loading = loading;

                                      if (isNullOrUndefined(this.dataSource.filteredData) || this.dataSource.filteredData.length == 0) {
                                        this.nullMessage = 'No data available';
                                      } else {
                                        this.nullMessage = 'Data present';
                                        this.getLineItems();
                                      }
                                    }
                                      );
   
}

  ngOnChanges() {
    this.getLineItems();
  }

  ngOnDestroy(): void {
    if(!isNullOrUndefined(this.subLineItems)) {
      this.subLineItems.unsubscribe();
    }
    this.subFlaggedInvoicesState .unsubscribe();
    this.subFlaggedInvoicesLoading.unsubscribe();
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
public selectAll(): void {
  this.selectedAll = !this.selectedAll;
  if (this.selectedAll) {
    this.dataSource.filteredData.forEach(el => {
      this.selectedRows.add(el);
      // this.totalLeakage += el.pt_spent_leakage_npt;
      // this.totalRecovered += el.recovered;
    });
  } else {
    this.selectedRows.clear();
  }
  // this.selectValue = this.selectedAll ? 'Unselect All' : 'Select All';
  // this.invoiceStatusSelected = null;
}


  public onRowClicked(invoice: FlaggedInvoice): void {
    this.selectedRows.has(invoice) ? this.selection.deselect(invoice) : this.selection.select(invoice);
    if (this.selectedRows.has(invoice)) {
      this.selectedRows.delete(invoice);
    } else {
      this.selectedRows.add(invoice);
    }
  }

  public setSelectedRow(row: FlaggedInvoice) {
    if (this.selectedRows.has(row)) {
      this.selection.select(row);
     
    } else {
      this.selection.deselect(row);
    }
    return {
      selected: this.selectedRows.has(row)
    };
  }

  private openSnackBar(message: string, duration: number): void {
    this.snackBar.open(message, '', {
      verticalPosition: 'top',
      duration: duration,
      panelClass: 'default-snackbar'
    });
  }

  public updateFlaggedInvoices(): void {
    const selectedInvoicesId = [];
    if (!this.selectedRows.size) {
      this.openSnackBar('Please select invoices!', 1300);
    } else {
      const invoicesArray: FlaggedInvoice[] = Array.from(this.selectedRows);
      invoicesArray.map(invoice => {
        selectedInvoicesId.push(invoice['id']);
      });
      let data = this.dataSource.data;
      data = data.filter(invoice => selectedInvoicesId.indexOf(invoice['id']) === -1);
      this.dataSource.data = data;
      this.dataSource.filteredData = data;
      this.updateNpt.emit(invoicesArray);
    }
  }

  /* Evaluated and store an evaluation of the rowspan for each row.
   * The key determines the column it affects, and the accessor determines the
   * value that should be checked for spanning.
  */
/*
  private cacheSpan(data, key, accessor): void {
    for (let i = 0; i < data.length;) {
      const currentValue = accessor(data[i]);
      let count = 1;
      // Iterate through the remaining rows to see how many match
      //   the current value as retrieved through the accessor.
      for (let j = i + 1; j < data.length; j++) {
        if (currentValue !== accessor(data[j])) {
          break;
        }
        count++;
      }
      if (!this.mergedCells[i]) {
        this.mergedCells[i] = {};
      }
      // Store the number of similar values that were found (the span)
      // and skip i to the next unique row.
      this.mergedCells[i][key] = count;
      i += count;
    }
  }
*/

/*
  private clearMergedCells(): void {
    this.mergedCells = [];
  }
*/

/*
  private initCacheSpans(data: FlaggedInvoice[]) {
    this.clearMergedCells();
    this.cacheSpan(data, this.toMargingCells[0],
      d => d[this.toMargingCells[0]]);
    this.cacheSpan(data, this.toMargingCells[1],
      d => d[this.toMargingCells[0]] + d[this.toMargingCells[1]]);
  }
*/

/*
  public getRowSpan(col, index) {
    return this.mergedCells[index] && this.mergedCells[index][col];
  }
*/

/*
  public integrateCells(mergedCells, letters) {
    let cell_1start_Index = 2;
    let cell_2start_index = 2;
    const mergedCellsSR = [];
    mergedCells.map(el => {
      if (el.wellname >= 2) {
        const lastIndex = cell_1start_Index + (el.wellname - 1);
        mergedCellsSR.push({s: `${letters[0]}${cell_1start_Index}` , e: `${letters[0]}${lastIndex}`});
        cell_1start_Index += el.wellname;
      }
      if (el.parent_vendor >= 2 ) {
        const lastIndex = cell_2start_index + (el.parent_vendor - 1);
        mergedCellsSR.push({s: `${letters[1]}${cell_2start_index}` , e: `${letters[1]}${lastIndex}`});
        cell_2start_index += el.parent_vendor;
      }
    });
    return mergedCellsSR;
  }
*/

  public expand(): void {
    this.fullscreen = !this.fullscreen;
  }

  private createSupplierReport(data) {
    const supplierReport = data.map((obj) => ({
      'Category Manager': obj['contract_owner_name'],
      'Supplier': obj['parent_vendor'],
      'Well Name - Job': `${obj['wellname']} - ${obj['jobtyp']}`,
      'ID': obj['id'],
      'NPT RefNo': obj['npt_ref_no'],
      'NPT Duration': obj['npt_duration'],
      'NPT Activity': obj['servicetyp'],
      'NPT Description': obj['npt_desc'],
      'Estimated NPT Spend': obj['pt_spent_leakage_npt']
    }));
    return supplierReport;
  }

  private createNptSpendData(data) {
    const nptSpendData = data.map((obj) => ({
      'Supplier': obj['parent_vendor'],
      'Well Name - Job': `${obj['wellname']} - ${obj['jobtyp']}`,
      'WBS #': obj['afe'],
      'NPT Activity': obj['servicetyp'],
      'NPT RefNo': obj['npt_ref_no'],
      'Rig Number': obj['rig_no'],
      'NPT Description': obj['npt_desc'],
     // 'NPT': obj['npt_event_no'],
    //  'NPT Type - Type Detail': obj['npt_type_detail'],
      'NPT Type Description': obj['npt_type_detail_desc'],
      'NPT Date': `${obj['npt_date_start'].slice(0, 10)} - ${obj['npt_date_end'].slice(0, 10)}`,
      'NPT Duration(Hr)': obj['npt_duration'],
     // 'Contract Title': obj['contract_title'],
     'Contract ID': obj['contract_id'],
      'Ariba Document ID': obj['ariba_doc_id'],
      'Work Start Date': obj['work_start_date'].slice(0, 10),
      'Work End Date': obj['work_end_date'].slice(0, 10),
      'Submit Date': obj['submit_date'].slice(0, 10),
      'Approved Date': obj['approved_date'],
      'Estimated NPT Spend': obj['pt_spent_leakage_npt'],
      'ID': obj['id']
    }));
    return nptSpendData;
  }

  private getMatchingLineItems(lineItemInvoice) {
    //console.log(JSON.stringify(lineItemInvoice));
    let arr =[];
    for(let i=0;i<lineItemInvoice.length;i++) {
     for(let j=0;j<this.dataSource.filteredData.length;j++) {
       if(lineItemInvoice[i].ariba_doc_id == this.dataSource.filteredData[j].ariba_doc_id){
         lineItemInvoice[i].id = this.dataSource.filteredData[j].id;
        console.log(lineItemInvoice[i].id + ' ' + this.dataSource.filteredData[j].id);
         arr.push(lineItemInvoice[i]);
       }
     }
    }
  // let invoicesLine =  lineItemInvoice.find(inv => inv['ariba_doc_id'] === this.dataSource.filteredData['ariba_doc_id']);
   return arr;
  }

  private createLineItemsReport(data) {
   // let lineItemsReport = {};
    if(isNullOrUndefined(data)) {
      const lineItemsReport = {
        'Contract Title': '',
        'Invoice No': '',
        'Part': '',
        'Description': '',
        'Unit Price': '',
        'QTY': '',
        'ID': '',
        'Discount': '',
        'Net Price': '',
        'Work Start': '',
        'Work Stop': '',
     //   'Duration': ''
      };
      return lineItemsReport;
    }
    else {
     const lineItemsReport = data.map((obj) => {
      const matchingInvoice = this.dataSource.filteredData.find(invoice => {
        return invoice['ariba_doc_id'] === obj['ariba_doc_id'];
      });

      return ({
        'Contract Title': obj['contract_title'],
        'Invoice No': obj['invoice_no'],
        'Part': obj['part_number'],
        'Description': obj['description'],
        'Unit Price': obj['unit_price_llf'],
        'QTY': obj['quantity_llf'],
        //'ID': matchingInvoice['id'],
        'ID' : obj['id'],
        'Discount': obj['discount'],
        'Net Price': obj['invoice_net_amount_usd'],
        'Work Start': obj['start_date'].slice(0, 10),
        'Work Stop': obj['end_date'].slice(0, 10),
       // 'Duration': obj['duration']
      });
    });
    return lineItemsReport;
  }
  }

  private getInvoiceIds() {
    const invoicesIds = [];
    this.dataSource.filteredData.map(invoice => {
      invoicesIds.push(invoice['id']);
    });
    return invoicesIds;
  }

  private setAribaIds(): void {
    this.dataSource.filteredData.map(invoice => {
      this.lineItemsIdsSet.add(invoice['ariba_doc_id']);
    });
  }

  private getAribaIds(): string[] {
    this.setAribaIds();
    return Array.from(this.lineItemsIdsSet);
  }

  private getLineItems(): void {
    if (this.dataSource.filteredData.length) {
      this.loading = true;
    const invoicesAribaIds = this.getAribaIds();
      if (invoicesAribaIds.length) {
        this.subLineItems = this.lineItemsService.getLetItemsByAribaId(invoicesAribaIds)
        .subscribe(
            data => {
              this.loading = false;
              this.lineItemsData = data;
            }
        );
      }
        }
  }

  public exportToExcel(): void {
    console.log("inside export to excel");
    console.log(JSON.stringify(this.dataSource.filteredData));
    const supplier_report = this.createSupplierReport(this.dataSource.filteredData);
    const npt_spend_report = this.createNptSpendData(this.dataSource.filteredData);
    console.log("created npt spend report now creating line_items_report");
    let filteredLineItems = this.getMatchingLineItems(this.lineItemsData);
    console.log("final line items");
    console.log(JSON.stringify(filteredLineItems));
    const line_items_report = this.createLineItemsReport(filteredLineItems);

    const supplier__sheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(supplier_report);
    const npt_spend_sheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(npt_spend_report);
    const line_items_sheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(line_items_report);

    supplier__sheet['!cols'] = [{wpx: 200}];
    npt_spend_sheet['!cols'] = [{wpx: 200}];

    const workbook: XLSX.WorkBook = {
      Sheets: {
       
        'Supplier_Report': npt_spend_sheet,
        'Line_Item_Detail': line_items_sheet
      },
      SheetNames: [ 'Supplier_Report', 'Line_Item_Detail']
    };

    XLSX.writeFile(workbook, 'Supplier Report.xlsx');
    console.log("excel done successfully");
    this.updateStatusInvoicesByID();
  }

  private updateStatusInvoicesByID(): void {
    const invoicesIds = this.getInvoiceIds();
    this.updateInvoicesStatus.emit(invoicesIds);
  }

  applyFilter(filterStateObj): void {
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
  }

  public applyFilterPredicate(): void {
    this.dataSource.filterPredicate = this.tableFilter();
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

  private tableFilter(): (data: any, filter: string) => boolean {
    const filterFunc = ((data, filter) => {
      const searchTerms = JSON.parse(filter);
      return this.checkFilterValue(data['wellname'], searchTerms['wellname'], 'multiselect')
        && this.checkFilterValue(data['parent_vendor'], searchTerms['parent_vendor'], 'multiselect')
        && this.checkFilterValue(data['contract_owner_name'], searchTerms['contract_owner_name'], 'multiselect')
        && data['submit_date'] >= searchTerms['submit_date']
        && (searchTerms['date_second'] ? (data['submit_date'] <= searchTerms['date_second']) : (data['submit_date'] >= searchTerms['submit_date']));
    });
    return filterFunc;
  }

  public getDrillingReports(): void {
    const invoicesIds = this.getInvoiceIds();
    const a = document.createElement('A');
    a['href'] = `/api/drilling?ids=${invoicesIds}`;
    a['download'] = `drilling-reports.html`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  }
}

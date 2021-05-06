import { Component, ViewChild, OnInit, AfterViewInit, Output, EventEmitter } from '@angular/core';
import { MatTableDataSource } from '@angular/material';
import { MatSort, MatPaginator } from '@angular/material';
import { InvoiceItem } from '../../shared/models/invoice-item-model';
import { ROW_FIELDS_DB_TO_UI_MATCH } from '../../shared/constants/db-fields-to-ui-mathces';

import { SelectedInvoiceCommService } from '../../shared/services/selected-invoice-comm.service';
import { InvoicesInDetailCommService } from '../shared/services/invoices-in-detail-comm.service';
import { INVOICES_DETAILS_COLUMNS } from '../shared/const/invoices-details-columns';

@Component({
  selector: 'app-invoices-in-detail',
  templateUrl: './invoices-in-detail.component.html',
  styleUrls: ['./invoices-in-detail.component.css']
})
export class InvoicesInDetailComponent implements OnInit, AfterViewInit {

  fleetData: InvoiceItem[] = [];
  dataSource = new MatTableDataSource(this.fleetData);

  fullscreen: boolean = false;
  selectedRows: Set<InvoiceItem> = new Set();
  dbToUiMatches = {...ROW_FIELDS_DB_TO_UI_MATCH};
  displayedColumns: string[] = INVOICES_DETAILS_COLUMNS;

  spans = [];

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  @Output() getInvoices: EventEmitter<any> = new EventEmitter<any>();

  constructor(private invoicesInDetailCommService: InvoicesInDetailCommService) { }

  ngOnInit() {
    const data = JSON.parse(sessionStorage.getItem('invoicesDetails'));
          this.fleetData = data;
          this.dataSource.data = this.fleetData;
          this.dataSource.filteredData = [...this.fleetData];
          this.initCacheSpans(data);
          this.selectedRows.add(data[0]);
          const selectedInvoices = Array.from(this.selectedRows);
          this.invoicesInDetailCommService.publishSelectedInvoices(selectedInvoices);
  }


  getRowSpan(col, index) {
    return this.spans[index] && this.spans[index][col];
  }


  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.getInvoices.emit(this.dataSource.filteredData);
  }

  onRowClicked(invoice: InvoiceItem) {
    this.selectedRows.has(invoice) ? (this.selectedRows.size > 1 ? this.selectedRows.delete(invoice) : '') : this.selectedRows.add(invoice);
    const selectedInvoices = Array.from(this.selectedRows);
    this.invoicesInDetailCommService.publishSelectedInvoices(selectedInvoices);
  }

  expand() {
    this.fullscreen = !this.fullscreen;
  }

  setSelectedRow(row: InvoiceItem) {
    return {
      selected: this.selectedRows.has(row)
    };
  }

  cacheSpan(data, key, accessor) {
    for (let i = 0; i < data.length;) {
      const currentValue = accessor(data[i]);
      let count = 1;

      // Iterate through the remaining rows to see how many match
      // the current value as retrieved through the accessor.
      for (let j = i + 1; j < data.length; j++) {
        if (currentValue !== accessor(data[j])) {
          break;
        }
        count++;
      }

      if (!this.spans[i]) {
        this.spans[i] = {};
      }

      // Store the number of similar values that were found (the span)
      // and skip i to the next unique row.
      this.spans[i][key] = count;
      i += count;
    }
  }

  private initCacheSpans(data: InvoiceItem[]) {
    this.cacheSpan(data, this.displayedColumns[0],
      d => d[this.displayedColumns[0]]);
    this.cacheSpan(data, this.displayedColumns[1],
      d => d[this.displayedColumns[0]] + d[this.displayedColumns[1]]);
    this.cacheSpan(data, this.displayedColumns[2],
      d => d[this.displayedColumns[0]] + d[this.displayedColumns[1]] + d[this.displayedColumns[2]]);
    this.cacheSpan(data, this.displayedColumns[3],
      d => d[this.displayedColumns[0]] + d[this.displayedColumns[1]] + d[this.displayedColumns[2]] + d[this.displayedColumns[3]]);
    this.cacheSpan(data, this.displayedColumns[4],
      d => d[this.displayedColumns[0]] + d[this.displayedColumns[1]] + d[this.displayedColumns[2]] + d[this.displayedColumns[3]] + d[this.displayedColumns[4]]);
    this.cacheSpan(data, this.displayedColumns[5],
      d => d[this.displayedColumns[0]] + d[this.displayedColumns[1]] + d[this.displayedColumns[2]] + d[this.displayedColumns[3]] + d[this.displayedColumns[4]] + d[this.displayedColumns[5]]);
    this.cacheSpan(data, this.displayedColumns[6],
      d => d[this.displayedColumns[0]] + d[this.displayedColumns[1]] + d[this.displayedColumns[2]] + d[this.displayedColumns[3]] + d[this.displayedColumns[4]] + d[this.displayedColumns[5]] + d[this.displayedColumns[6]]);
    this.cacheSpan(data, this.displayedColumns[7],
      d => d[this.displayedColumns[0]] + d[this.displayedColumns[1]] + d[this.displayedColumns[2]] + d[this.displayedColumns[3]] + d[this.displayedColumns[4]] + d[this.displayedColumns[5]] + d[this.displayedColumns[6]] + d[this.displayedColumns[7]]);
    this.cacheSpan(data, this.displayedColumns[8],
      d => d[this.displayedColumns[0]] + d[this.displayedColumns[1]] + d[this.displayedColumns[2]] + d[this.displayedColumns[3]] + d[this.displayedColumns[4]] + d[this.displayedColumns[5]] + d[this.displayedColumns[6]] + d[this.displayedColumns[7]] + d[this.displayedColumns[8]]);
    this.cacheSpan(data, this.displayedColumns[9],
        d => d[this.displayedColumns[0]] + d[this.displayedColumns[1]] + d[this.displayedColumns[2]] + d[this.displayedColumns[3]] + d[this.displayedColumns[4]] + d[this.displayedColumns[5]] + d[this.displayedColumns[6]] + d[this.displayedColumns[7]] + d[this.displayedColumns[8]] + d[this.displayedColumns[9]]);
  }

}

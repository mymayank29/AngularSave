import { Component, ViewChild, OnInit, OnDestroy, AfterViewInit, Output, EventEmitter } from '@angular/core';
import { MatTableDataSource } from '@angular/material';
import { MatSort, MatPaginator } from '@angular/material';

import { Subscription } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';

import { LineItem } from '../../shared/models/line-item-model';
import { InvoiceItem } from '../../shared/models/invoice-item-model';

import { InvoicesInDetailCommService } from '../shared/services/invoices-in-detail-comm.service';
import { LineItemsService } from '../shared/services/line-items.service';
import { LoadingLineItems } from '../shared/services/loading-line-items.service';
import { LINE_ITEMS_FIELDS_DB_TO_UI_MATCH } from '../../shared/constants/db-fields-to-ui-mathces';
import { isNullOrUndefined } from 'util';

@Component({
  selector: 'app-line-items',
  templateUrl: './line-items.component.html',
  styleUrls: ['./line-items.component.css']
})
export class LineItemsComponent implements OnInit, OnDestroy, AfterViewInit {

  private subLineItems: Subscription;
  private nullMessage: string = '';
  public invoicesIdsSet: Set<string> = new Set();
  public invoicesIds: string[] = [];
  public loading: boolean = true;

  fleetData: LineItem[] = [];
  dataSource = new MatTableDataSource(this.fleetData);

  fullscreen: boolean = false;
  selectedRows: Set<LineItem> = new Set();
  public selectedInvoices: InvoiceItem[];
  dbToUiMatches = {...LINE_ITEMS_FIELDS_DB_TO_UI_MATCH};

  displayedColumns: string[] = [
    'invoice_no',
    'part_number',
    'description',
    'unit_price_llf',
    'quantity_llf',
    'discount',
    'invoice_net_amount_usd',
    'start_date',
    'end_date',
    'duration'
  ];

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  @Output() getLineItems: EventEmitter<any> = new EventEmitter<any>();

  constructor(
    private invoicesInDetailCommService: InvoicesInDetailCommService,
    private lineItemsService: LineItemsService,
    private loadingLineItems: LoadingLineItems) { }

  ngOnInit() {
    this.subLineItems = this.invoicesInDetailCommService.selectedInvoicesDataChannel$.pipe(
      tap((invoices: InvoiceItem[]) => this.selectedInvoices = Array.from(invoices)),
      switchMap((invoices: InvoiceItem[]) => {
        this.invoicesIds = this.getInvoicesIds();
         this.loading = true;
         this.loadingLineItems.publishLoadingLineItems(this.loading);
        return this.lineItemsService.getLetItemsByAribaId(this.invoicesIds);
    })
    )
    .subscribe(
        data => {
           this.loading = true;
           this.loadingLineItems.publishLoadingLineItems(this.loading);
          this.fleetData = data;
          if (isNullOrUndefined(this.fleetData) || this.fleetData.length == 0) {
            this.nullMessage = 'No data available';
          }
          else{
            this.nullMessage = 'Data present';
          }
          this.dataSource.data = this.fleetData;
          this.dataSource.filteredData = [...this.fleetData];
          this.getLineItems.emit(this.dataSource.filteredData);
          setTimeout(() => {
            this.loading = false;
            this.loadingLineItems.publishLoadingLineItems(this.loading); 
          }, 500);

        }
    )
  }

  initInvoicesIdsSet() {
    this.selectedInvoices.map(invoice => {
      this.invoicesIdsSet.add(invoice['ariba_doc_id']);
    });
  }

  getInvoicesIds() {
    this.invoicesIdsSet.clear();
    this.initInvoicesIdsSet();
    return Array.from(this.invoicesIdsSet);
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    // setTimeout(() => {
    //   this.loading = false;
    // }, 2000);
  }

  expand() {
    this.fullscreen = !this.fullscreen;
  }

  ngOnDestroy() {
    this.subLineItems.unsubscribe();
  }

}

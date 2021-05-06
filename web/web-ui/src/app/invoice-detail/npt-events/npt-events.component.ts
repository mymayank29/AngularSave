import { Component, ViewChild, OnInit, OnDestroy, AfterViewInit, Output, EventEmitter } from '@angular/core';
import { MatTableDataSource } from '@angular/material';
import { MatSort, MatPaginator } from '@angular/material';

import { Subscription } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';

import { NPTDetail } from '../../shared/models/npt-detail-model';
import { InvoiceItem } from '../../shared/models/invoice-item-model';

import { InvoicesInDetailCommService } from '../shared/services/invoices-in-detail-comm.service';
import { NptDetailsService } from '../shared/services/npt-details.service';
import { NPT_FIELDS_DB_TO_UI_MATCH } from '../../shared/constants/db-fields-to-ui-mathces';
import { deleteId } from '../../shared/helpers/delete-id';
import { isNullOrUndefined } from 'util';
import { LoadingNptEvent } from '../shared/services/loading-npt-events.service';

@Component({
  selector: 'app-npt-events',
  templateUrl: './npt-events.component.html',
  styleUrls: ['./npt-events.component.css']
})
export class NptEventsComponent implements OnInit, OnDestroy, AfterViewInit {

  private subNPTs: Subscription;
  private nullMessage: string = '';
  public loading: boolean = true;
  public invoicesIdsSet: Set<string> = new Set();
  public invoicesIds: string[] = [];
  public selectedInvoices: InvoiceItem[];
  fleetData: NPTDetail[] = [];
  dataSource = new MatTableDataSource(this.fleetData);

  fullscreen: boolean = false;
  dbToUiMatches = {...NPT_FIELDS_DB_TO_UI_MATCH};

  displayedColumns: string[] = [
    'invoice_no',
    'ref_no',
    'typ',
    'npt_date_start',
    'npt_date_end',
    'duration',
    'npt_type_detail_description',
    'com'
  ];

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  @Output() getNptEvents: EventEmitter<any> = new EventEmitter<any>();

  constructor(
    private invoicesInDetailCommService: InvoicesInDetailCommService,
    private nptDetailsService: NptDetailsService,
    private loadingNptEvent: LoadingNptEvent) { }

  ngOnInit() {
    const toggles = JSON.parse(sessionStorage.getItem('invoicesDetailsToggles'));
    this.subNPTs = this.invoicesInDetailCommService.selectedInvoicesDataChannel$.pipe(
      tap((invoices: InvoiceItem[]) => this.selectedInvoices = Array.from(invoices)),
      switchMap((invoices: InvoiceItem[]) => {
        this.invoicesIds = this.getInvoicesIds();
        this.loading = true;
        this.loadingNptEvent.publishLoadingNptEvents(this.loading);
        return this.nptDetailsService.getNPTsByAribaId(this.invoicesIds, toggles.isWeather, toggles.isMatchByDate);
      })
    )
    .subscribe(
        data => {
          this.loading = true;
          this.loadingNptEvent.publishLoadingNptEvents(this.loading);
          this.fleetData = data;
          if (isNullOrUndefined(this.fleetData) || this.fleetData.length == 0) {
            this.nullMessage = 'No data available';
          }
          else{
            this.nullMessage = 'Data present';
          }
          this.dataSource.data = this.fleetData;
          this.dataSource.filteredData = [...this.fleetData];
          this.getNptEvents.emit( this.dataSource.filteredData);
           setTimeout(() => {
            this.loading = false;
            this.loadingNptEvent.publishLoadingNptEvents(this.loading);
          }, 500);
        }
    );
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
    setTimeout(() => {
      this.loading = false;
    }, 500);
  }

  expand() {
    this.fullscreen = !this.fullscreen;
  }

  ngOnDestroy() {
    this.subNPTs.unsubscribe();
  }

}

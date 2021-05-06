import * as InvoicesActions from '../+store/invoices/invoices.actions';
import { InvoiceItem } from './../shared/models/invoice-item-model';
import { Component, OnInit, ViewChild } from '@angular/core';
import { AppState } from '../+store/app.state';
import { Store } from '@ngrx/store';
import { getInvoicesData } from '../+store/invoices/invoices.selectors';
import { InvoicesDetailsActions } from '../+store/invoices-details/invoices-details.actions';
import * as XLSX from 'xlsx';
import { LoadingLineItems } from './shared/services/loading-line-items.service';
import { Subscription } from 'rxjs';
import { LoadingNptEvent } from './shared/services/loading-npt-events.service';
import { STATUSES } from '../shared/constants/statuses';
import { Status } from '../shared/models/status-interface';
import { InvoicesInDetailCommService } from './shared/services/invoices-in-detail-comm.service';
import { isNullOrUndefined } from 'util';
import { InvoicesInDetailComponent } from './invoices-in-detail/invoices-in-detail.component';

@Component({
  selector: 'app-invoice-detail',
  templateUrl: './invoice-detail.component.html',
  styleUrls: ['./invoice-detail.component.css']
})
export class InvoiceDetailComponent implements OnInit {
  public invoicesData: Array<any>;
  public lineItemsData: Array<any>;
  public nptEventsData: Array<any>;
  private LoadingLineItemSub: Subscription;
  public loadLineItems: boolean  = true;
  public loadNptEvents: boolean = true;
  public statuses: Status[] = STATUSES;
  public selectedRows: Set<InvoiceItem> = new Set();
  public invoiceStatusSelected: any = null;

  @ViewChild(InvoicesInDetailComponent) invoiceInDetail: InvoicesInDetailComponent;

  constructor(private store: Store<AppState>, 
              private loadingLineItems: LoadingLineItems,
              private loadingNptEvent: LoadingNptEvent,
              private invoicesInDetailCommService: InvoicesInDetailCommService) { }

  ngOnInit() {
        this.loadingLineItems.selectedloadingLineItems.subscribe(
          (loading) => {
            this.loadLineItems = loading;
          }
        );

        this.loadingNptEvent.selectedloadingNptEvent.subscribe(
          (loading) => {
            this.loadNptEvents = loading;
          }
        );

        this.invoicesInDetailCommService.selectedInvoicesDataChannel$.subscribe(
          (invoices) => {
            // console.log(invoices);
            if (invoices !== null) {
              this.selectedRows.clear();
              invoices.forEach((inv) => {
                this.selectedRows.add(inv);
              });
            }
          }
        );
  }

  public changeStatus(status: string) {
    // this.invoiceStatusSelected = null;
    // console.log(Array.from(this.selectedRows));
    this.selectedRows.forEach((inv) => {
      inv.status = status;
      this.updateLeakagesByStatus(status,inv);
    });
    // this.updateInvoice.emit([this.selectedInvoices[0]]);
    this.onInvoiceUpdate(Array.from(this.selectedRows));
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

  public onInvoiceUpdate(invoices: InvoiceItem[]): void {
    this.store.dispatch(new InvoicesActions.UpdateInvoice(invoices));
  }

  public setInvoiceData(data) {
    this.invoicesData = this.createInvoicesTab(data);
  }

  public setLineItemsData(data) {
    this.lineItemsData = this.createLineItemsTab(data);
  }

  public setNptEventsData(data) {
    this.nptEventsData = this.createNptEventsTab(data);
  }

  private createInvoicesTab(data) {
    const invoices = data.map((obj) => ({
     
     
      'Supplier': obj['parent_vendor'],
      'Well': obj['wellname'],
      'WBS #': obj['afe'],
      'Contract Owner Name': obj['contract_owner_name'],
      'Contract ID': obj['contract_id'],
      'Ariba Doc ID': obj['ariba_doc_id'],
      'Net Total': obj['price'],
      'Submit Date': obj['submit_date'],
      'Approved Date': obj['approved_date'],
      'Work Start Date': obj['work_start_date'],
      'Work End Date': obj['work_end_date'],
      'NPT ref no': obj['npt_ref_no'],
      'NPT Duration': obj['npt_duration'],
      'Status': obj['status'],
      'Leakage Identified': obj['pt_spent_leakage_npt'],
      'Leakage Recovered': obj['recovered'],
      'Comments': obj['comment'],
      'Modified By': obj['modified_by'],
      'Modified Date': obj['modified_date'],
     // 'Job Type': obj['jobtyp'],
     // 'NPT': obj['npt_event_no'],
      
      
      
      
      
      
      // 'Leakage Confirmed': obj['spent_leakage_confirmed'],
     
     // 'NPT Start': obj['npt_date_start'],
     // 'NPT End': obj['npt_date_end'],
     
     
   //   'Service Type': obj['servicetyp'],
    //  'NPT Type Detail Desc': obj['npt_type_detail_desc'],
   //   'NPT Desc': obj['npt_desc'],
   //   'NPT Type': obj['npt_type'],
  //    'NPT Type Detail': obj['npt_type_detail'],
    //  'Contract Title': obj['contract_title'],
     
     
      
      
      
     
     
    //  'Days Since NPT': obj['days_since_npt']
    }));
    return invoices;
  }

  private createLineItemsTab(data) {
    if(isNullOrUndefined(data)) {
      const lineItems = {
        'Contract Title': '',
        'Invoice No': '',
        'Part': '',
        'Description': '',
        'Unit Price': '',
        'QTY': '',
        'Discount': '',
        'Net Price': '',
        'Work Start':'',
        'Work Stop': '',
     //   'Duration': ''
      };
      return lineItems;
    }
    else {
    const lineItems = data.map((obj) => ({
      'Contract Title': obj['contract_title'],
      'Invoice No': obj['invoice_no'],
      'Part': obj['part_number'],
      'Description': obj['description'],
      'Unit Price': obj['unit_price_llf'],
      'QTY': obj['quantity_llf'],
      'Discount': obj['discount'],
      'Net Price': obj['invoice_net_amount_usd'],
      'Work Start': obj['start_date'],
      'Work Stop': obj['end_date'],
     // 'Duration': obj['duration']
    }));
    return lineItems;
  }
  }

  private createNptEventsTab(data) {
    const nptEvents = data.map((obj) => ({
      'Well': obj['well_name'],
      'Invoice No': obj['invoice_no'],
      'Service Type': obj['service_typ'],
      'NPT#': obj['ref_no'],
      'NPT Type': obj['typ'],
      'Start': obj['npt_date_start'],
      'End': obj['npt_date_end'],
      'Duration (Hr)': obj['duration'],
      'Description': obj['com'],
      'Relation Restricrion': obj['npt_type_detail_description'],
      'Rig/Unit': obj['rig_no']
     
    }));
    return nptEvents;
  }

  public exportToExcel(): void {
    this.invoiceInDetail.ngAfterViewInit();
    const invoices_sheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(this.invoicesData);
    const lineItems_sheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(this.lineItemsData);
    const nptEvents_sheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(this.nptEventsData);
    invoices_sheet['!cols'] = [{wpx: 150}];
    const workbook: XLSX.WorkBook = {
      Sheets: {
        'Invoices': invoices_sheet,
        'Line_Items': lineItems_sheet,
        'NPT_Events': nptEvents_sheet
      },
      SheetNames: ['Invoices', 'Line_Items', 'NPT_Events']
    };
    XLSX.writeFile(workbook, 'Invoice details.xlsx');
  }

}

import { Component, OnInit, OnDestroy, EventEmitter, Output } from '@angular/core';

import { Status } from '../../shared/models/status-interface';
import { STATUSES } from '../../shared/constants/statuses';
import { InvoiceItem } from '../../shared/models/invoice-item-model';
import { Subscription } from 'rxjs';
import { InvoicesInDetailCommService } from '../shared/services/invoices-in-detail-comm.service';
import { InvoiceService } from '../../invoices/shared/services/invoice.service';


@Component({
  selector: 'app-review',
  templateUrl: './review.component.html',
  styleUrls: ['./review.component.css']
})
export class ReviewComponent implements OnInit, OnDestroy {

  private subInvoice: Subscription;
  private subInvoiceStatusChange: Subscription;

  @Output() updateInvoice: EventEmitter<InvoiceItem[]> = new EventEmitter<InvoiceItem[]>();
  public invoiceStatusSelected: any = null;
  public selectedInvoices: InvoiceItem[];
  public statuses: Status[] = STATUSES;

  constructor(private invoiceService: InvoiceService,
              private invoicesInDetailCommService: InvoicesInDetailCommService) {}

  ngOnInit() {
    this.subInvoice = this.invoicesInDetailCommService.selectedInvoicesDataChannel$
      .subscribe(invoices => {
        this.selectedInvoices = invoices;
        this.invoiceStatusSelected = null;
      });
  }

  ngOnDestroy() {
    this.subInvoice.unsubscribe();
    if (this.subInvoiceStatusChange) {
      this.subInvoiceStatusChange.unsubscribe();
    }
  }

  changeStatus(status: string) {
    this.invoiceStatusSelected = status;
    this.selectedInvoices[0].status = status;
    this.updateLeakagesByStatus(status, this.selectedInvoices[0]);
    this.updateInvoice.emit([this.selectedInvoices[0]]);
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

  submitChanges(total: HTMLInputElement, recovered: HTMLInputElement, comment: HTMLInputElement) {
    let changed = false;
    if (this.invoiceStatusSelected !== null && this.invoiceStatusSelected !== this.selectedInvoices[0].status) {
      this.selectedInvoices[0].status = this.invoiceStatusSelected;
      changed = true;
    }
    if (total.value !== '' && this.selectedInvoices[0].spent_leakage_confirmed !== +total.value) {
      let leakageConfirmedNumber = +total.value;
      if (leakageConfirmedNumber < 0) {
        leakageConfirmedNumber = 0;
      }
      this.selectedInvoices[0].spent_leakage_confirmed = leakageConfirmedNumber;
      changed = true;
    }
    if (recovered.value !== '' && this.selectedInvoices[0].recovered !== +recovered.value) {
      let recoveredNumber = +recovered.value;
      if (recoveredNumber < 0) {
        recoveredNumber = 0;
      }
      this.selectedInvoices[0].recovered = recoveredNumber;
      this.selectedInvoices[0].status = this.statuses[3].value;
      changed = true;
    }
    if (this.selectedInvoices[0].comment == undefined) {
      this.selectedInvoices[0].comment = '';
    }
    if (this.selectedInvoices[0].comment !== comment.value.trim()) {
      this.selectedInvoices[0].comment = comment.value.trim();
      changed = true;
    }
    if (changed) {
      this.subInvoiceStatusChange = this.invoiceService.updateInvoice([this.selectedInvoices[0]]).subscribe(
        () => {},
        error => console.log(error)
      );
    }
  }

  onInputFocus(event) {
    if (event.target.type === 'number' && event.target.value === '0') {
      event.target.value = '';
    }
  }



}

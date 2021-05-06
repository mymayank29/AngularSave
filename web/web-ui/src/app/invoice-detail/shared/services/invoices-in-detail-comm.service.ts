import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

import { InvoiceItem } from '../../../shared/models/invoice-item-model';

@Injectable({
  providedIn: 'root'
})
export class InvoicesInDetailCommService {

  private firstRow: InvoiceItem;
  private selectedInvoicesDataChannel = new BehaviorSubject<InvoiceItem[]>(null);

  public selectedInvoicesDataChannel$ = this.selectedInvoicesDataChannel.asObservable();

  publishSelectedInvoices(invoices: InvoiceItem[]) {
    this.selectedInvoicesDataChannel.next(invoices);
  }

}

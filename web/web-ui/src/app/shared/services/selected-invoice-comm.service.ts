import { Injectable } from '@angular/core';
import { Subject, BehaviorSubject } from 'rxjs';
import { InvoiceItem } from '../models/invoice-item-model';

@Injectable({
  providedIn: 'root'
})
export class SelectedInvoiceCommService {

  private selectedInvoicesDataChannel = new BehaviorSubject<InvoiceItem[]>([]);
  public selectedInvoicesDataChannel$ = this.selectedInvoicesDataChannel.asObservable();
  publishSelectedInvoices(data: InvoiceItem[]) {
    this.selectedInvoicesDataChannel.next(data);
  }

}

import { Component, OnInit } from '@angular/core';
import { InvoiceService } from '../invoices/shared/services/invoice.service';
import { HttpClient, HttpParams, HttpErrorResponse, HttpHeaders } from '@angular/common/http';



@Component({
  selector: 'app-count',
  templateUrl: './count.component.html',
  styleUrls: ['./count.component.css']
})

export class CountComponent implements OnInit {
	
  count: String;
  params: HttpParams;

  constructor(private invoiceservice: InvoiceService) { }

  ngOnInit() {
	  this.invoiceservice.getRecordCount(this.params).subscribe(data => {
	      this.count = data;
	  });

}
}
 
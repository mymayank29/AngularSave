import { Component, ViewChild, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material';
import { MatSort, MatPaginator } from '@angular/material';

import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { NPTDetail } from '../../shared/models/npt-detail-model';

import { ConvertToCSVService } from '../../shared/services/convert-to-csv.service';
import { deleteId } from '../../shared/helpers/delete-id';
import { InvoicesInDetailCommService } from '../shared/services/invoices-in-detail-comm.service';
import { NptDetailsService } from '../shared/services/npt-details.service';

@Component({
  selector: 'app-contract-restrictions',
  templateUrl: './contract-restrictions.component.html',
  styleUrls: ['./contract-restrictions.component.css']
})
export class ContractRestrictionsComponent implements OnInit, OnDestroy, AfterViewInit {

  private subNPTs: Subscription;

  fleetData: NPTDetail[] = [];
  dataSource = new MatTableDataSource(this.fleetData);

  fullscreen: boolean = false;

  displayedColumns: string[] = [
    // 'npt_event_no',
    // 'typ',
    // 'com'
  ];

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(
    private convertToCSVService: ConvertToCSVService,
    private invoicesInDetailCommService: InvoicesInDetailCommService,
    private nptDetailsService: NptDetailsService,
    private router: Router) { }

  ngOnInit() {
    // this.subNPTs = this.invoicesInDetailCommService.selectedInvoicesDataChannel$.pipe(
    //   switchMap(nptRow => this.nptDetailsService.getNPTsByAribaId(nptRow.ariba_doc_id))
    // )
    // .subscribe(
    //     data => {
    //       this.fleetData = data;
    //       this.dataSource.data = this.fleetData;
    //       this.dataSource.filteredData = [...this.fleetData];
    //     }
    // )
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }


  expand() {
    this.fullscreen = !this.fullscreen;
  }

  navigateToContract() {
    this.router.navigateByUrl('/contract-detail');
  }

  saveCsvFile() {
    const csvArray = this.convertToCSVService.convertJsonToCSV(deleteId(this.dataSource.filteredData));
    const blob = new Blob([csvArray], { type: 'text/csv' });
    this.convertToCSVService.saveFile('contracts.csv', blob);
  }

  ngOnDestroy() {
    // this.subNPTs.unsubscribe();
  }

}

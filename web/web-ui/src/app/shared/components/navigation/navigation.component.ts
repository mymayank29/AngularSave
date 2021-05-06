import { Component, Output, EventEmitter } from '@angular/core';
import { MENU_LIST } from '../../constants/menu-list';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent {

  @Output() goToInvoiceDetail: EventEmitter<any> = new EventEmitter<any>();

  public menuList = MENU_LIST;

  constructor(private router: Router) {}

  navigateToInvoiceDetail() {
    this.goToInvoiceDetail.emit();
  }



}

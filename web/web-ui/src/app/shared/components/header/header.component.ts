import { Component } from '@angular/core';
import { AuthServiceMock } from '../../../shared/services/auth.service.mock';
import { MENU_LIST } from '../../constants/menu-list';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  public isAuthenticated = false;
  public menuList = MENU_LIST;

  constructor(public authService: AuthServiceMock) {}

}

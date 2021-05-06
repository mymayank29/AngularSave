import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, NavigationExtras } from '@angular/router';
import { AuthServiceMock } from '../../services/auth.service.mock';
import { IUser } from '../../models/user.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  message: string;
  user: IUser;
  loading = false;
  responseData: any;
  private sub: Subscription;

  constructor(
    public authService: AuthServiceMock,
    private router: Router) {}

  ngOnInit() {
    this.user = {};
  }

  ngOnDestroy() {
    if (this.sub) {
      this.sub.unsubscribe();
    }
  }

  // login() : void {
  //   this.message = 'Trying to login ...';
  //   this.authService.login(this.user).subscribe(
  //     data => {
  //       console.log(`login data is ${data}`)
  //       this.responseData = data;
  //       this.router.navigate([""]);
  //     },
  //     err => console.log(`onLogin err is ${err}`)
  //  );
  // }

  login() {
    this.sub = this.authService.login().subscribe(() => {
      if (this.authService.isLoggedIn) {
        // Get the redirect URL from our auth service
        // If no redirect has been set, use the default
        const redirect = this.authService.redirectUrl
          ? this.authService.redirectUrl
          : '/invoices';

          const navigationExtras: NavigationExtras = {
            queryParamsHandling: 'preserve',
            preserveFragment: true
          };

          // Redirect the user
          this.router.navigate([redirect], navigationExtras);
      }
    });
  }

  logout() {
    this.authService.logout();
  }

  cancel(): void {
    this.router.navigate(['']);
  }


}

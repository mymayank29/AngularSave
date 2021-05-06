import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InvoicesInDetailComponent } from './invoices-in-detail.component';
import { SharedModule } from '../../shared/shared.module';
import { AppRoutingModule } from '../../app-routing.module';
import { InvoicesModule } from 'src/app/invoices/invoices.module';
import { HttpClientModule } from '@angular/common/http';
import { MainStoreModule } from '../../+store/main-store.module';

describe('InvoicesComponent', () => {
  let component: InvoicesInDetailComponent;
  let fixture: ComponentFixture<InvoicesInDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        AppRoutingModule,
        InvoicesModule,
        MainStoreModule,
        HttpClientModule
      ],
      declarations: [ InvoicesInDetailComponent ]
    })
    .compileComponents();
  }));


  it('should create', () => {
    fixture = TestBed.createComponent(InvoicesInDetailComponent);
    component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});

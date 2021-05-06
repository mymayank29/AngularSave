import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LineItemsComponent } from './line-items.component';
import { AppRoutingModule } from '../../app-routing.module';
import { SharedModule } from '../../shared/shared.module';
import { InvoicesModule } from '../../invoices/invoices.module';
import { HttpClientModule } from '@angular/common/http';
import { MainStoreModule } from '../../+store/main-store.module';

describe('LineItemsComponent', () => {

  let component: LineItemsComponent;
  let fixture: ComponentFixture<LineItemsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        AppRoutingModule,
        InvoicesModule,
        MainStoreModule,
        HttpClientModule
      ],
      declarations: [ LineItemsComponent ]
    })
    .compileComponents();
  }));

  it('should create', () => {
    fixture = TestBed.createComponent(LineItemsComponent);
    component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});

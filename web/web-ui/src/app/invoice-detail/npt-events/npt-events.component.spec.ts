import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NptEventsComponent } from './npt-events.component';
import { AppRoutingModule } from '../../app-routing.module';
import { SharedModule } from '../../shared/shared.module';
import { InvoicesModule } from '../../invoices/invoices.module';
import { MainStoreModule } from '../../+store/main-store.module';
import { HttpClientModule } from '@angular/common/http';

describe('NptEventsComponent', () => {

  let component: NptEventsComponent;
  let fixture: ComponentFixture<NptEventsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        AppRoutingModule,
        InvoicesModule,
        MainStoreModule,
        HttpClientModule
      ],
      declarations: [ NptEventsComponent ]
    })
    .compileComponents();
  }));

  it('should create', () => {
    fixture = TestBed.createComponent(NptEventsComponent);
    component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});

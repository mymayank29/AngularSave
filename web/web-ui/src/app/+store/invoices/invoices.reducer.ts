import { InvoicesActions, InvoicesActionTypes } from './invoices.actions';
import { InvoicesState, initialInvoicesState } from './invoices.state';
import { InvoiceItem } from '../../shared/models/invoice-item-model';
export function invoicesReducer(state: InvoicesState = initialInvoicesState, action: InvoicesActions): InvoicesState {
  // case must be return new state(object that build from copy of old state and changes in this object)
  switch (action.type) {
    case InvoicesActionTypes.GET_INVOICES: {
      return {
        ...state,
         loading: true
        };
    }
    case InvoicesActionTypes.GET_INVOICES_SUCCESS: {
      const data = [ ...(<InvoiceItem[]>action.payload) ];
      return {
        ...state,
        data,
        loading: false,
        loaded: true
       };
    }
    case InvoicesActionTypes.GET_INVOICES_ERROR: {
      const error = action.payload;
      return {
        ...state,
        loading: false,
        loaded: false,
        error
      };
    }
    case InvoicesActionTypes.UPDATE_INVOICE: {
      return { ...state };
    }
    case InvoicesActionTypes.UPDATE_INVOICE_SUCCESS: {
      var invoiceItems = [];
      if (action.payload) {
        action.payload.forEach(element => {
          if (element) {
            invoiceItems = invoiceItems.concat(element.payload);
          }
        });
      }
      const updatedData = invoiceItems;
      return {
        ...state,
        // data,
        updatedData
       };
    }
    case InvoicesActionTypes.UPDATE_INVOICE_ERROR: {
      const error = action.payload;
      return {
        ...state,
        error
       };
    }
    default: {
      return state;
    }
  }
}

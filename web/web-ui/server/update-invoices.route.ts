import { Request, Response } from 'express';
import { INVOICES } from './data_base/db-data-invoices';
import { findIndex } from 'rxjs/operators';

export function updateInvoiceById(req: Request, res: Response) {
  const invoiceIds = req.params['id'].split(',');
  console.log(req.body);
  console.log(invoiceIds);
  let i = 0;
  invoiceIds.map(id => {
    const index = INVOICES.findIndex(invoice => invoice.id === id);
    INVOICES[index] = req.body[i];
    i++;
  });
  res.status(200).json(INVOICES);
}

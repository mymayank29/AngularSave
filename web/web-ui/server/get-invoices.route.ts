import { Request, Response } from 'express';
import { INVOICES } from './data_base/db-data-invoices';

export function getInvoiceById(req: Request, res: Response) {
  const invoiceId = req.params['id'];
  const invoice = INVOICES.find(invoice => invoice.id === invoiceId);
  res.status(200).json(invoice);
}

export function putInvoiceById(req: Request, res: Response) {
  setTimeout(() => {
    res.status(200).json(req.body);
  }, 200);
}

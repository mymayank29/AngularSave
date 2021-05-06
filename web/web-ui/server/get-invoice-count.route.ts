import { Request, Response } from 'express';
import { INVOICES } from './data_base/db-data-invoices';

export function getinvoicecount(req: Request, res: Response) {
console.log("hitting invoices count");
//const invoiceCount = INVOICES.length;
//const response = {count: 5000};
setTimeout(()=>{
    res.status(200).json({count: 5000});
}, 500);

}

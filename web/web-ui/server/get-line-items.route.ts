import { Request, Response } from 'express';
import { LINE_ITEMS } from './data_base/db-data-line-items';

export function getLineItemsByAribaId(req: Request, res: Response) {
    console.log(req.params['ariba_doc_id']);
    const aribaDocIds: string[] = req.params['ariba_doc_id'].split(',');
    const lineItems: any[] = [];
    aribaDocIds.map(id => {
      lineItems.push(...LINE_ITEMS[id]);
    });

    const response = {count: lineItems.length, payload: lineItems};

    setTimeout(() => {
        res.status(200).json(response);
    }, 500);
}

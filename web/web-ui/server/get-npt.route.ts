import { Request, Response } from 'express';
import { NPT } from './data_base/db-data-line-items';

export function getNPTsByAribaId(req: Request, res: Response) {

  console.log(req.params['ariba_doc_id']);
  const aribaDocIds: string[] = req.params['ariba_doc_id'].split(',');
  const npts: any[] = [];
  aribaDocIds.map(id => {
    npts.push(...NPT[id]);
  });

  const response = {count: npts.length, payload: npts};

    setTimeout(() => {
        res.status(200).json(response);
    }, 1000);
}

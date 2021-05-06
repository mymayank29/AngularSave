import { Request, Response } from 'express';
import { FLAGGED_INVOICES } from './data_base/db-data-flagged-invoices';

export function getFlaggedInvoices(req: Request, res: Response) {

    setTimeout(() => {
        res.status(200).json({payload: FLAGGED_INVOICES});
    }, 200);
}

export function getFlaggedInvoicesByParams(req: Request, res: Response) {
  const queryParams = req.query;
  // const filterIsWeather = queryParams.isweather || 'false';
  // const invoicesFilterIsweather = FLAGGED_INVOICES.filter(invoice => invoice.isweather.toString() === filterIsWeather.toString());
  const filterIsLowImpact = queryParams.islowimpact || 'true';
  const nptDurationLow = queryParams.nptdurationlow;
  const nptDurationHigh = queryParams.nptdurationhigh;
  const invoicesFilterIsLowImpact = FLAGGED_INVOICES.filter(invoice => {
      if (filterIsLowImpact.toString() === 'true') {
          return invoice.npt_duration >= 5;
      } else {
          return invoice;
      }
  });
  const invoicesByAllFilters = invoicesFilterIsLowImpact.filter(invoice => {
    if ((invoice.npt_duration > nptDurationLow) && (invoice.npt_duration <= nptDurationHigh)) {
      return invoice;
    }
  });

  setTimeout(() => {
      res.status(200).json({payload: invoicesByAllFilters});
  }, 300);
}

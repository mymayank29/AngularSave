import { Request, Response } from 'express';
import { INVOICES } from './data_base/db-data-invoices';
import { setTimeout } from 'timers';

export function searchInvoices(req: Request, res: Response) {
    const queryParams = req.query;
    const filter = queryParams.filter || '',
          sortOrder = queryParams.sortOrder,
          pageNumber = parseInt(queryParams.pageNumber) || 0,
          pageSize = parseInt(queryParams.pageSize);
    let invoices = INVOICES.sort((inv1, inv2) => inv1.id - inv2.id);

    if (filter) {
        const values = invoices.map(invoice => Object.values(invoice)).map(el => el.toString());
        const filteredAll = values.filter(el => el.trim().toLowerCase().search(filter.toLowerCase()) >= 0 );
        const indexes = filteredAll.map(el => el.split(',')).map(el => el[0]);
        invoices = invoices.filter(invoice => indexes.some(el => el === invoice.id));
     }

    if (sortOrder === 'desc') {
        invoices = invoices.reverse();
    }

    const initialPos = pageNumber * pageSize;
    const invoicesPage = invoices.slice(initialPos, initialPos + pageSize);

    setTimeout(() => {
        res.status(200).json({payload: invoicesPage});
    }, 500);
}

export function searchInvoicesByWeather(req: Request, res: Response) {
    console.log("hittiong all invoice records");
    const queryParams = req.query;
    const filterIsWeather = queryParams.isweather || 'false';
    const invoicesFilterIsweather = INVOICES.filter(invoice => {
        if(filterIsWeather.toString() == 'Y' || filterIsWeather.toString() == 'N'){
            return invoice.is_weather.toString() === filterIsWeather.toString();
        }
        return invoice;
    });
    const filterIsLowImpact = queryParams.islowimpact || 'true';
    const nptDurationLow = queryParams.nptdurationlow;
    const nptDurationHigh = queryParams.nptdurationhigh;
    const invoicesFilterIsLowImpact = invoicesFilterIsweather.filter(invoice => {
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

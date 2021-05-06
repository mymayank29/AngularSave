
import * as express from 'express';
import { Application } from 'express';
import { getInvoiceById, putInvoiceById } from './get-invoices.route';
import { getLineItemsByAribaId } from './get-line-items.route';
import { getNPTsByAribaId } from './get-npt.route';
import { searchInvoices, searchInvoicesByWeather } from './search-invoices.route';
import { getFlaggedInvoices, getFlaggedInvoicesByParams } from './get-flagged-npt.route';
import { updateInvoiceById } from './update-invoices.route';
import * as bodyParser from 'body-parser';
import { getinvoicecount } from './get-invoice-count.route';

const app: Application = express();

// Parsers for POST data
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

app.use(function (req, res, next) {
    const allowedOrigins = ['http://localhost:4200', 'http://localhost:3000', 'http://localhost:8000'];
    const origin = req.headers.origin.toString();
    if (allowedOrigins.indexOf(origin) > -1) {
        res.setHeader('Access-Control-Allow-Origin', origin);
    }
    res.setHeader(
      'Access-Control-Allow-Methods',
      'GET, POST, OPTIONS, PUT, PATCH, DELETE'
    );
    res.setHeader(
      'Access-Control-Allow-Headers',
      'X-Requested-With,content-type'
    );
    next();
});

app.route('/api/invoices').get(searchInvoicesByWeather);
app.route('/api/invoices/search').get(searchInvoices);

app.route('/api/invoices/:id').get(getInvoiceById);
app.route('/api/invoices/:id').put(putInvoiceById);

app.route('/api/line-items/:ariba_doc_id').get(getLineItemsByAribaId);
app.route('/api/npt/:ariba_doc_id').get(getNPTsByAribaId);
app.route('/api/flagged-invoices').get(getFlaggedInvoicesByParams);

app.route('/api/invoices/update/:id').put(updateInvoiceById);
app.route('/api/invoices/getinvoicecount').get(getinvoicecount);

const httpServer = app.listen(13080, () => {
    console.log('HTTP REST API Server running at http://localhost:' + httpServer.address().port);
});

export interface LineItem {
    id: string;
    part_number: string;
    description: string;
    unit_price_llf: number;
    quantity_llf: number;
    discount: number;
    invoice_net_amount_usd: number;
    start_date: string;
    end_date: string;
    duration: number;
}

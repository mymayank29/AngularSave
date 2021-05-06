package com.chevron.edap.gomica.util;

import com.chevron.edap.gomica.dto.LineItemDto;
import com.chevron.edap.gomica.model.LineItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.DAYS;


public class LineItemConverter {


    public static LineItemDto convertLineItem(LineItem lineItem) {

        LineItemDto lineItemDto = new LineItemDto();
        lineItemDto.setAriba_doc_id(lineItem.getAribaDocId());
        lineItemDto.setInvoice_no(lineItem.getAribaDocId());
        lineItemDto.setId(lineItem.getBusiness_key());
        lineItemDto.setPart_number(lineItem.getPart_number());
        lineItemDto.setDescription(lineItem.getDescription());
        lineItemDto.setUnit_price_llf(Float.valueOf(lineItem.getUnit_price_llf()));
        lineItemDto.setQuantity_llf(Float.valueOf(lineItem.getQuantity_llf()));
        lineItemDto.setDiscount(Float.valueOf(lineItem.getDiscount()));
        lineItemDto.setInvoice_net_amount_usd(Float.valueOf(lineItem.getInvoice_net_amount_usd()));

        String endDate = convertIntToDateString(lineItem.getEnd_date());
        String startDate = convertIntToDateString(lineItem.getStart_date());

        lineItemDto.setEnd_date(endDate);
        lineItemDto.setStart_date(startDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate startDate_local = LocalDate.parse(startDate, formatter);
        LocalDate endDate_local = LocalDate.parse(endDate, formatter);

        lineItemDto.setDuration(DAYS.between(startDate_local,endDate_local));

        lineItemDto.setContract_title(lineItem.getContractName());

        return lineItemDto;
    }

    private static String convertIntToDateString(int date) {
        try {
            String parsedDate = String.valueOf(date);
            if (parsedDate.length() != 8) {
                return "unable to parse date";
            } else {
                return parsedDate.substring(0, 4) + "-" +
                        parsedDate.substring(4, 6) + "-" +
                        parsedDate.substring(6) + " " + "00:00:00";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "unable to parse date";
        }
    }
}

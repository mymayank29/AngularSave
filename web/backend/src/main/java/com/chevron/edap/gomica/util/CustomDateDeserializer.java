package com.chevron.edap.gomica.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CustomDateDeserializer extends JsonDeserializer<Date> {


    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String dateString = jsonParser.getText();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        try {

            java.util.Date utDate = sdf.parse(dateString);
            Date parsed = new Date(utDate.getTime());
            return parsed;

        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return null;
    }
}

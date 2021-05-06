package com.chevron.edap.gomica.controller;

import com.chevron.edap.gomica.dto.InvoiceDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class Test {
    static class STest{
        String f1 = "la1";
        String f2 = "la12";
        Double d1 = 3.4;

        public String getF1() {
            return f1;
        }

        public void setF1(String f1) {
            this.f1 = f1;
        }

        public String getF2() {
            return f2;
        }

        public void setF2(String f2) {
            this.f2 = f2;
        }

        public Double getD1() {
            return d1;
        }

        public void setD1(Double d1) {
            this.d1 = d1;
        }
    }

    public static void main(String[] args){
        List<STest> stest = new ArrayList<>();
        stest.add(new STest());
        stest.add(new STest());

        Gson gson =new Gson();
        JsonArray result = gson.toJsonTree(stest, new TypeToken<List<STest>>(){}.getType()).getAsJsonArray();
        JsonObject response = new JsonObject();
        response.add("payload", result);

        System.out.println(response.toString());
    }
}

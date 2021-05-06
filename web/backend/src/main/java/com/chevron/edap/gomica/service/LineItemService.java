package com.chevron.edap.gomica.service;

import com.chevron.edap.gomica.dto.InvoiceDto;
import com.chevron.edap.gomica.dto.LineItemDto;
import com.chevron.edap.gomica.model.LineItem;
import com.chevron.edap.gomica.repository.LineItemRepository;
import com.chevron.edap.gomica.util.LineItemConverter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineItemService {

    @Autowired
    LineItemRepository lineItemRepository;

    public String getLineItemsByAribaDocId(String aribaDocId) {
        List<LineItemDto> lineItems = lineItemRepository.findByAribaDocId(aribaDocId)
                .stream()
                .map(lineItem -> LineItemConverter.convertLineItem(lineItem))
                .collect(Collectors.toList());

        Gson gson =new Gson();
        JsonArray arr = gson.toJsonTree(lineItems, new TypeToken<List<LineItemDto>>(){}.getType())
                .getAsJsonArray();
        JsonObject object = new JsonObject();
        object.add("payload", arr);

        return object.toString();
    }

    public String getLineItemsByAribaDocIds(List<String> aribaDocIds) {
        List<LineItemDto> lineItems = lineItemRepository.findByAribaDocIdIn(aribaDocIds)
                .stream()
                .map(lineItem -> LineItemConverter.convertLineItem(lineItem))
                .collect(Collectors.toList());

        Gson gson =new Gson();
        JsonArray arr = gson.toJsonTree(lineItems, new TypeToken<List<LineItemDto>>(){}.getType())
                .getAsJsonArray();
        JsonObject object = new JsonObject();
        object.add("payload", arr);

        return object.toString();
    }
}

package com.chevron.edap.gomica.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.List;

import com.chevron.edap.gomica.dto.IntervalProblemDto;
import com.chevron.edap.gomica.repository.IntervalProblemRepository;

@Service
public class IntervalProblemService {
	

    @Autowired
    IntervalProblemRepository intervalProblemRepository;


    public String getNptsByAribaDocId(String aribaDocId) {
        List<IntervalProblemDto> lineItems = intervalProblemRepository.findByAribaDocId(aribaDocId)
                .stream()
                .map(intervalProblem -> new IntervalProblemDto(intervalProblem))
                .collect(Collectors.toList());

        Gson gson =new Gson();
        JsonArray arr = gson.toJsonTree(lineItems, new TypeToken<List<IntervalProblemDto>>(){}.getType())
                .getAsJsonArray();
        JsonObject object = new JsonObject();
        object.add("payload", arr);
        
//        logger.info("NPT Payload ==>", object.toString());


        return object.toString();
    }

    public String getNptsByAribaDocIds(List<String> aribaDocId, Boolean isMatchByDate, String isWeather) {
        List<IntervalProblemDto> lineItems = intervalProblemRepository.findByAribaDocIdIn(aribaDocId)
                .stream()
                .map(intervalProblem -> new IntervalProblemDto(intervalProblem))
                .collect(Collectors.toList());
        
        if (isMatchByDate) {
			lineItems = lineItems.stream()
				.filter(intervalProblem -> intervalProblem.getBy_date())
				.collect(Collectors.toList());
		}
        
        if ("N".equals(isWeather)) {
			lineItems = lineItems.stream()
					.filter(intervalProblem -> !intervalProblem.getIs_weather())
					.collect(Collectors.toList());
		} else if ("Y".equals(isWeather)) {
			lineItems = lineItems.stream()
					.filter(intervalProblem -> intervalProblem.getIs_weather())
					.collect(Collectors.toList());
		}

        Gson gson =new Gson();
        JsonArray arr = gson.toJsonTree(lineItems, new TypeToken<List<IntervalProblemDto>>(){}.getType())
                .getAsJsonArray();
        JsonObject object = new JsonObject();
        object.add("payload", arr);

        return object.toString();
    }
}

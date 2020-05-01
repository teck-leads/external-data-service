package com.external.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.external.app.model.LocationLiveTracker;
import com.external.app.service.ExternalCSVDataService;

@Controller
public class ExternalServiceController {
	@Autowired
	private ExternalCSVDataService externalCSVDataService;

	@GetMapping(value = "/")
	public String extrenalDataLiveTracker(Model model) throws Exception {
		List<LocationLiveTracker> externalCSVData = externalCSVDataService.getExternalCSVData();
		
		int totalCasesAsOfToday = externalCSVData.stream().
		mapToInt(stat->stat.getTotalLatestCases()).sum();
		
		int differenceFromPreviousDay = externalCSVData.stream().
		mapToInt(stat->stat.getDifferenceFromPreviousDay()).sum();
		
		model.addAttribute("totalKey", totalCasesAsOfToday);
		model.addAttribute("differenceFromPreviousDayKey", differenceFromPreviousDay);
		model.addAttribute("statsKey", externalCSVData);
		//model.addAttribute("statsKey", null);
		
		return "livetracker";
	}

}

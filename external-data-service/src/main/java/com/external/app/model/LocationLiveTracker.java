package com.external.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationLiveTracker {
	private String ProvinceState;
	private String CountryRegion;
	private Integer totalLatestCases;
	private Integer yesterDayTotalCases;
	private Integer differenceFromPreviousDay;
	

}

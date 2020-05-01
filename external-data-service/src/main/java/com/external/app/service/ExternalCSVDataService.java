package com.external.app.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.external.app.model.LocationLiveTracker;

@Service
public class ExternalCSVDataService {

	private final String EXTERNAL_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	private List<LocationLiveTracker> stats = new ArrayList<>();

	// @Scheduled(cron = "* * * * * *")
	@PostConstruct
	public List<LocationLiveTracker> getExternalCSVData() throws Exception {
		try {
			List<LocationLiveTracker> newstats = new ArrayList<>();
			HttpClient httpClient = HttpClient.newHttpClient();
			HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(EXTERNAL_URL)).build();

			HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
			Reader reader = new StringReader(httpResponse.body());
			Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
			boolean isNumberValueToday = false;
			boolean isNumberValuepreviousDay = false;
			for (CSVRecord record : records) {
				LocationLiveTracker stat = new LocationLiveTracker();
				String provinceState = record.get("Province/State");
				String countryRegion = record.get("Country/Region");
				String todayLatestCases = record.get(record.size() - 1);
				String yesterdayTotal = record.get(record.size() - 2);
				
				if (!StringUtils.isEmpty(todayLatestCases)&& todayLatestCases.matches("-?\\d+(\\.\\d+)?")) {
					stat.setTotalLatestCases(Integer.parseInt(todayLatestCases));
					isNumberValueToday = true;
				}
				if (!StringUtils.isEmpty(yesterdayTotal)&&yesterdayTotal.matches("-?\\d+(\\.\\d+)?")) {
					stat.setYesterDayTotalCases(Integer.parseInt(yesterdayTotal));
					isNumberValuepreviousDay = true;
				}
				stat.setProvinceState(provinceState);
				stat.setCountryRegion(countryRegion);

				if (isNumberValueToday && isNumberValuepreviousDay) {
					stat.setDifferenceFromPreviousDay(stat.getTotalLatestCases() - stat.getYesterDayTotalCases());
				}

				newstats.add(stat);
			}
			this.stats = newstats;
//			stats.forEach(stat -> {
//				System.out.println(stat.getProvinceState() + " <***> " + stat.getCountryRegion() + " <***> "
//						+ stat.getTotalLatestCases()+" <***> "+stat.getDifferenceFromPreviousDay());
//
//			});
			return stats;
		} catch (IOException e) {
			throw e;
		} catch (InterruptedException e) {
			throw e;
		}
	}

}

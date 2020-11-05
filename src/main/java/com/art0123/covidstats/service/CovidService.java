package com.art0123.covidstats.service;

import com.art0123.covidstats.mystats.MyStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class CovidService {
    private final String covidStatsRepo = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/";
    private final String newestCsv;
    private final String previousCsv;
    private final String newestUrl;
    private final String previousUrl;
    private String countryName;
    private final ZonedDateTime newestZdt;
    private final ZonedDateTime previousDayZdt;
    private List<MyStats> allStats = new ArrayList<>();
    private final ArrayList<Integer> prevDayDiff = new ArrayList<>();

    public CovidService() {
        this.newestZdt = ZonedDateTime.now().minusDays(1);
        this.previousDayZdt = ZonedDateTime.now().minusDays(2);
        this.newestCsv = this.newestZdt.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        this.previousCsv = this.previousDayZdt.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        this.newestUrl = this.covidStatsRepo + newestCsv + ".csv";
        this.previousUrl = this.covidStatsRepo + previousCsv + ".csv";
    }

    public void fetchData() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(newestUrl))
                .build();

        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        HttpClient secondClient = HttpClient.newHttpClient();
        HttpRequest secondRequest = HttpRequest.newBuilder()
                .uri(URI.create(previousUrl))
                .build();

        HttpResponse<String> secondHttpResponse = secondClient.send(secondRequest, HttpResponse.BodyHandlers.ofString());

        StringReader newestCsvReader = new StringReader(httpResponse.body());
        StringReader previousCsvReader = new StringReader(secondHttpResponse.body());
        
        Iterable<CSVRecord> previousRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(previousCsvReader);
        Iterable<CSVRecord> currentRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(newestCsvReader);

        List<MyStats> currentRecordList = new ArrayList<>();
        ArrayList<Integer> activeCasesList = new ArrayList<>();

        for (CSVRecord record : previousRecords) {
            MyStats stats = new MyStats();
            if (record.get("Country_Region").equals(countryName)) {
                stats.setCountry(record.get("Country_Region"));
                stats.setState(record.get("Province_State"));
                if (record.get("Active").isEmpty()) {
                    stats.setActiveCases(0);
                    activeCasesList.add(0);
                } else {
                    stats.setActiveCases(Integer.parseInt(record.get("Active")));
                    activeCasesList.add(stats.getActiveCases());
                }
            }
        }

        int i = 0;
        for (CSVRecord record : currentRecords) {
            MyStats stats = new MyStats();
            if (record.get("Country_Region").equals(countryName)) {
                stats.setCountry(record.get("Country_Region"));
                stats.setState(record.get("Province_State"));
                if (record.get("Active").isEmpty()) {
                    stats.setActiveCases(0);
                } else {
                    stats.setActiveCases(Integer.parseInt(record.get("Active")));
                    int setNum = Integer.parseInt(record.get("Active"));
                    if (setNum - activeCasesList.get(i) <= 0) {
                        stats.setPrevDayDifference(0);
                    } else {
                        stats.setPrevDayDifference(setNum - activeCasesList.get(i));
                    }
                }
                i++;

                currentRecordList.add(stats);
            }
            this.allStats = currentRecordList;
        }
    }
    public ArrayList<Integer> getPrevDayDiff() {
        return prevDayDiff;
    }

    public List<MyStats> getAllStats() {
        return allStats;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}

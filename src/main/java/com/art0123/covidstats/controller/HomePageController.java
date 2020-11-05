package com.art0123.covidstats.controller;


import com.art0123.covidstats.mystats.MyStats;
import com.art0123.covidstats.service.CovidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

@Controller
public class HomePageController {

    @Autowired
    private CovidService covidService;

    @GetMapping("/")
    public String startingPage() {
        return "startingPage";
    }

    @RequestMapping(value = "/home", method = RequestMethod.POST)
    public String homePage(@RequestParam("country_name") String countryName, Model model) throws IOException, InterruptedException {
        String correctName = "";
        if (countryName.equalsIgnoreCase("us")) {
            correctName = countryName.toUpperCase();
        } else {
            correctName = countryName.toLowerCase();
            correctName = correctName.substring(0,1).toUpperCase() + countryName.substring(1);
        }

        covidService.setCountryName(correctName);
        covidService.fetchData();
        List<MyStats> allStats = covidService.getAllStats();
        int totalWorldSum = allStats.stream().mapToInt(MyStats::getActiveCases).sum();
        String totalWorldSumFormated = NumberFormat.getInstance().format(totalWorldSum).replace(".", " ");


        model.addAttribute("totalWorldSum", totalWorldSumFormated);
        model.addAttribute("stats", allStats);
        model.addAttribute("prevDayDifference", covidService.getPrevDayDiff());
        return "home";
    }


}

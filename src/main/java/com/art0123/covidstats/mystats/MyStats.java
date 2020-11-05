package com.art0123.covidstats.mystats;

public class MyStats {
    private String country;
    private int activeCases;
    private int prevDayDifference;
    private String state;

    public String getCountry() {
        return country;
    }

    public int getPrevDayDifference() {
        return prevDayDifference;
    }

    public String getState() {
        return state;
    }

    public void setPrevDayDifference(int prevDayDifference) {
        this.prevDayDifference = prevDayDifference;
    }

    public int getActiveCases() {
        return activeCases;
    }

    public void setActiveCases(int activeCases) {
        this.activeCases = activeCases;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Country: " + this.country + ", State: " + this.state + ", Total Cases: " + ", Active Cases: " + this.activeCases;
    }
}

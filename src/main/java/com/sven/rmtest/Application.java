package com.sven.rmtest;

import java.util.List;
import java.util.stream.IntStream;

public class Application
{

    public static void main(String[] args) {
        
        List<Property> properties = PropertyUtils.loadDataFromClassPathFile("/property-data.json");
        System.out.println(String.format("Loaded %d properties", properties.size()));
        
        //1 Find the mean price in the postcode outward "W1F"
        System.out.println("-------------------");
        String queryPostcodeOutward = "W1F";
        Double meanPrice = PropertyUtils.getMeanPriceByPostcodeOutward(queryPostcodeOutward, properties);
        System.out.println(String.format("Mean price for postcode [%s] is %f", queryPostcodeOutward, meanPrice));
        
        
        //2 Find the difference in average property prices between detached houses and flats?
        Double detachedHouseMeanPrice = 
                PropertyUtils.getMeanPriceByPropertyType(PropertyType.Detached, properties);
        System.out.println(String.format("Mean price of detached houses is %f", detachedHouseMeanPrice));
        
        Double flatMeanPrice = 
                PropertyUtils.getMeanPriceByPropertyType(PropertyType.Flat, properties);
        System.out.println(String.format("Mean price of flats is %f", flatMeanPrice));
        
        System.out.println(String.format("Mean price difference between detached houses & flats is %f", 
                detachedHouseMeanPrice - flatMeanPrice
                ));
        System.out.println("-------------------");
        
        //3 Find the top 10% most expensive properties
        int percent = 10;
        List<Property> topNPercentMostExpensiveProperties = PropertyUtils
            .getTopNPercentMostExpensive(percent, properties);
            
        
        IntStream
            .range(0, topNPercentMostExpensiveProperties.size())
            .forEach(s -> {
                System.out.println(String.format("Top %d price %f for property [%d]", 
                        s,
                        topNPercentMostExpensiveProperties.get(s).getPrice(),
                        topNPercentMostExpensiveProperties.get(s).getPropertyReference()));
            });
        
    }
    
    
}

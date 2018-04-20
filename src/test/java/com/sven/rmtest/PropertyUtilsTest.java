package com.sven.rmtest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

public class PropertyUtilsTest
{

    private Property property1 = new Property(1, 100, "ZONE1 ST1", PropertyType.Detached);
    private Property property2 = new Property(2, 30, "ZONE1 ST2", PropertyType.Detached);
    private Property property3 = new Property(3, 21, "ZONE1 ST3", PropertyType.Detached);
    private Property property4 = new Property(4, 155, "ZONE2 ST4", PropertyType.Detached);
    private Property property5 = new Property(5, 23, "ZONE2 ST5", PropertyType.Flat);
    private Property property6 = new Property(6, 100, "ZONE2 ST6", PropertyType.Flat);
    private Property property7 = new Property(7, 100, "ZONE3 ST7", PropertyType.Flat);
    private Property property8 = new Property(8, 100, "ZONE3 ST8", PropertyType.Mansion);
    
    //propertyPriceDescentComparator tests
    @Test
    public void test_propertyPriceDescentComparator() {
        
        List<Property> actual = Arrays.asList(null, property1, property2, property3,property4, property5).stream()
                .sorted(PropertyUtils.propertyPriceDescentComparator)
                .collect(Collectors.toList());
        
        Assert.assertEquals(6, actual.size());
        Assert.assertEquals(property4, actual.get(0));
        Assert.assertEquals(property1, actual.get(1));
        Assert.assertEquals(property2, actual.get(2));
        Assert.assertEquals(property5, actual.get(3));
        Assert.assertEquals(property3, actual.get(4));
        Assert.assertEquals(null, actual.get(5));
    }
    
    //isPostcodeOutward tests
    @Test
    public void test_isPostcodeOutward_with_blank_postcode() {

        List<Property> actual = Arrays.asList(property1, property4, property8).stream()
            .filter(PropertyUtils.isPostcodeOutward(""))
            .collect(Collectors.toList());
        
        Assert.assertEquals(3, actual.size());
        Assert.assertEquals(property1, actual.get(0));
        Assert.assertEquals(property4, actual.get(1));
        Assert.assertEquals(property8, actual.get(2));
    }
    
    @Test
    public void test_isPostcodeOutward_with_Nonblank_postcode() {

        List<Property> actual = Arrays.asList(property1, property4, property8).stream()
            .filter(PropertyUtils.isPostcodeOutward("Zone2"))
            .collect(Collectors.toList());
        
        Assert.assertEquals(1, actual.size());
        Assert.assertEquals(property4, actual.get(0));
    }
    
    //isPropertyType tests
    
    @Test
    public void test_isPropertyType_with_Nonblank_propertyType() {

        List<Property> actual = Arrays.asList(property1, property5, property8).stream()
            .filter(PropertyUtils.isPropertyType(PropertyType.Detached))
            .collect(Collectors.toList());
        
        Assert.assertEquals(1, actual.size());
        Assert.assertEquals(property1, actual.get(0));
    }
    
    //getOutwardPostcode tests 
    @Test
    public void test_getOutwardPostcode_with_blank() {
        Optional<String> actual = PropertyUtils.getOutwardPostcode("");
        
        Assert.assertFalse(actual.isPresent());
    }
    
    @Test
    public void test_getOutwardPostcode_with_Nonblank() {
        Optional<String> actual1 = PropertyUtils.getOutwardPostcode("W11 234 566");
        Optional<String> actual2 = PropertyUtils.getOutwardPostcode("W11");
        Assert.assertEquals("W11", actual1.get());
        Assert.assertEquals("W11", actual2.get());
    }
    
    //loadDataFromClassPathFile tests
    @Test(expected=RuntimeException.class)
    public void test_loadDataFromClassPathFile_with_wrong_file_path() {
        PropertyUtils.loadDataFromClassPathFile("a.b.c");
    }
    
    @Test()
    public void test_loadDataFromClassPathFile() {
        List<Property> actual = PropertyUtils.loadDataFromClassPathFile("/test-property-data.json");
        Assert.assertEquals(2, actual.size());
        Assert.assertEquals(1, actual.get(0).getPropertyReference());
        Assert.assertEquals(2, actual.get(1).getPropertyReference());
    }
    
    //getMeanPriceByPostcodeOutWard tests
    @Test
    public void test_getMeanPriceByPostcodeOutWard_with_null_properties() {
        
        Double actual = PropertyUtils.getMeanPriceByPostcodeOutward("", null);
        Assert.assertEquals(0, actual, 0);
    }
    
    @Test
    public void test_getMeanPriceByPostcodeOutWard_with_blank_postCode() {
        
        List<Property> properties = Arrays.asList(property1, property2, property3);
        Double actual = PropertyUtils.getMeanPriceByPostcodeOutward("", properties);
        Assert.assertEquals(50.333, actual, 0.01);
                
    }
    
    @Test
    public void test_getMeanPriceByPostcodeOutWard_with_nonblank_postCode() {
        
        List<Property> properties = Arrays.asList(property1, property2, property5);
        Double actual = PropertyUtils.getMeanPriceByPostcodeOutward("ZONE1", properties);
        Assert.assertEquals(65, actual, 0);
    }
    
    //getMeanPriceByPropertyType tests
    @Test
    public void test_getMeanPriceByPropertyType_with_null_properties() {
        
        Double actual = PropertyUtils.getMeanPriceByPropertyType(PropertyType.Detached, null);
        Assert.assertEquals(0, actual, 0);
    }
    
    @Test
    public void test_getMeanPriceByPropertyType_with_nonblank_properties() {
        
        List<Property> properties = Arrays.asList(property1, property2, property5);
        Double actual = PropertyUtils.getMeanPriceByPropertyType(
                PropertyType.Detached, properties);
        Assert.assertEquals(65, actual, 0);
    }
    
    //getTopNPercentMostExpensive test
    @Test
    public void test_getTopNPercentMostExpensive_with_null_properties() {
        List<Property> actual = PropertyUtils.getTopNPercentMostExpensive(10, null);
        Assert.assertTrue(actual.isEmpty());
    }
    
    @Test
    public void test_getTopNPercentMostExpensive_with_1_percent_of_3_properties() {
        List<Property> properties = Arrays.asList(property1, property2, property5);
        List<Property> actual = PropertyUtils.getTopNPercentMostExpensive(1, properties);
        Assert.assertEquals(1, actual.size());
        Assert.assertEquals(property1, actual.get(0));
    }
    
    @Test
    public void test_getTopNPercentMostExpensive_with_90_percents_of_3_properties() {
        List<Property> properties = Arrays.asList(property7, property2, property5);
        List<Property> actual = PropertyUtils.getTopNPercentMostExpensive(90, properties);
        Assert.assertEquals(3, actual.size());
        Assert.assertEquals(property7, actual.get(0));
        Assert.assertEquals(property2, actual.get(1));
        Assert.assertEquals(property5, actual.get(2));
    }
    
    @Test
    public void test_getTopNPercentMostExpensive_with_50_percents_of_4_properties() {
        List<Property> properties = Arrays.asList(property7, property6, property8, property5);
        List<Property> actual = PropertyUtils.getTopNPercentMostExpensive(50, properties);
        Assert.assertEquals(3, actual.size());
        Assert.assertEquals(property7, actual.get(0));
        Assert.assertEquals(property6, actual.get(1));
        Assert.assertEquals(property8, actual.get(2));
    }
    
    //getTopNMostExpensive get
    
    @Test
    public void test_getTopNMostExpensive_with_includeIfHasSamePrice() {
        
        List<Property> properties = Arrays.asList(property7, property6, property8, property5);
        List<Property> actual = PropertyUtils.getTopNMostExpensive(2, properties, true);
        Assert.assertEquals(3, actual.size());
        Assert.assertEquals(property7, actual.get(0));
        Assert.assertEquals(property6, actual.get(1));
        Assert.assertEquals(property8, actual.get(2));
    }
    
    @Test
    public void test_getTopNMostExpensive_without_includeIfHasSamePrice() {
        
        List<Property> properties = Arrays.asList(property7, property6, property8, property5);
        List<Property> actual = PropertyUtils.getTopNMostExpensive(2, properties, false);
        Assert.assertEquals(2, actual.size());
        Assert.assertEquals(property7, actual.get(0));
        Assert.assertEquals(property6, actual.get(1));
    }
}

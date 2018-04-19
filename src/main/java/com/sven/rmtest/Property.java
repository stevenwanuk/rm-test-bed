package com.sven.rmtest;

public class Property
{
    private long propertyReference;
    private double price;
    private int bedrooms;
    private int bathrooms;
    private String houseNumber;
    private String address;
    private String region;
    private String postcode;
    private String propertyType;
    
    public Property() {
        
    }
    public Property(long propertyReference, double price, String postcode,
            String propertyType)
    {
        this.postcode = postcode;
        this.propertyReference = propertyReference;
        this.price = price;
        this.propertyType = propertyType;
    }

    public long getPropertyReference()
    {
        return propertyReference;
    }

    public void setPropertyReference(long propertyReference)
    {
        this.propertyReference = propertyReference;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public int getBedrooms()
    {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms)
    {
        this.bedrooms = bedrooms;
    }

    public int getBathrooms()
    {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms)
    {
        this.bathrooms = bathrooms;
    }

    public String getHouseNumber()
    {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber)
    {
        this.houseNumber = houseNumber;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

    public String getPostcode()
    {
        return postcode;
    }

    public void setPostcode(String postcode)
    {
        this.postcode = postcode;
    }

    public String getPropertyType()
    {
        return propertyType;
    }

    public void setPropertyType(String propertyType)
    {
        this.propertyType = propertyType;
    }

}

package com.sven.rmtest;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PropertyUtils
{
    
    private final static String Postcode_Separator = " ";
    
    
    /**
     * comparing price of {@link Property} with descent order.
     * <p>
     * Null value element will be moved to bottom of list
     * 
     */
    public static final Comparator<Property> propertyPriceDescentComparator = new Comparator<Property>() {

        @Override
        public int compare(Property o1, Property o2)
        {
            
            if (o1 == null || o2 == null) {
                
                int val1 = 0;
                int val2 = 0;
                
                //null value should be moved to bottom of list
                if (o1 == null) {
                    val1 = 1;
                }
                if (o2 == null) {
                    val2 = 1;
                }
                return Integer.compare(val1, val2);
            }
            
            return Double.compare(o1.getPrice(), o2.getPrice()) * -1;
        }
    };
    
    /**
     * predicate if postcode of {@link Property}  has same giving <code>outwardPostcode</code>
     * <p>
     * doesn't filter out any {@link Property} if giving <code>outwardPostcode</code> is blank. 
     * @param postcodeOutWard 
     * @return 
     */
    public static final Predicate<Property> isPostcodeOutward(String outwardPostcode) {
        return s -> {
            
            Optional<String> outward = getOutwardPostcode(s.getPostcode());
            return StringUtils.isBlank(outwardPostcode) || 
                    outward.isPresent() && outwardPostcode.equalsIgnoreCase(outward.get());
        };
    }
    
    /**
     * predicate if properTye of {@link Property}  has same giving <code>propertyType</code>
     * 
     * @param propertyType
     * @return
     */
    public static final Predicate<Property> isPropertyType(PropertyType propertyType) {
        return s -> 
            propertyType == s.getPropertyType();
    }

    
    /**
     * Get outward from giving <code>postcode</code>
     * @param postcode
     * @return
     */
    public static Optional<String> getOutwardPostcode(String postcode) {
        
        if (StringUtils.isNotBlank(postcode)) {
            
            String outward = postcode.split(Postcode_Separator)[0];
            return Optional.ofNullable(outward);
        }
        return Optional.empty();
    }
 

    /**
     * load data from classpath file.
     * <p>
     * throw RuntimeException if failed
     * @param classPath
     * @return
     */
    public static List<Property> loadDataFromClassPathFile(String classPath)
    {
        
        /*
         * load file in this way
         * for making sure it's still working when runs as a single War file.  
         */
        
        InputStream in = PropertyUtils.class.getResourceAsStream(classPath);
        if (in == null)
        {
            throw new RuntimeException(
                    String.format("File [%s] not found on classpath", classPath));
        }
        try
        {
            String json = IOUtils.toString(in, Charset.forName("UTF-8"));
            return new Gson().fromJson(json, new TypeToken<List<Property>>()
            {
            }.getType());
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    String.format("Unabled to load data from [%s]", classPath));
        }
    }
    
    /**
     *  
     * filter <code>properties</code> with giving  <code>postcodeOutward</code> and 
     * calculate mean price.
     * <p>
     * filter will not be triggered if giving  <code>postcodeOutward</code> is blank,
     * @param postcodeOutward
     * @param properties
     * @return
     *  0 if <code>properties</code> is empty or null
     */
    public static Double getMeanPriceByPostcodeOutward(String postcodeOutward, Collection<Property> properties)
    {

        if (properties == null || properties.isEmpty()) {
            return 0d;
        }
        
      //@formatter:off
        return properties.stream()
                .filter(isPostcodeOutward(postcodeOutward))
                .collect(Collectors.averagingDouble(Property::getPrice));
      //@formatter:on
    }
    
    /**
     * filter <code>properties</code> with giving  <code>propertyType</code> and 
     * calculate mean price.
     * <p>
     * filter will not be triggered if giving  <code>propertyType</code> is blank, 
     * @param propertyType
     * @param properties
     * @return
     *  0 if <code>properties</code> is empty or null
     */
    public static Double getMeanPriceByPropertyType(PropertyType propertyType, Collection<Property> properties)
    {

        if (properties == null || properties.isEmpty()) {
            return 0d;
        }
        
      //@formatter:off
        return properties.stream()
                .filter(isPropertyType(propertyType))
                .collect(Collectors.averagingDouble(Property::getPrice));
      //@formatter:on
    }
    
    /**
     * get top <code>percent</code> percent most expensive properties
     * <p>
     * fractional part (if have) is rounded up to upper Integer.
     * @param percent
     * @param properties
     * @return
     *  empty list if <code>properties</code> is empty or null
     */
    public static List<Property> getTopNPercentMostExpensive(int percent, Collection<Property> properties)
    {
        if (properties == null || properties.isEmpty()) {
            return Collections.emptyList();
        }
        
        int size = properties.size();
        int n = (int) Math.ceil(size / 100f * percent );
        return getTopNMostExpensive(n, properties, true);        
    }
    
    //@formatter:off
    /**
     * get top <code>n</code> most expensive properties
     * @param n
     * @param properties
     *  must not null.
     * @param includeIfHasSamePrice
     *  if true, include properties if has same price as the last property of top N.
     *  
     *  Example:
     *  For properties:
     *  Property A: price 200 
     *  Property B: price 150
     *  Property C: price 150
     *  Property D: price 100
     *  
     *  Result: 
     *   Top 2 without includeIfHasSamePrice: 
     *      [Property A, Property B]
     *   Top 2 with includeIfHasSamePrice:      
     *      [Property A, Property B, Property C]
     * @return
     */
    //@formatter:on    
    protected static List<Property> getTopNMostExpensive(int n, Collection<Property> properties, boolean includeIfHasSamePrice) {
        List<Property> orderedProperties = properties.stream()
                .sorted(propertyPriceDescentComparator)
                .collect(Collectors.toList());
        
        return IntStream.range(0, orderedProperties.size())
            .filter(s -> {
                if (s < n) {
                    
                    //top N properties
                    return true;
                } else if (includeIfHasSamePrice){
                    
                    //other properties with same price as lowestPriceInTopN
                    double lowestPriceInTopN = orderedProperties.get(n-1).getPrice();
                    double currPrice = orderedProperties.get(s).getPrice();
                    return currPrice >= lowestPriceInTopN;
                    
                } else {
                    
                    //skip rest properties
                    return false;
                }
            }).mapToObj(s -> orderedProperties.get(s))
            .collect(Collectors.toList());
    }
}

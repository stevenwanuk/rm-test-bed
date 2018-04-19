package com.sven.rmtest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PropertyUtils
{
    
    /**
     * predicate of checking postcode of {@link Property}  has same giving <code>outwardPostcode</code>
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
    
    public static final Predicate<Property> isPropertyType(String propertyType) {
        return s -> 
            StringUtils.isBlank(propertyType) ||
            propertyType.equalsIgnoreCase(s.getPropertyType());
    }
    
    public static final Comparator<Property> propertyPriceDescentComparator = new Comparator<Property>() {

        @Override
        public int compare(Property o1, Property o2)
        {
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            return Double.compare(o1.getPrice(), o2.getPrice()) * -1;
        }
    };
    
    public static Optional<String> getOutwardPostcode(String postcode) {
        
        if (StringUtils.isNotBlank(postcode)) {
            
            String outward = postcode.split(" ")[0];
            return Optional.ofNullable(outward);
        }
        return Optional.empty();
    }
 

    public static List<Property> loadDataFromClassPathFile(String classPath)
    {
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
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Double getMeanPriceByPostcodeOutWard(String postcodeOutWard, Collection<Property> properties)
    {

      //@formatter:off
        return properties.stream()
                .filter(isPostcodeOutward(postcodeOutWard))
                .collect(Collectors.averagingDouble(Property::getPrice));
      //@formatter:on
    }
    
    public static Double getMeanPriceByPropertyType(String propertyType, Collection<Property> properties)
    {

      //@formatter:off
        return properties.stream()
                .filter(isPropertyType(propertyType))
                .collect(Collectors.averagingDouble(Property::getPrice));
      //@formatter:on
    }
    
    public static List<Property> getTopNPercentMostExpensive(int percent, Collection<Property> properties)
    {
        
        int size = properties.size();
        int n = (int) Math.ceil(size / 100f * percent );
        return getTopNMostExpensive(n, properties);
    }
    
    protected static List<Property> getTopNMostExpensive(int n, Collection<Property> properties) {
        return properties.stream()
                .sorted(propertyPriceDescentComparator)
                .limit(n)
                .collect(Collectors.toList());
    }
}

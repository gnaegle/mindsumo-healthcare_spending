//******************************************************************************
// Name: ParseHealthcareSpending.java
// 
// Author: Garret Naegle
//
// Description: Using the Healthcare Spending Per Capita dataset, this program
// ranks the top 10 states with the highest total healthcare spending per capita
// (from years 2000 - 2009) and ranks all categories of healthcare services 
// (e.g. hospital care, dental services, etc) from the highest total healthcare
// spending per capita to the lowest (in year 2009).
//******************************************************************************

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

// Comparator class to use with the TreeMap to sort Maps based on value in descending order
class MapValueComparator<K, V extends Comparable<V>> implements Comparator<K>
{
    Map<K, V> map;
 
    MapValueComparator(Map<K, V> map) 
    {
        this.map = map;
    }
 
    public int compare(K key1, K key2) 
    {
           V val1 = map.get(key1);
           V val2 = map.get(key2);

           return val2.compareTo(val1);
    }
}

public class ParseHealthcareSpending 
{
    // Takes in Map and sorts Map based on value
    public static <K, V extends Comparable<V>> Map<K, V> sortMapByValues(Map<K, V> unsortedMap) 
    {
        Map<K, V> sortedMap = new TreeMap<K, V>(new MapValueComparator<K, V>(unsortedMap));
        sortedMap.putAll(unsortedMap);
        return sortedMap;
    }
    
    // Creates string describing the 10 Maps entries with largest values
    public static String createTopTenOuputString(Map<String, Double> map)
    {
        //Sorting Map
        map = sortMapByValues(map);
        
        String output = "";
        int listNum = 1;
        
        // Stepping through entries of sorted Map
        for (Map.Entry<String, Double> entry : map.entrySet())
        {
            // Creating output string
            output += String.valueOf(listNum) + ". " + entry.getKey() + " ($" 
                    + String.format("%1$,.3f", entry.getValue()) + ")\n";
            
            // Breaking when 10th entry is reached
            if(listNum == 10)
                break;
            
            // Incrementing number of processed entries
            listNum++;
        }
        
        return output;
    }
    
    // Creates string listing all entries of a Map in descending order of the values
    public static String createDescendingOuputString(Map<String, Double> map)
    {
        // Sorting Map
        map = sortMapByValues(map);
        
        String output = "";
        int listNum = 1;
        
        // Stepping through entries of sorted Map
        for (Map.Entry<String, Double> entry : map.entrySet())
        {
            // Creating output string
            output += String.valueOf(listNum) + ". " + entry.getKey() + " ($" 
                    + String.format("%1$,.3f", entry.getValue()) + ")\n";
            
            // Incrementing number of processed entries
            listNum++;
        }
        
        return output;
    }
    
    public static void main(String[] args) throws IOException
    {
        String fileName = null;
        
        // Allowing passing of file name to the program or just uses the default file name if one is not provided
        if(args.length == 0)
            fileName = "Healthcare_Spending_Per_Capita.csv";
        else if(args.length == 1)
            fileName = args[0];
        else
        {
            System.out.println("Format for the program is: java ParseHealthcareSpending.java"
                    + " [File name of CSV file]");
            System.exit(0);
        }

        // Initializing Maps used to hold parsed data
        Map<String, Double> stateTotals = new HashMap<String, Double>();
        Map<String, Double> categoryTotals = new HashMap<String, Double>();
       
        String line = null;
        
        try
        {
            // Opening up CSV file to be read
            FileReader csvFileReader = new FileReader(fileName);
            BufferedReader csvFileBufferedReader = new BufferedReader(csvFileReader);
            
            // Skipping first line of file (column headers)
            csvFileBufferedReader.readLine();

            // Stepping through lines of file
            while( (line = csvFileBufferedReader.readLine()) != null)
            {
                // Splitting the line based on a comma that is outside quotes
                String[] fields = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                
                // Indexes in fields array of relevant fields after splitting line
                // Catagory     =   1
                // Group        =   2
                // State        =   5
                // Year 2000    =   15
                // Year 2009    =   24
                
                String category = fields[1];
                
                // Finding state data
                if(fields[2].equals("State"))
                {
                    double total = 0;
                    
                    // getting state name
                    String state = fields[5];

                    // Stepping from year 2000 to year 2009 adding up spending
                    for(int fieldIndex = 15; fieldIndex <= 24; fieldIndex++)
                    {
                        total += Double.parseDouble(fields[fieldIndex]);
                    }
                    
                    // Updating state entry in stateTotals Map
                    stateTotals.put(state, stateTotals.getOrDefault(state, 0.0) + total);
                }
                
                // Updating category entry in categoryTotals Map
                categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0)
                        + Double.parseDouble(fields[24]));              
            }
            
            csvFileBufferedReader.close();
            csvFileReader.close();
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        
        // Printing out top 10 states with the highest total healthcare spending data
        System.out.println("Top 10 states with the highest total healthcare spending"
                + " per capita (from years 2000 - 2009)");
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println(createTopTenOuputString(stateTotals));
        
        // Printing out healthcare services spending data
        System.out.println("Healthcare services from the highest total healthcare spending"
                + " per capita to the lowest (in year 2009)");
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println(createDescendingOuputString(categoryTotals));
    }
    
}
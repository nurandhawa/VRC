package ca.sfu.teambeta.logic;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that reads and writes data to the database
 */
public class DBManager {
    private final String FILENAME = "ladder.csv";

    /**
     * Saves values to the database. TODO: Accept Ladder data type, convert to List<String[]>
     * manually in this method
     *
     * @param values: A List of String arrays, where each String array is a "row" of data
     */
    public void saveToDB(List<String[]> values) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILENAME))) {
            for (String[] nextLine : values) {
                writer.writeNext(nextLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads values from the database. TODO: Return the Ladder data type, convert List manually in
     * this method
     *
     * @return List of all values
     */
    public List loadFromDB() {
        try (CSVReader reader = new CSVReader(new FileReader(FILENAME))) {
            List ladderEntries = reader.readAll();
            return ladderEntries;
        } catch (IOException e) {
            return new ArrayList<>(0);
        }
    }
}

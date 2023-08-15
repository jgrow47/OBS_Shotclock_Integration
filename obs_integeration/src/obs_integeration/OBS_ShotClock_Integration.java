package obs_integeration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.json.JSONObject;

public class OBS_ShotClock_Integration {

// Constants--------------------------------------------------------------------

    /*
     * Parent type
     */
    private static final String TEAM1 = "team1";
    private static final String TEAM2 = "team2";
    private static final String GAMECLOCK = "gameClock";

    /*
     * Name of value wanted
     */
    private static final String SHOTCLOCK = "displayedCount";
    private static final String PLAYERS = "playersLeft";
    private static final String TIMEOUTS = "timeoutsLeft";
    private static final String POINTS = "points";

    /*
     * Game clock
     */
    private static final String MINUTES = "minutes";
    private static final String SECONDS = "seconds";

// Methods----------------------------------------------------------------------

    /**
     * Gets the corresponding value from json_name
     *
     * @param json_name
     *            the name of the corresponding string from the json file
     * @param parent
     *            the corresponding parent (team or gameclock)
     * @param type
     *            the desired output value
     * @return
     */
    private static int getValue(String json_name, String parent, String type) {
        try {
            // Parse the JSON string into a JSONObject
            JSONObject jsonObject = new JSONObject(json_name);

            // Get shot clock value
            int value = jsonObject.getJSONObject(parent).getInt(type);

            return value;
        } catch (Exception e) {
            System.err.println(
                    "Error getting " + parent + "_" + type + " value.");
            throw e;
        }
    }

    /**
     * Writes to file of name "team#_type" with the proper value.
     *
     * @param team
     *            which team the value corresponds to
     * @param type
     *            which type the value corresponds to
     * @param value
     *            the value to the corresponding type
     * @param path
     *            the exact path in which the values will the OBS files will be
     *            uploaded to
     *
     */
    private static void writeToFile(String team, String type, int value,
            String path) {
        // Open file
        PrintWriter file;
        try {
            file = new PrintWriter(new BufferedWriter(
                    new FileWriter(path + "\\" + team + "_" + type + ".txt")));
        } catch (Exception e) {
            System.err.println("Error opening " + team + "_" + type
                    + "file. Check that the proper path was entered.");
            return;
        }

        // Write only the value to the file
        try {
            file.print(value);
        } catch (Exception e) {
            System.err
                    .println("Error writing to " + team + "_" + type + "file");
        }

        // Close the file
        try {
            file.close();
        } catch (Exception e) {
            System.err.println("Error closing " + team + "_" + type + "file");
            return;
        }
    }

    /**
     * Writes to file of name "gameclock" with the proper value.
     *
     * @param gameclock
     *            which team the value corresponds to
     * @param type
     *            which type the value corresponds to
     * @param value1
     *            minutes
     * @param value2
     *            seconds
     * @param path
     *            the exact path in which the values will the OBS files will be
     *            uploaded to
     *
     */
    private static void writeToFile(String gameclock, int value1, String value2,
            String path) {
        // Open file
        PrintWriter file;
        try {
            file = new PrintWriter(new BufferedWriter(
                    new FileWriter(path + "\\" + gameclock + ".txt")));
        } catch (Exception e) {
            System.err.println("Error opening " + gameclock
                    + "file. Check that the proper path was entered.");
            return;
        }

        // Write the time with (minutes):(seconds)
        try {
            file.print(value1 + ":" + value2);
        } catch (Exception e) {
            System.err.println("Error writing to " + gameclock + "file");
        }

        // Close the file
        try {
            file.close();
        } catch (Exception e) {
            System.err.println("Error closing " + gameclock + "file");
            return;
        }
    }

// Main code--------------------------------------------------------------------
    public static void main(String[] args) {
        // Get scanner to be able to read from console
        Scanner scanner = new Scanner(System.in);

        // Get exact location and name of JSON string file
        System.out.println("Enter in exact path and name of JSON string file:");
        String json_file = scanner.nextLine();
        // Test that the file can be opened
        try {
            BufferedReader file = new BufferedReader(new FileReader(json_file));
            file.close();
        } catch (IOException e) {
            System.err.println("Error opening JSON file.");
            return;
        }

        // Get exact path to output the files that OBS will read from
        System.out.println(
                "Enter in exact path to write files that OBS will read from:");
        String path = scanner.nextLine();

        System.out.println(
                "Starting OBS integration program. (type stop to terminate program)");

        // Create a separate thread to monitor input from the console
        Thread inputThread = new Thread(() -> {
            while (true) {
                String userInput = scanner.nextLine().trim();
                // Stop the program from continuing to run
                if (userInput.equalsIgnoreCase("stop")) {
                    System.out.println("Stopping the program...");
                    scanner.close();
                    // Output that program has successfully ended
                    System.out.println("Program stopped successfully.");
                    System.exit(0); // Terminate the entire program
                }
            }
        });

        // Declare values
        Integer value1;
        Integer value2;
        String seconds;

        // Start loop and thread
        inputThread.start();
        while (true) {
            // Open JSON file
            BufferedReader file;
            try {
                file = new BufferedReader(new FileReader(json_file));
            } catch (IOException e) {
                System.err.println("Error opening JSON file.");
                return;
            }

            // Get the JSON string from the file
            String json_string;
            try {
                json_string = file.readLine();
            } catch (IOException e) {
                System.err.println("Error reading from JSON file.");
                // Close file
                try {
                    file.close();
                } catch (IOException f) {
                    System.err.println(
                            "Error reading from and closing JSON file.");
                    return;
                }
                return;
            }

            /*------------------------------------------------------------------
             * Game clock (special case because OBS must read in
             * (minutes):(seconds)
             */

            // Get the value for minutes and seconds
            value1 = getValue(json_string, GAMECLOCK, MINUTES);
            value2 = getValue(json_string, GAMECLOCK, SECONDS);

            // Check if value2 is less than 10, adding a 0 in front if so
            seconds = value2.toString();
            if (value2 < 10) {
                seconds = "0" + seconds;
            }

            // Write the time to the proper file
            writeToFile(GAMECLOCK, value1, seconds, path);

            /*------------------------------------------------------------------
             * Shot clock
             */

            // Get values
            value1 = getValue(json_string, TEAM1, SHOTCLOCK);
            value2 = getValue(json_string, TEAM2, SHOTCLOCK);

            // Write to files
            writeToFile(TEAM1, SHOTCLOCK, value1, path);
            writeToFile(TEAM2, SHOTCLOCK, value2, path);

            /*------------------------------------------------------------------
             * Points
             */

            // Get values
            value1 = getValue(json_string, TEAM1, POINTS);
            value2 = getValue(json_string, TEAM2, POINTS);

            // Write to files
            writeToFile(TEAM1, POINTS, value1, path);
            writeToFile(TEAM2, POINTS, value2, path);

            /*------------------------------------------------------------------
             * Timeouts
             */

            // Get values
            value1 = getValue(json_string, TEAM1, TIMEOUTS);
            value2 = getValue(json_string, TEAM2, TIMEOUTS);

            // Write to files
            writeToFile(TEAM1, TIMEOUTS, value1, path);
            writeToFile(TEAM2, TIMEOUTS, value2, path);

            /*------------------------------------------------------------------
             * Players left
             */

            // Get values
            value1 = getValue(json_string, TEAM1, PLAYERS);
            value2 = getValue(json_string, TEAM2, PLAYERS);

            // Write to files
            writeToFile(TEAM1, PLAYERS, value1, path);
            writeToFile(TEAM2, PLAYERS, value2, path);

            /*------------------------------------------------------------------
             * Cleanup
             */

            // Close file
            try {
                file.close();
            } catch (IOException e) {
                System.err.println("Error closing test file.");
                return;
            }

            // Delay before next loop
            try {
                Thread.sleep(100); // Number in milliseconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

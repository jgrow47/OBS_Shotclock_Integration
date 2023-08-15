# Dodgeball-Streaming
Program used to read from a JSON string (example in obs_integration/data/JSON_testFile) and output individual values to corresponding files in a specfic folder to allow OBS to read these files. Currently the program reads from the JSON string file every 100 ms (1/10 second) and can be set to go faster. The refresh will be set higher when proper tests are able to take place.

How to run program:
- Download the provided jar file called OBS_Integration.jar and put into any desired location
- Go to command prompt and type cd and then the exact path to where you put the jar file
- Then type java -jar OBS_Integration.jar (you must have downloaded java se development kit 17.0.8 here: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- The program will ask for the exact path and file name of the JSON string file and then ask for the exact path to the folder in which to output the files
- The program should run aftwerwards and produce any error messages pertaining to what was provided. The user is required to type stop to end the program.

The program will output the files to the provided path with the format of "parent_type.txt" (A list of all files currently created by the program are below *in the case of team# two files are created, 1 for each team)
- gameClock.txt
- team#_displayedCount.txt
- team#_points.txt
- team#_playersLeft.txt
- team#_timeoutsLeft.txt

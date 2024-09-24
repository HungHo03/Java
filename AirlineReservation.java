import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AirlineReservation {
    /* Delimiters and Formatters */
    private static final String CSV_DELIMITER = ",";
    private static final String COMMAND_DELIMITER = " ";
    private static final String PLANE_FORMAT = "%d\t | %s | %s \n";

    /* Travel Classes */
    private static final int FIRST_CLASS = 0;
    private static final int BUSINESS_CLASS = 1;
    private static final int ECONOMY_CLASS = 2;
    private static final String[] CLASS_LIST = new String[] {"F", "B", "E"};
    private static final String[] CLASS_FULLNAME_LIST = new String[] {
        "First Class", "Business Class", "Economy Class"};

    /* Commands */
    private static final String[] COMMANDS_LIST = new String[] { "book", 
        "cancel", "lookup", "availabletickets", "upgrade", "print","exit"};
    private static final int BOOK_IDX = 0;
    private static final int CANCEL_IDX = 1;
    private static final int LOOKUP_IDX = 2;
    private static final int AVAI_TICKETS_IDX = 3;
    private static final int UPGRADE_IDX = 4;
    private static final int PRINT_IDX = 5;
    private static final int EXIT_IDX = 6;
    private static final int BOOK_UPGRADE_NUM_ARGS = 3;
    private static final int CANCEL_LOOKUP_NUM_ARGS = 2;

    /* Strings for main */
    private static final String USAGE_HELP =
            "Available commands:\n" +
            "- book <travelClass(F/B/E)> <passengerName>\n" +
            "- book <rowNumber> <passengerName>\n" +
            "- cancel <passengerName>\n" +
            "- lookup <passengerName>\n" +
            "- availabletickets\n" +
            "- upgrade <travelClass(F/B)> <passengerName>\n" +
            "- print\n" +
            "- exit";
    private static final String CMD_INDICATOR = "> ";
    private static final String INVALID_COMMAND = "Invalid command.";
    private static final String INVALID_ARGS = "Invalid number of arguments.";
    private static final String INVALID_ROW = 
        "Invalid row number %d, failed to book.\n";
    private static final String DUPLICATE_BOOK =
        "Passenger %s already has a booking and cannot book multiple seats.\n";
    private static final String BOOK_SUCCESS = 
        "Booked passenger %s successfully.\n";
    private static final String BOOK_FAIL = "Could not book passenger %s.\n";
    private static final String CANCEL_SUCCESS = 
        "Canceled passenger %s's booking successfully.\n";
    private static final String CANCEL_FAIL = 
        "Could not cancel passenger %s's booking, do they have a ticket?\n";
    private static final String UPGRADE_SUCCESS = 
        "Upgraded passenger %s to %s successfully.\n";
    private static final String UPGRADE_FAIL = 
        "Could not upgrade passenger %s to %s.\n";
    private static final String LOOKUP_SUCCESS = 
            "Passenger %s is in row %d.\n";
    private static final String LOOKUP_FAIL = "Could not find passenger %s.\n";
    private static final String AVAILABLE_TICKETS_FORMAT = "%s: %d\n";
    
    /* Static variables - DO NOT add any additional static variables */
    static String [] passengers;
    static int planeRows;
    static int firstClassRows;
    static int businessClassRows;

    /**
     * Runs the command-line interface for our Airline Reservation System.
     * Prompts user to enter commands, which correspond to different functions.
     * @param args args[0] contains the filename to the csv input
     * @throws FileNotFoundException if the filename args[0] is not found
     */
    public static void main(String[] args) throws FileNotFoundException {
        // If there are an incorrect num of args, print error message and quit
        if (args.length != 1) {
            System.out.println(INVALID_ARGS);
            return;
        }
        initPassengers(args[0]); // Populate passengers based on csv input file
        System.out.println(USAGE_HELP);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(CMD_INDICATOR);
            String line = scanner.nextLine().trim();

            // Exit
            if (line.toLowerCase().equals(COMMANDS_LIST[EXIT_IDX])) {
                scanner.close();
                return;
            }

            String[] splitLine = line.split(COMMAND_DELIMITER);
            splitLine[0] = splitLine[0].toLowerCase(); 

            // Check for invalid commands
            boolean validFlag = false;
            for (int i = 0; i < COMMANDS_LIST.length; i++) {
                if (splitLine[0].toLowerCase().equals(COMMANDS_LIST[i])) {
                    validFlag = true;
                }
            }
            if (!validFlag) {
                System.out.println(INVALID_COMMAND);
                continue;
            }

            // Book
            if (splitLine[0].equals(COMMANDS_LIST[BOOK_IDX])) {
                if (splitLine.length < BOOK_UPGRADE_NUM_ARGS) {
                    System.out.println(INVALID_ARGS);
                    continue;
                }
                String[] contents = line.split(COMMAND_DELIMITER, 
                        BOOK_UPGRADE_NUM_ARGS);
                String passengerName = contents[contents.length - 1];
                try {
                    // book row <passengerName>
                    int row = Integer.parseInt(contents[1]);
                    if (row < 0 || row >= passengers.length) {
                        System.out.printf(INVALID_ROW, row);
                        continue;
                    }
                    // Do not allow duplicate booking
                    boolean isDuplicate = false;
                    for (int i = 0; i < passengers.length; i++) {
                        if (passengerName.equals(passengers[i])) {
                            isDuplicate = true;
                        }
                    }
                    if (isDuplicate) {
                        System.out.printf(DUPLICATE_BOOK, passengerName);
                        continue;
                    }
                    if (book(row, passengerName)) {
                        System.out.printf(BOOK_SUCCESS, passengerName);
                    } else {
                        System.out.printf(BOOK_FAIL, passengerName);
                    }
                } catch (NumberFormatException e) {
                    // book <travelClass(F/B/E)> <passengerName>
                    validFlag = false;
                    contents[1] = contents[1].toUpperCase();
                    for (int i = 0; i < CLASS_LIST.length; i++) {
                        if (CLASS_LIST[i].equals(contents[1])) {
                            validFlag = true;
                        }
                    }
                    if (!validFlag) {
                        System.out.println(INVALID_COMMAND);
                        continue;
                    }
                    // Do not allow duplicate booking
                    boolean isDuplicate = false;
                    for (int i = 0; i < passengers.length; i++) {
                        if (passengerName.equals(passengers[i])) {
                            isDuplicate = true;
                        }
                    }
                    if (isDuplicate) {
                        System.out.printf(DUPLICATE_BOOK, passengerName);
                        continue;
                    }
                    int travelClass = FIRST_CLASS;
                    if (contents[1].equals(CLASS_LIST[BUSINESS_CLASS])) {
                        travelClass = BUSINESS_CLASS;
                    } else if (contents[1].equals(
                                CLASS_LIST[ECONOMY_CLASS])) {
                        travelClass = ECONOMY_CLASS;
                    }
                    if (book(passengerName, travelClass)) {
                        System.out.printf(BOOK_SUCCESS, passengerName);
                    } else {
                        System.out.printf(BOOK_FAIL, passengerName);
                    }
                }
            }

            // Upgrade 
            if (splitLine[0].equals(COMMANDS_LIST[UPGRADE_IDX])) {
                if (splitLine.length < BOOK_UPGRADE_NUM_ARGS) {
                    System.out.println(INVALID_ARGS);
                    continue;
                }
                String[] contents = line.split(COMMAND_DELIMITER, 
                        BOOK_UPGRADE_NUM_ARGS);
                String passengerName = contents[contents.length - 1];
                validFlag = false;
                contents[1] = contents[1].toUpperCase();
                for (int i = 0; i < CLASS_LIST.length; i++) {
                    if (CLASS_LIST[i].equals(contents[1])) {
                        validFlag = true;
                    }
                }
                if (!validFlag) {
                    System.out.println(INVALID_COMMAND);
                    continue;
                }
                int travelClass = FIRST_CLASS;
                if (contents[1].equals(CLASS_LIST[BUSINESS_CLASS])) {
                    travelClass = BUSINESS_CLASS;
                } else if (contents[1].equals(CLASS_LIST[ECONOMY_CLASS])) {
                    travelClass = ECONOMY_CLASS;
                }
                if (upgrade(passengerName, travelClass)) {
                    System.out.printf(UPGRADE_SUCCESS, passengerName, 
                            CLASS_FULLNAME_LIST[travelClass]);
                } else {
                    System.out.printf(UPGRADE_FAIL, passengerName, 
                            CLASS_FULLNAME_LIST[travelClass]);
                }
            }

            // Cancel
            if (splitLine[0].equals(COMMANDS_LIST[CANCEL_IDX])) {
                if (splitLine.length < CANCEL_LOOKUP_NUM_ARGS) {
                    System.out.println(INVALID_ARGS);
                    continue;
                }
                String[] contents = line.split(COMMAND_DELIMITER, 
                        CANCEL_LOOKUP_NUM_ARGS);
                String passengerName = contents[contents.length - 1];
                if (cancel(passengerName)) {
                    System.out.printf(CANCEL_SUCCESS, passengerName);
                } else {
                    System.out.printf(CANCEL_FAIL, passengerName);
                }
            }

            // Lookup
            if (splitLine[0].equals(COMMANDS_LIST[LOOKUP_IDX])) {
                if (splitLine.length < CANCEL_LOOKUP_NUM_ARGS) {
                    System.out.println(INVALID_ARGS);
                    continue;
                }
                String[] contents = line.split(COMMAND_DELIMITER, 
                        CANCEL_LOOKUP_NUM_ARGS);
                String passengerName = contents[contents.length - 1];
                if (lookUp(passengerName) == -1) {
                    System.out.printf(LOOKUP_FAIL, passengerName);
                } else {
                    System.out.printf(LOOKUP_SUCCESS, passengerName, 
                            lookUp(passengerName));
                }
            }

            // Available tickets
            if (splitLine[0].equals(COMMANDS_LIST[AVAI_TICKETS_IDX])) {
                int[] numTickets = availableTickets();
                for (int i = 0; i < CLASS_FULLNAME_LIST.length; i++) {
                    System.out.printf(AVAILABLE_TICKETS_FORMAT, 
                            CLASS_FULLNAME_LIST[i], numTickets[i]);
                }
            }

            // Print
            if (splitLine[0].equals(COMMANDS_LIST[PRINT_IDX])) {
                printPlane();
            }
        }
    }

    private static void initPassengers(String fileName) throws 
            FileNotFoundException {
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
        String info = scanner.nextLine();
        String [] infoSplit = info.split(",");
        planeRows = Integer.parseInt(infoSplit[0]);
        firstClassRows = Integer.parseInt(infoSplit[1]);
        businessClassRows = Integer.parseInt(infoSplit[2]);
        passengers = new String [planeRows];
        while (scanner.hasNextLine()){
            String info1 = scanner.nextLine();
            String[] info2 = info1.split(",");
            int newRow = Integer.parseInt(info2[0]);
            passengers[newRow] = info2[1];
        }
        scanner.close();
    }

    private static int findClass(int row) {
        if (row < 0) {
            return -1;
        }
        else if (row <= (firstClassRows - 1)) {
            return FIRST_CLASS;
        }
        else if (row <= (firstClassRows + businessClassRows - 1)) {
            return BUSINESS_CLASS;
        }
        else if (row <= (planeRows - 1)) {  // planeRows = firstClassRows + businessClassRows + economyClassRows
            return ECONOMY_CLASS;
        }
        else{
            return -1;
        }
    }

    private static int findFirstRow(int travelClass) {
        if (travelClass == FIRST_CLASS){
            return 0;
        }
        else if (travelClass == BUSINESS_CLASS){
           return firstClassRows; 
        }
        else if (travelClass == ECONOMY_CLASS){
            return firstClassRows + businessClassRows;
        }
        else{
            return -1;
        }
        
    }

    private static int findLastRow(int travelClass) {
        if (travelClass == ECONOMY_CLASS){
            return planeRows -1;
        }
        else if (travelClass == BUSINESS_CLASS){
            return firstClassRows + businessClassRows -1;
        }
        else if (travelClass == FIRST_CLASS){
            return firstClassRows -1;
        }
        else{
            return -1;
        }
    }

    public static boolean book(String passengerName, int travelClass) {
        if (passengerName == null){
            return false;
        }
        int startRow = 0;
        int endRow = 0;
        if (travelClass == FIRST_CLASS){ // find the first and last row of the travel class
            startRow = findFirstRow(travelClass);
            endRow = findLastRow(travelClass);
        }
        else if (travelClass == BUSINESS_CLASS){
            startRow = findFirstRow(travelClass);
            endRow = findLastRow(travelClass);
        }
        else if (travelClass == ECONOMY_CLASS){
            startRow = findFirstRow(travelClass);
            endRow = findLastRow(travelClass);
        }
      
        for (int i = startRow; i <= endRow; i++){ // check if the seat is available
            if (passengers[i] == null){
                passengers[i] = passengerName;
                return true;
            }
        }
        return false; 
    }

    public static boolean book(int row, String passengerName) {
        if (passengerName == null){
            return false;
        }
        int classRow = findClass(row); 
        if (passengers[row] == null){ 
            passengers[row] = passengerName;
            return true;
        }
        else{ // if the seat is not available, find another one in the same class
            for (int i = findFirstRow(classRow); i <= findLastRow(classRow); i++){
                if (passengers[i] == null){
                    passengers[i] = passengerName;
                    return true;
                }
            }
            return false;  
        }
    }

    public static boolean cancel(String passengerName){
        if (passengerName == null){
            return false;
        }
        for (int i = 0; i < passengers.length; i++){
            if (passengerName.equals(passengers[i])){
                passengers[i] = null;
                return true;
            }
        }
        return false;
    }

    public static int lookUp(String passengerName) {
        if (passengerName == null){
            return -1;
        }
        for (int i = 0; i < (int) passengers.length; i++){
            if (passengers[i] != null && passengers[i].equals(passengerName)){
                return i;
            }
        }
        return -1;
    }

    public static int[] availableTickets() {
        int firstClassTickets = 0;
        int businessClassTickets = 0;
        int economyClassTickets = 0;
        for (int i = 0; i < (int) passengers.length; i++){
            if (passengers[i] == null){
                if (findClass(i) == FIRST_CLASS){
                    firstClassTickets++;
                }
                else if (findClass(i) == BUSINESS_CLASS){
                    businessClassTickets++;
                }
                else if (findClass(i) == ECONOMY_CLASS){
                    economyClassTickets++;
                }
            }
        }
        return new int[] {firstClassTickets, businessClassTickets, economyClassTickets};
    }

    public static boolean upgrade(String passengerName, int upgradeClass) {
        if (passengerName == null || lookUp(passengerName) == -1){
            return false;
        }
        int currentClass = findClass(lookUp(passengerName));
        int[] openTickets = availableTickets();
        if (upgradeClass >= currentClass || openTickets[upgradeClass] <= 0){
            return false;
        }
        else{
            cancel(passengerName);
            book(passengerName, upgradeClass);
            return true;
        }
    }

    /**
     * Prints out the names of each of the passengers according to their booked
     * seat row. No name is printed for an empty (currently available) seat.
     */
    public static void printPlane() {
        for (int i = 0; i < passengers.length; i++) {
            System.out.printf(PLANE_FORMAT, i, CLASS_LIST[findClass(i)], 
                    passengers[i] == null ? "" : passengers[i]);
        }
    }
}
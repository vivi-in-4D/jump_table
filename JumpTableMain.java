/* this program is very large and might seem fairly complicated,
 the most important thing to understand about how i wrote this 
 is that for every option the user can choose while in the menu,
 there is an associated function, meaning there is an extra hashmap.
 Some of the options users can choose call the same function.
 These functions are at the very bottom of the "Screen" class
 */


// file io stuff
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

// backing data structures
import java.util.HashMap;
import java.util.Stack;
import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;


interface StateEnterExitMeth {
    public void invoke();
}

interface StateStayMeth {
    public boolean invoke();
}





//  each state besides IDLE has an associated file name, as well as an array of options
enum State {                                                                                                                                            // COLORS: (ANSI escape codes)
    IDLE(null, new String[] {"Stack", "Queue", "List", "Quit"}, "\u001B[37m" ),                                                          // white
    STACK("stack.txt", new String[] {"Push", "Pop", "Save & Move to Queue", "Save and Move to List", "Quit"}, "\u001B[31m" ),            // red
    QUEUE("queue.txt", new String[] {"Enqueue", "Dequeue", "Save & Move to Stack", "Save and Move to List", "Quit"}, "\u001B[32m" ),     // green
    LIST("list.txt", new String[] {"Append", "Remove", "Save & Move to Stack", "Save and Move to Queue", "Quit"}, "\u001B[34m" );        // blue

    private String fileName;
    private String[] options;
    private String color;

    State(String fileName, String[] options, String color) {
        this.fileName = fileName;
        this.options = options;
        this.color = color;
    }


    public String getFileName() {
        return fileName;
    }


    public String[] getOptions() {
        return options;
    }

    public String getColor() {
        return color;
    }
}






 
public class JumpTableMain {
    public static void main(String[] args) {

        Screen screen = new Screen();
        boolean keepRunning = true;

        while(keepRunning) {
            keepRunning = screen.doState();
        }
    }
}



class Screen {

    // initializes extra colors for bonus
    public static final String ANSI_RESET;
    public static final String ANSI_YELLOW;

    static {
        ANSI_RESET = "\u001B[0m";
        ANSI_YELLOW = "\u001B[33m";
    }


    // handle user input
    private String userInput;
    private Scanner inputScanner;


    // read and write files
    private Scanner fileScanner;
    private FileWriter fileWriter;
    private PrintWriter printWriter;


    // state and function maps
    private HashMap<State, StateEnterExitMeth> stateEnterMeths;
    private HashMap<State, StateStayMeth> stateStayMeths;
    private HashMap<State, StateEnterExitMeth> stateExitMeths;

    // every state change we map different functions to user inputted options
    private HashMap<Integer, StateStayMeth> optionMeths;


    // state
    private State screenState;


    // string that visualizes data structures
    private String visualization;


    // backing data structures we are visualizing
    private Stack<Character> stack;
    private Queue<Character> queue; // backing data structure for queue is a linked list
    private ArrayList<Character> list;





    ////// CONSTRUCTOR //////

    public Screen() {

        // create input scanner
        inputScanner = new Scanner(System.in);


        // initialize hashmaps
        stateEnterMeths = new HashMap<>();
        stateStayMeths = new HashMap<>();
        stateExitMeths = new HashMap<>();

        // map states and functions
        stateEnterMeths.put(State.IDLE, () -> { stateEnterIdle(); });
        stateEnterMeths.put(State.STACK, () -> { stateEnterStack(); });
        stateEnterMeths.put(State.QUEUE, () -> { stateEnterQueue(); });
        stateEnterMeths.put(State.LIST, () -> { stateEnterList(); });

        stateStayMeths.put(State.IDLE, () -> { return stateStayIdle(); });
        stateStayMeths.put(State.STACK, () -> { return stateStayStack(); });
        stateStayMeths.put(State.QUEUE, () -> { return stateStayQueue(); });
        stateStayMeths.put(State.LIST, () -> { return stateStayList(); });

        stateExitMeths.put(State.IDLE, () -> { stateExitIdle(); });
        stateExitMeths.put(State.STACK, () -> { stateExitStack(); });
        stateExitMeths.put(State.QUEUE, () -> { stateExitQueue(); });
        stateExitMeths.put(State.LIST, () -> { stateExitList(); });


        // sets state to default
        changeState(State.IDLE);
    }





    /** called to swap states, runs state enter methods */
    public void changeState(State newState) {
        if (screenState != newState) {
            screenState = newState;

            if (stateEnterMeths.containsKey(screenState)) {
                stateEnterMeths.get(screenState).invoke();
            }
        }
    }


    /** called from main function to run stay methods and check for "quit" input */
    public boolean doState() {
        boolean keepRunning = true;

        if (stateStayMeths.containsKey(screenState)) {
            keepRunning = stateStayMeths.get(screenState).invoke();
        }

        return keepRunning;
    }





    //////   ENTER FUNCTIONS   //////

    /** always enter here, creates options for idle menu */
    private void stateEnterIdle() {
        // reset vizualization, so we don't print old data
        visualization = "";

        optionMeths = new HashMap<>();
        
        optionMeths.put(1, () -> { return changeToStack(); });
        optionMeths.put(2, () -> { return changeToQueue(); });
        optionMeths.put(3, () -> { return changeToList(); });
        optionMeths.put(4, () -> { return quit(); });
    }


    /** reads stack file, builds stack, and creates options for stack menu */
    private void stateEnterStack() {
        stack = new Stack<Character>();
        String data[] = readFile(screenState.getFileName()).split(",");

        for (String part : data) {
            stack.push(part.charAt(0));
        }

        optionMeths = new HashMap<>();
        
        optionMeths.put(1, () -> { return addData(); });
        optionMeths.put(2, () -> { return removeData(); });
        optionMeths.put(3, () -> { return saveMoveToQueue(); });
        optionMeths.put(4, () -> { return saveMoveToList(); });
        optionMeths.put(5, () -> { return quit(); });
    }


    /** reads queue file, builds queue, and creates options for queue menu */
    private void stateEnterQueue() {
        queue = new LinkedList<>();
        String data[] = readFile(screenState.getFileName()).split(",");

        for (String part : data) {
            queue.add(part.charAt(0));
        }

        optionMeths.put(1, () -> { return addData(); });
        optionMeths.put(2, () -> { return removeData(); });
        optionMeths.put(3, () -> { return saveMoveToStack(); });
        optionMeths.put(4, () -> { return saveMoveToList(); });
        optionMeths.put(5, () -> { return quit(); });
    }


    /** reads list file, builds list, and creates options for list menu */
    private void stateEnterList() {
        list = new ArrayList<Character>();

        String data[] = readFile(screenState.getFileName()).split(",");

        for (String part : data) {
            list.add(part.charAt(0));
        }

        optionMeths.put(1, () -> { return addData(); });
        optionMeths.put(2, () -> { return removeData(); });
        optionMeths.put(3, () -> { return saveMoveToStack(); });
        optionMeths.put(4, () -> { return saveMoveToQueue(); });
        optionMeths.put(5, () -> { return quit(); });
    }





    //////   STAY FUNCTIONS   //////

    /** builds screen and handles user input */
    private boolean stateStayIdle() {
        boolean keepRunning = true;

        
        printScreen();
        keepRunning = handleUserInput();


        return keepRunning; 
    }


    /** builds screen and handles user input */
    private boolean stateStayStack() {
        boolean keepRunning = true;

        buildStackVisual();
        printScreen();

        keepRunning = handleUserInput();

        return keepRunning; 
    }


    /** builds screen and handles user input */
    private boolean stateStayQueue() { 
        boolean keepRunning = true;

        buildQueueVisual();
        printScreen();

        keepRunning = handleUserInput();

        return keepRunning; 
    }


    /** builds screen and handles user input */
    private boolean stateStayList() { 
        boolean keepRunning = true;

        buildListVisual();
        printScreen();

        keepRunning = handleUserInput();

        return keepRunning; 
    }





    //////   EXIT FUNCTIONS   //////

    /** this just clears options hashmap from heap */
    private void stateExitIdle() {
        optionMeths.clear();
    }


    /** writes to file, clears options hashmap from heap */
    private void stateExitStack() {
        String dataToWrite = "";
        char currentChar;

        while (!stack.empty()) {
            currentChar = stack.pop();
            dataToWrite += String.format("%c,", currentChar);
        }

        writeFile(screenState.getFileName(), dataToWrite);

        optionMeths.clear();
    }


    /** writes to file, clears options hashmap from heap */
    private void stateExitQueue() {
        String dataToWrite = "";
        char currentChar;

        while (queue.peek() != null) {
            currentChar = queue.remove();
            dataToWrite += String.format("%c,", currentChar);
        }

        writeFile(screenState.getFileName(), dataToWrite);

        optionMeths.clear();
    }

    
    /** writes to file, clears options hashmap from heap */
    private void stateExitList() {
        String dataToWrite = "";
        char currentChar;

        while (list.size() != 0) {
            currentChar = list.remove(list.size()-1);
            dataToWrite += String.format("%c,", currentChar);
        }

        writeFile(screenState.getFileName(), dataToWrite);

        optionMeths.clear();
    }





    ///////   BUILD VISUALIZATIONS   //////

    /** builds stack visualization
     *  because stacks can not be accessed from the middle, we just move everything into a new stack and then move it back
     */
    private void buildStackVisual() {
        Stack<Character> swap = new Stack<>();
        char currentChar;

        visualization = "|   |\n";

        while(!stack.empty()) {
            currentChar = stack.pop();
            visualization += String.format("|---|\n| %c |\n", currentChar);

            swap.push(currentChar);
        }


        while(!swap.empty()) {
            stack.push(swap.pop());
        }


        visualization += "|---|\n";
    }


    /** builds queue visualization
     *  because queues can not be accessed from the middle, we just move everything into a new queue and then move it back
     */
    private void buildQueueVisual() {
        Queue<Character> swap = new LinkedList<>();
        char currentChar;

        visualization = "|";

        while (queue.peek() != null) {
            currentChar = queue.remove();
            visualization += String.format(" %c |", currentChar);

            swap.add(currentChar);
        }

        
        while(swap.peek() != null) {
            queue.add(swap.remove());
        }


        visualization += "\n";
    }


    /** builds list visualization */
    private void buildListVisual() {
        visualization = "{ ";
        int size = list.size();

        for (int i=0; i<size; i++) {
            visualization += String.format("%c, ", list.get(i));
        }

        visualization += " }\n";
    }





    //////   IO   //////

    /** gets user input and calls function associated with that input */
    private boolean handleUserInput() {
        int optionNum;
        boolean keepRunning = true;

        // gets user input, then converts it into a usable index so we can call a function
        userInput = inputScanner.nextLine();
        optionNum = userInput.charAt(0) - 48;


        if (optionMeths.containsKey(optionNum)) {
                keepRunning = optionMeths.get(optionNum).invoke();
        }

        return keepRunning;
    }


    /** prints visualization and options */
    private void printScreen() {
        String[] options = screenState.getOptions();
        int size = options.length;

        System.out.print(screenState.getColor() + visualization + ANSI_RESET);

        for (int i=0; i<size; i++) {
            System.out.println(ANSI_YELLOW + (i+1) + ". " + options[i]);
        }

        System.out.print("? " + ANSI_RESET);
    }





    /** 
     * reads data from file and stores as string
     * @param fileName name of file to read from
     * @return returns string that was read from file
    */
    private String readFile(String fileName) {
        String dataFromFile = "";

        // handles idle state, adds protection against changes
        if (fileName != null) {
            try {
                File fileToRead = new File(fileName);
                fileScanner = new Scanner(fileToRead);

                while(fileScanner.hasNextLine()) {
                    dataFromFile += (fileScanner.nextLine());
                }

                fileScanner.close();

            }   
            catch(FileNotFoundException error) {
                System.err.println("An error occured while attempting to read from file.");
                error.printStackTrace();
            }
        }

        return dataFromFile;

    }


    /** 
     * writes to files 
     * @param fileName name of file to write to
     * @param dataToWrite data you wish to write to file
    */
    private void writeFile(String fileName, String dataToWrite) {
        try {
            fileWriter = new FileWriter(fileName);
            printWriter = new PrintWriter(fileWriter);

            printWriter.print(dataToWrite);
            printWriter.close();
        }
        catch(IOException error) {
            System.err.println("An error occured while attempting to write to file.");
            error.printStackTrace(printWriter);
        }
    }





    //////   OPTIONS FUNCTIONS   //////

    /** function called when choosing "Quit" in any menu */
    private boolean quit() {
        return false;
    }


    /** function called when choosing "Stack" in the first menu" */
    private boolean changeToStack() {
        changeState(State.STACK);

        return true;
    }

    /** function called when choosing "Queue" in the first menu" */
    private boolean changeToQueue() {
        changeState(State.QUEUE);

        return true;
    }

    /** function called when choosing "List" in the first menu" */
    private boolean changeToList() {
        changeState(State.LIST);

        return true;
    }



    /** handles push, enqueue, append, when each of those options are selected */
    private boolean addData() {
        // make sure we got proper input to add
        if (userInput.length() >= 3) {
            char data = userInput.charAt(2);

            if (screenState == State.STACK) {
                stack.push(data);
            }
            else if (screenState == State.QUEUE) {
                queue.add(data);
            }
            else if (screenState == State.LIST) {
                list.add(data);
            }
        }

        return true;
    }

    /** handles pop, dequeue, remove, when each of those options are selected */
    private boolean removeData() {
        if (screenState == State.STACK) {
            if (!stack.empty()) {
                stack.pop();
            }
        }
        else if (screenState == State.QUEUE) {
            if (queue.peek() != null) {
                queue.remove();
            }
        }
        else if (screenState == State.LIST) {
            if (list.size() != 0) {
                list.remove(list.size()-1);
            }
        }

        return true;
    }


    /** function called when user selects "Save and Move to Stack" */
    private boolean saveMoveToStack() {
        if (screenState == State.QUEUE) {
            stateExitQueue();
        }
        else if (screenState == State.LIST) {
            stateExitList();
        }

        changeState(State.STACK);

        return true;
    }

    /** function called when user selects "Save and Move to Queue" */
    private boolean saveMoveToQueue() {
        if (screenState == State.STACK) {
            stateExitStack();
        }
        else if (screenState == State.LIST) {
            stateExitList();
        }

        changeState(State.QUEUE);

        return true;
    }

    /** function called when user selects "Save and Move to List" */
    private boolean saveMoveToList() {
        if (screenState == State.STACK) {
            stateExitStack();
        }
        else if (screenState == State.QUEUE) {
            stateExitQueue();
        }

        changeState(State.LIST);

        return true;
    }
}
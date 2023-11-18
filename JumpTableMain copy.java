
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


interface StateEnterExitMeth {
    public void invoke();
}

interface StateStayMeth {
    public boolean invoke();
}



/*  each state besides null has an associated file name, as well as an associated "options" enum, this is done through hashmaps rather than linking directly through enum values, 
    as I couldn't find a good way to do it through just enums, also to make it easier to read. it is pretty complex, but its the best solution i could come up with to promote maintainability */
enum State {
    IDLE(null),
    STACK("stack.txt"),
    QUEUE("queue.txt"),
    LIST("list.txt");

    public String fileName;

    State(String fileName) {
        this.fileName = fileName;
    }
}



 // the  "Options" enums exist to print a list of options and map functions to those options
enum IdleOptions {
    IDLE(null, 0),
    STACK("Stack", 1),
    QUEUE("Queue", 2),
    LIST("List", 3),
    QUIT("Quit", 4);

    public String optionName;
    public int optionNum;

    IdleOptions(String optionName, int optionNum) {
        this.optionName = optionName;
        this.optionNum = optionNum;
    }
}

enum StackOptions {
    IDLE(null, 0),
    PUSH("Push", 1),
    POP("Pop", 2),
    SandQ("Save & Move to Queue", 3),
    SandL("Save & Move to List", 4),
    QUIT("Quit", 5);

    public String optionName;
    public int optionNum;

    StackOptions(String optionName, int optionNum) {
        this.optionName = optionName;
        this.optionNum = optionNum;
    }
}

enum QueueOptions {
    IDLE(null, 0),
    ENQUEUE("Enqueue", 1),
    Dequeue("Dequeue", 2),
    SandS("Save & Move to Stack", 3),
    SandL("Save & Move to List", 4),
    QUIT("Quit", 5);

    public String optionName;
    public int optionNum;

    QueueOptions(String optionName, int optionNum) {
        this.optionName = optionName;
        this.optionNum = optionNum;
    }
}

enum ListOptions {
    IDLE(null, 0),
    APPEND("Append", 1),
    REMOVE("Remove", 2),
    SandS("Save & Move to Stack", 3),
    SandQ("Save & Move to Queue", 4),
    QUIT("Quit", 5);

    public String optionName;
    public int optionNum;

    ListOptions(String optionName, int optionNum) {
        this.optionName = optionName;
        this.optionNum = optionNum;
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

    private Scanner fileScanner;
    private FileWriter fileWriter;
    private PrintWriter printWriter;



    private HashMap<State, StateEnterExitMeth> stateEnterMeths;
    private HashMap<State, StateStayMeth> stateStayMeths;
    private HashMap<State, StateEnterExitMeth> stateExitMeths;

    // maps states to their respective list of options, because all enums inherit from object, we use that as a type, as each option list is a different enum type
    private HashMap<State, Object> stateOptionsMap;



    private State screenState;

    // options lists
    private IdleOptions idleOptions;
    private StackOptions stackOptions;
    private QueueOptions queueOptions;
    private ListOptions listOptions;



    private String visualization;

    private Stack stack;
    private Queue queue;
    private ArrayList list;



    public Screen() {

        stateEnterMeths = new HashMap<>();
        stateStayMeths = new HashMap<>();
        stateExitMeths = new HashMap<>();


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



        idleOptions = IdleOptions.IDLE;
        stackOptions = StackOptions.IDLE;
        queueOptions = QueueOptions.IDLE;
        listOptions = ListOptions.IDLE;

        stateOptionsMap.put(State.IDLE, idleOptions);
        stateOptionsMap.put(State.STACK, stackOptions);
        stateOptionsMap.put(State.QUEUE, queueOptions);
        stateOptionsMap.put(State.LIST, listOptions);

        System.out.println(idleOptions.optionNum);

        try {
            System.out.println((stateOptionsMap.get(State.IDLE)).optionNum);
        }
        catch(Exception e) {

        }

        screenState = null;
        changeState(State.IDLE);
    }



    /** called to swap states, runs state enter methods */
    public void changeState(State newState) {
        if (screenState != newState) {
            screenState = newState;

            // reset visualization
            visualization = "";

            if (stateEnterMeths.containsKey(screenState)) {
                stateEnterMeths.get(screenState).invoke();
            }
        }
    }


    /** called from main function to run stay methods and check for "quit" input */
    public boolean doState() {
        if (stateStayMeths.containsKey(screenState)) {
            stateStayMeths.get(screenState).invoke();
        }

        return true;
    }



    // Enter
    private void stateEnterIdle() {
        
    }
    private void stateEnterStack() {

    }
    private void stateEnterQueue() {

    }
    private void stateEnterList() {

    }


    // Stay
    private boolean stateStayIdle() {
        //System.out.println((stateOptionsMap.get(screenState)).optionName);
        return true; 
    }
    private boolean stateStayStack() {
        return true; 
    }
    private boolean stateStayQueue() { 
        return true; 
    }
    private boolean stateStayList() { 
        return true; 
    }


    // Exit
    private void stateExitIdle() {

    }
    private void stateExitStack() {

    }
    private void stateExitQueue() {

    }
    private void stateExitList() {

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
}
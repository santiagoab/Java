import gate.util.GateException;
import gatestandaloneapp.CallMyGATEApp;
import wekatools.WekaMain;
import wekatools.WekaTools;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Boolean.FALSE;

public class App {
    private static Boolean debug = FALSE;
    private static PrintStream originalStream;
    private static PrintStream noStream;

    public static void main(String[] args) {
        String rootDir = args[0]; 
        String pathToWekaTraining = rootDir.concat("/lab_1/data/instances.arff");
        String pathToMyGapp = rootDir.concat("/lab_1/gapps/MyNLPApplication.gapp");
        String pathToAirPlaneGapp = rootDir.concat("/lab_1/gapps/AIR_CRASH_IE.gapp");
        String pathToEarthQuakeGapp = rootDir.concat("/lab_1/gapps/EARTHQUAKE_IE.gapp");

        try {
            originalStream = System.out;
            noStream = new PrintStream(new OutputStream() {
                public void write(int a) {
                }
            });

            if (!debug) System.setOut(noStream);
            WekaMain.init(pathToWekaTraining);
            if (!debug) System.setOut(originalStream);

            System.out.println("Training the Text Classifier.....done!\n");

            if (!debug) System.setOut(noStream);
            CallMyGATEApp.load(pathToMyGapp, pathToAirPlaneGapp, pathToEarthQuakeGapp);
            if (!debug) System.setOut(originalStream);

            System.out.println("Loading IE System for aviation accidents .... done!\n");
            System.out.println("Loading IE System for earthquakes ..... done!\n");

            while (true) {
                Scanner reader = new Scanner(System.in);  // Reading from System.in
                System.out.print("READY FOR YOUR TEXT> ");
                String text = reader.nextLine();

                if (text.toLowerCase().compareTo("quit") == 0) {
                    System.out.println("GOOD BYE!!!");
                    break;
                }

                if (!debug) System.setOut(noStream);

                String result = WekaMain.typeText(text.toLowerCase());
                String type = result;

                if (!debug) System.setOut(originalStream);

                if (result.compareTo("EARTHQUAKE") != 0) {
                    result += " ACCIDENTS.";
                }

                System.out.printf("\nYOUR TEXT IS ABOUT %s\n\n", result);

                System.out.println("CALLING THE EXTRACTION SYSTEM....\n");

                domain(type, text);

                System.out.println("");

            }

        } catch (GateException ge) {
            ge.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(CallMyGATEApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(WekaTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static void domain(String type, String text) throws IOException, GateException {
        switch (type) {
            case "AVIATION":
                airplane(text);
                break;
            case "EARTHQUAKE":
                earthquake(text);
                break;
            default:
                System.out.println("DOMAIN NOT RECOGNIZED.\n");
                break;
        }
    }

    public static void airplane(String text) throws IOException, GateException {
        if (!debug) System.setOut(noStream);
        CallMyGATEApp.processText(text);
        if (!debug) System.setOut(originalStream);

        CallMyGATEApp.AirPlane(text);
    }

    public static void earthquake(String text) throws IOException, GateException {
        if (!debug) System.setOut(noStream);
        CallMyGATEApp.processText(text);
        if (!debug) System.setOut(originalStream);
        CallMyGATEApp.EarthQuake(text);
    }
}

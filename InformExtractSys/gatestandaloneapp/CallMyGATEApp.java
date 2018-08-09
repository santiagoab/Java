package gatestandaloneapp;

import gate.*;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;

import static java.lang.Boolean.FALSE;


/**
 * Loading in JAVA a GATE gapp and executing it over a document
 *
 * @author UPF
 */
public class CallMyGATEApp {

    public static Document document;
    private static Boolean debug = FALSE;
    private static Corpus corpus;
    private static CallMyGATEApp myapp;
    private static CallMyGATEApp my_airplane_extractor;
    private static CallMyGATEApp my_earthquake_extractor;

    public CorpusController application;


    public CallMyGATEApp() {
        super();
    }


    public static void load(String pathToMyGapp, String pathAirPlaneGapp, String pathEarthQuakeGapp) throws GateException, IOException {
        Gate.init();

        myapp = new CallMyGATEApp();
        myapp.loadMyGapp(pathToMyGapp);

        my_airplane_extractor = new CallMyGATEApp();
        my_airplane_extractor.loadMyGapp(pathAirPlaneGapp);

        my_earthquake_extractor = new CallMyGATEApp();
        my_earthquake_extractor.loadMyGapp(pathEarthQuakeGapp);

    }

    public static void processText(String text) throws GateException, IOException {
        corpus = Factory.newCorpus("");
        // create a GATE document from a string
        //Document document=Factory.newDocument("December 20 American Airlines Flight 965 , a Boeing 757 , crashes into a mountain while approaching Santiago de Cali, Colombia ; of the 164 people on board, only 4 people and a dog survive.");
        document = Factory.newDocument(text);
        // put document in corpus
        corpus.add(document);
        // pass corpus to app
        myapp.setCorpus(corpus);

        // show annotations before call
        System.out.println(">>>> annotations before call <<<<<");
        System.out.println(document.getAnnotations());
        // execute app
        myapp.executeMyGapp();
        // show annotations after call
        System.out.println(">>>> annotations after call <<<<<");
        //System.out.println(document.getAnnotations());
        myapp.extractTokenString(document);
        // do stuff with your document...

    }

    public static void AirPlane(String text) throws GateException, IOException {
        PrintStream originalStream = System.out;
        PrintStream noStream = new PrintStream(new OutputStream() {
            public void write(int a) {
            }
        });

        if (!debug) System.setOut(noStream);

        String type = "AVIATION ACCIDENT";
        my_airplane_extractor.setCorpus(corpus);
        my_airplane_extractor.executeMyGapp();
        if (!debug) System.setOut(originalStream);
        System.out.println("I FOUND THE FOLLOWING USEFUL INFORMATION ABOUT THE AVIATION ACCIDENT:\n");
        my_airplane_extractor.extractAnnotations(document, "Mention", "type");

        Factory.deleteResource(document);
    }

    public static void EarthQuake(String text) throws GateException, IOException {
        PrintStream originalStream = System.out;
        PrintStream noStream = new PrintStream(new OutputStream() {
            public void write(int a) {
            }
        });

        if (!debug) System.setOut(noStream);
        String type = "EARTHQUAKE";
        my_earthquake_extractor.setCorpus(corpus);
        my_earthquake_extractor.executeMyGapp();
        if (!debug) System.setOut(originalStream);
        System.out.println("I FOUND THE FOLLOWING USEFUL INFORMATION ABOUT THE EARTHQUAKE:\n");
        my_earthquake_extractor.extractAnnotations(document, "Mention", "type");
        Factory.deleteResource(document);
    }
    /* Extract the "string" from each "Token" from DEFAULT annotation Set */

    public void loadMyGapp(String pathToGapp) throws IOException, ResourceInstantiationException, PersistenceException {

        // load the GAPP
        this.application =
                (CorpusController)
                        PersistenceManager.loadObjectFromFile(new File(pathToGapp));
    }

    /* Extract annotations from DEFAULT annotation Set */

    public void setCorpus(Corpus c) {
        this.application.setCorpus(c);

    }

    public void executeMyGapp() throws ExecutionException {

        this.application.execute();
    }

    public void extractTokenString(Document doc) {

        /* the default annotations in the document */
        AnnotationSet all = doc.getAnnotations();

        /* extract the annotations required */
        AnnotationSet annotations = all.get("Token");

        /* iterate on the annotations and list the value of the feature */
        Iterator<Annotation> ite = annotations.iterator();
        Annotation ann;
        FeatureMap fm;

        while (ite.hasNext()) {
            ann = ite.next();
            fm = ann.getFeatures();
            if (fm.containsKey("string")) {
                System.out.println(fm.get("string"));
            }
        }

    }

    public void extractAnnotations(Document doc, String annType, String featureName) {

        /* the text of the document */
        String dc = doc.getContent().toString();

        /* the default annotations in the document */
        AnnotationSet all = doc.getAnnotations();

        /* extract the annotations required */
        AnnotationSet annotations = all.get(annType);

        /* iterate on the annotations and list the value of the feature */
        Iterator<Annotation> ite = annotations.iterator();

        Annotation ann;
        FeatureMap fm;
        Long start, end;
        String the_type;
        String the_mention;

        while (ite.hasNext()) {
            ann = ite.next();
            fm = ann.getFeatures();

            if (fm.containsKey(featureName)) {
                // extract the feature value from the feature map
                the_type = fm.get(featureName).toString();

                // start and end of the annotation
                start = ann.getStartNode().getOffset();
                end = ann.getEndNode().getOffset();

                // the string representing the mention from the content of the document
                the_mention = dc.substring(start.intValue(), end.intValue());
                System.out.println(the_type + ": " + the_mention);
                //annotationsList.add(the_type + ": " + the_mention);

            }
        }

    }

}
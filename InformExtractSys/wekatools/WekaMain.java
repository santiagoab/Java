package wekatools;

import java.io.File;

public class WekaMain {
    private static WekaTools model;

    public static void init(String path) throws Exception {
        model = new WekaTools();
        model.setTraining(path);
        //testing.setTraining("instances.arff");

        // *******  set the classifier  *******
        String classifier = "weka.classifiers.bayes.NaiveBayes";
        String InputMappedclassifier = "weka.classifiers.misc.InputMappedClassifier";
        String[] classifierOptions = weka.core.Utils.splitOptions("-I -trim -W " + classifier);
        model.setClassifier(InputMappedclassifier, classifierOptions);

        // *******  set the filter  *******
        // (in our case we use no filter)
        String filter = "weka.filters.AllFilter"; //AllFilter is like no filter
        String[] filterOptions = weka.core.Utils.splitOptions("");
        model.setFilter(filter, filterOptions);

        // *******  Build the model (Train)  *******
        model.buildModel();
    }

    public static String typeText(String text) throws Exception {
        model.setTestSet(text);
        String result = model.getClassification().toUpperCase();
        return result;
    }
}

package wekatools;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;

import java.io.BufferedReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.evaluation.NominalPrediction;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;

/**
 * A tools to use WekaMain APIs
 */
public class WekaTools {

    /** topic names **/

    protected ArrayList<String> event_types;
    /** the classifier used internally */
    protected Classifier m_InputMappedClassifier = null;

    /** the classifier used internally */
    protected Classifier m_Classifier = null;

    /** the filter to use */
    protected Filter m_Filter = null;

    /** the training file */
    protected String m_TrainingFile = null;

    /** the training instances */
    protected Instances m_Training = null;

    /** the training instances */
    protected Instances m_TrainingFiltered = null;

    /** the test instances */
    protected Instances m_Test = null;

    /** the filter to use on the test set*/
    protected Filter m_STWVFilter = null;

    /** the filter to use on the test set*/
    protected Filter m_ReorderFilter = null;

    /** for evaluating the classifier */
    protected Evaluation m_Evaluation = null;

    /** for evaluating the classifier */
    public boolean trained = false;

    /**
     * initializes String To Word Vector and Reorder Filters
     * @throws java.lang.Exception
     */
    public WekaTools() throws Exception {
        super();

        // configure String To Word Vector filter
        String nameSTWV="weka.filters.unsupervised.attribute.StringToWordVector";
        String[] filterOptionsSTWV = weka.core.Utils.splitOptions("-R first -W 1000 -prune-rate -1.0 -N 0 -stemmer weka.core.stemmers.NullStemmer -M 1 -tokenizer \"weka.core.tokenizers.WordTokenizer -delimiters \\\" \\\\r\\\\n\\\\t.,;:\\\\\\'\\\\\\\"()?!\\\"\"");

        m_STWVFilter = (Filter) Class.forName(nameSTWV).newInstance();
        if (m_STWVFilter instanceof OptionHandler)
            ((OptionHandler) m_STWVFilter).setOptions(filterOptionsSTWV);

        // configure Reorder filter
        String nameReorder="weka.filters.unsupervised.attribute.Reorder";
        String[] filterOptionsReorder = weka.core.Utils.splitOptions("-R 2-last,first");

        m_ReorderFilter = (Filter) Class.forName(nameReorder).newInstance();
        if (m_ReorderFilter instanceof OptionHandler)
            ((OptionHandler) m_ReorderFilter).setOptions(filterOptionsReorder);
    }

    /**
     * sets the classifier to use
     * @param name        the classname of the classifier
     * @param options     the options for the classifier
     */
    public void setClassifier(String name, String[] options) throws Exception {
        m_InputMappedClassifier = AbstractClassifier.forName(name, options);
    }

    /**
     * sets the filter to use
     * @param name        the class name of the filter
     * @param options     the options for the filter
     */
    public void setFilter(String name, String[] options) throws Exception {
        m_Filter = (Filter) Class.forName(name).newInstance();
        if (m_Filter instanceof OptionHandler)
            ((OptionHandler) m_Filter).setOptions(options);
    }

    /**
     * sets the file to use for training
     * and applies the filters STWV and Reorder to the training set
     * in order to use the words of the training set as features
     *
     * @param name        the path of the file ARFF to use as training
     */
    public void setTraining(String name) throws Exception {
        m_TrainingFile = name;
        Instances m_TrainingTmp = new Instances(
                new BufferedReader(new FileReader(m_TrainingFile)));

        //String To Word Vector Filter
        m_STWVFilter.setInputFormat(m_TrainingTmp);
        Instances m_TrainingSTWV = Filter.useFilter(m_TrainingTmp, m_STWVFilter);

        System.out.println("\n------------------------------------");
        System.out.println("Applying StringToWordVector filter to Training Set:\n" + m_TrainingSTWV.toString());

        //Apply Reorder Filter to test set
        m_ReorderFilter.setInputFormat(m_TrainingSTWV);
        Instances m_TrainingReorder = Filter.useFilter(m_TrainingSTWV, m_ReorderFilter);

        System.out.println("\n------------------------------------");
        System.out.println("Applying Reorder filter to Training Set:\n" + m_TrainingReorder.toString() + "\n");

        // set Training Set
        m_Training = m_TrainingReorder;
        m_Training.setClassIndex(m_Training.numAttributes() - 1);


        Enumeration enu=m_Training.attribute("topic").enumerateValues();
        event_types=new ArrayList();
        while(enu.hasMoreElements()) {
            event_types.add(enu.nextElement().toString());
        }

    }


    /**
     * given a text to test it creates the Test Set (only one Instance)
     * and applies the filters STWV and Reorder to it
     * in order to use the words of the sentence given in input as features
     *
     * @param text        the text to test
     */
    public void setTestSet(String text) throws Exception {

        // declare text attribute
        Attribute textAttribute = new Attribute("text",(Vector) null);

        // declare Class attribute
        ArrayList fvClassVal = new ArrayList();
        fvClassVal.add("aviation");
        fvClassVal.add("earthquake");
        fvClassVal.add("train");
        Attribute classAttribute = new Attribute("topic", fvClassVal);

        // declare the feature vector
        ArrayList fvWekaAttributes = new ArrayList();
        fvWekaAttributes.add(textAttribute);
        fvWekaAttributes.add(classAttribute);

        // create an empty training set
        m_Test = new Instances("Rel", fvWekaAttributes, 5); //5 is the capacity, we need only one instance though

        // create empty instance with two attribute values (2 features)
        Instance inst = new DenseInstance(2);

        // set instance's values for the attributes text and class
        inst.setValue((Attribute)fvWekaAttributes.get(0), text);
        inst.setValue((Attribute)fvWekaAttributes.get(1), "train");

        // add the instance to the empty test set
        m_Test.add(inst);

        // print the test set
        System.out.println("Creating the Test Set:\n" + m_Test.toString());

        //Apply String To Word Vector Filter to Test Set
        m_STWVFilter.setInputFormat(m_Test);

        Instances test_filteredSTWV = Filter.useFilter(m_Test, m_STWVFilter);

        System.out.println("\n------------------------------------");
        System.out.println("Applying StringToWordVector filter to Test Set::\n" + test_filteredSTWV.toString());

        //Apply Reorder Filter to Test Set
        m_ReorderFilter.setInputFormat(test_filteredSTWV);

        Instances test_filteredReorder = Filter.useFilter(test_filteredSTWV, m_ReorderFilter);

        System.out.println("\n------------------------------------");
        System.out.println("Applying Reorder filter to Test Set:\n" + test_filteredReorder.toString() + "\n");

        m_Test = test_filteredReorder;

        m_Test.setClassIndex(m_Test.numAttributes() - 1);
    }

    /**
     * builds the model (train)
     */
    public void buildModel() throws Exception {

        // run filter
        m_Filter.setInputFormat(m_Training);
        m_TrainingFiltered = Filter.useFilter(m_Training, m_Filter);

        // train classifier on complete file for tree
        m_InputMappedClassifier.buildClassifier(m_TrainingFiltered);
    }

    public String getClassification() throws Exception {

        // Evaluate the sentece
        m_Evaluation = new Evaluation(m_TrainingFiltered);
        m_Evaluation.evaluateModel(m_InputMappedClassifier, m_Test);
        System.out.println(m_Evaluation.toSummaryString("\nResults\n=======\n", false));

        ArrayList list= m_Evaluation.predictions();
        NominalPrediction prediction=(NominalPrediction) list.get(0);

        double predicted=prediction.predicted();

        if(predicted<=event_types.size()) {
            return event_types.get((new Double(predicted)).intValue());
        } else {
            return "Unknown";
        }
    }

    /**
     * Returns some data about the classifier
     */
    public String classifierInfo() {
        StringBuffer        result;

        result = new StringBuffer();
        result.append("INFO: WekaMain - Model Results\n===========\n");

        //result.append("CLassifier: "
        //    + Utils.toCommandLine(m_InputMappedClassifier) + "\n");

        result.append("Input Mapped Classifier with...: "
                + Utils.toCommandLine(m_InputMappedClassifier).substring(56) + "\n"); //to get the actual name of the algorithm used

        if (m_Filter instanceof OptionHandler)
            result.append("Filter.......: "
                    + m_Filter.getClass().getName() + " "
                    + Utils.joinOptions(((OptionHandler) m_Filter).getOptions()) + "\n");
        else
            result.append("Filter.......: "
                    + m_Filter.getClass().getName() + "\n");
        result.append("Training file: "
                + m_TrainingFile + "\n");
        result.append("");
        result.append("===========\n\n");

        // more info
        //result.append(m_InputMappedClassifier.toString() + "\n");

        return result.toString();
    }

}

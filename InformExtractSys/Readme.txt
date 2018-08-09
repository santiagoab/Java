Group project created at UPF. Big amounts of machine learning data not in the repository.

Text classification system that given a string of text, can determine if it is about earthquakes, airplane accidents or an unknown topic.
After this classification is successfully done, we extract information from the text, such as when and where it happened and how many victims are if any.
Depending on which kind of text we have, the information extraction system (IE) implemented with GATE will also give us specific information about the text: 
In the Airplane accidents case, it will display the flight number, the airline, the type of aircraft and the type of accident. 
In the case of Earthquakes, it will show the region and the magnitude of the earthquake.
Otherwise the system will display a message stating that the topic wasn’t recognized and no extraction will be done.

We implement in Java the text classification system using the Weka machine learning library.
The process the program goes through is using a database of .xml files from the CONCISUS Corpus related to airplane accidents and earthquakes to create an .arff file 
which allows us to train the text classifier, and once it’s trained, we can insert a text input and the program will recognize which domain the text belongs to.

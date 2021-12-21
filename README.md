# GameTheorySimulator

Made by: Alexandru Martin Locodi<br />
License: CC-BY<br />
This is the most up to date version of the simulator.
Version and Date for Paper 1 can be found: https://github.com/Locodi/GameTheorySimulator-Paper1 <br />
Version and Date for Paper 2 can be found: https://github.com/Locodi/GameTheorySimulator-Paper2 <br />
## Table of Contents 

- [Data](#data)
- [Simulator Installation](#Simulator-installation)
- [How to use simulator](#How-to-use-simulator)
- [Bugs](#bugs)

## Data
The data is found in the Data + analysis folder.
## Simulator Installation

### To get the project started:
- remove currently installed java (if any) by going to control panel -> Programs and Features and uninstalling the java you have
- download the java jdk 14.0.1 from https://www.oracle.com/java/technologies/javase-jdk14-downloads.html 
- instal the jdk
- control panel -> System -> Advanced system settings -> Advanced -> Environment Variables -> in System Variables select "Path" then click edit and then:
  - for Windows 8.1 (and probably similar for older) add (don't remove anything!) at the end of the Variable value the following "C:\Program Files\Java\jdk-14.0.1\bin;" (change if you installed the jdk somewhere else); the Variable value should look something like this: "C:\Windows\system32;C:\Windows;C:\Windows\System32\Wbem;C:\Windows\System32\WindowsPowerShell\v1.0\;C:\Program Files (x86)\NVIDIA Corporation\PhysX\Common;C:\Program Files\Java\jdk-14.0.1\bin;"; then apply and close
  - for Windows 10 click new and put "C:\Program Files\Java\jdk-14.0.1\bin" -> ok -> ok -> ok
- now go to https://www.eclipse.org/ and find the Eclipse IDE 2020-06 R Packages and download the Eclipse IDE for Enterprise Java Developers
- instal eclipse (in the java path the new jdk should be there) -> select all Certificates -> accept
- In eclipse go to help -> marketplace -> search for e(fx)clipse -> install it, it will ask for eclipse to restart allow it.
- Get JavaFX Windows x64 SDK from https://gluonhq.com/products/javafx/
- Create a new User Library under Eclipse -> Window -> Preferences -> Java -> Build Path -> User Libraries -> New, name it JavaFX14 -> click add external JARs -> include all the jars under the lib folder from "...\javafx-sdk-14.0.1\lib" -> Apply and Close
- Download Project from github 
- Import the project (the Java folder) in eclipse
- Go to Run -> run configurations -> Java Application -> MainMenu -> arguments -> VM arguments add:  --module-path "Your location!\javafx-sdk-14.0.1\lib" --add-modules javafx.controls,javafx.fxml  ->  Apply
- go to Project -> properties -> Java Build Path -> Libraries -> in classpath if there are any remove them -> then click classpath -> click add Library -> User Library -> select JavaFX14 -> finish and then Apply and Close


### To install SceneBuilder (it allows easy edit of fxml files):
- go to https://gluonhq.com/products/scene-builder/ and download it
- now in eclipse go to Window -> Preferences -> JavaFX -> in SceneBuilder executable browse for the SceneBuilder.exe
- now you can right click a .fxml file and open it with SceneBuilder

## How to use simulator
- When you start the simulator you will see the Main Menu. There are 5 buttons:
  - Continue -> which will open the last graph used (the one in the CurrentGraph folder).
  - New -> which will allow you to create a new graph.
  - Open -> which will allow you to open an existing graph.
  - Other Features -> which will send you to the Other Features menu.
  - Exit -> which closes the application.

![Alt text](Images/MainMenu.PNG?raw=true "Main Menu")

- In the New graph window we have 5 inputs, for the first 2 if the values are >=3 it will create a lattice graph (with k=4) of that size otherwise we will create an empty graph,the next input sets the Interaction Model (1 - default), the next input sets the Update Mechanism (1 - default, 2 - normalized payoff) and the last input will set the name of the graph.

![Alt text](Images/New_Graph.PNG?raw=true "New Graph")

- In the Open Graph window we see a list of all graphs (the ones saved in the Graphs folder) we can then select one and open it or delete it.

![Alt text](Images/Open_Graph.PNG?raw=true "Open Graph")

- In the Editor window we can edit our graph:
  - To the left:
    - if nothing is selected (by clicking on empty space) we will see:
      - the number of nodes
      - the payoffs which we can edit here
      - we can set a number of nodes to be random defectors
      - we can zoom in and out
      - and we can see the Interaction Model and Update Mechanism
    - if a node is selected:
      - we will see the nodes id
      - we can set if it cooperates or defects
      - and we can set its x and y positions (which will also change the size of the canvas if necessary)
  - In the middle:
    - we can see the graph
    - we can scroll to left-right and up-down if the graph is to large
    - we can click and drag a node to change its position
    - We can click and drag in an empty space to start selecting multiple nodes
  - To the right we have 7 buttons:
    - Add node will make it so the next click in an empty space will create a new node, hold shift to repeat the process
    - Add edge will make it so you can create an edge between the next 2 nodes you click. If multiple nodes are selected, before clicking the button, you will add an edge frome each selected node to the next node you click, hold shift to repeat the process
    - Delete will make it so you delete the next node or edge you click, or it will delete all nodes currently selected and any edges to them, hold shift to repeat the process
    - Select & copy will give you a ghost of currently selected nodes (and the edges between them) after taht whne you click an empty space it will place new nodes there acording to the ghost
    - Simulate will save the graph and send you to the Simulation screen
    - Adv. Simulate will save the graph and send you to the Simulation screen
    - Save & Exit will save the graph and send you to the Main Menu screen

![Alt text](Images/Editor1.PNG?raw=true "Editor1")
![Alt text](Images/Editor2.PNG?raw=true "Editor2")

- In the Simulate window we can run simple step by step simulations on our graph:
  - On the right we have the additional options to:
    - View a generated lattice graph as an image (up to some size and afected by zoom)
    - Save the image generated (afected by zoom)
  - at the bottom we have 4 buttons:
    - Go back -> will send you back to the Editor window
    - Restore -> will revert the nodes to the initial configuration
    - Step -> will update the nodes to the next step
    - Play/Pause -> will start/stop the automatic step with the speed of each step can be adjusted to the right when it is paused.

![Alt text](Images/Simulate.PNG?raw=true "Simulate")

- In the Advanced simulate window we can see the advanced simulations we can do on the graph, it has 2 buttons:
  - Full Simulation -> will send you to the Full Simulation window
  - Go Back -> will send you back to the Editor window

![Alt text](Images/Advanced_simulate.PNG?raw=true "Advanced simulate")

- In the Full Simulation window we can run multiple simulations on the graph. It will start with start of range (initialized with 0) number of random defectors and increase by the step until we have more than the range end (initialized with the number of nodes in the graph). This will be repeated for Repeats times. The results are stored in test.csv

![Alt text](Images/Full_Simulation.PNG?raw=true "Full Simulation")

- In the Other Features window we see a list of other simulations we can run, we have 3 buttons:
  - One Permutation -> will send you to the One Permutation window
  - Lattice 4 Payoffs -> will send you to the Lattice 4 Payoffs window
  - Payoffs Test -> will send you to the Payoffs Test window
  - Go Back -> will send you back to the Main Menu window

![Alt text](Images/Other_Features.PNG?raw=true "Other Features")

- In the One Permutation window we can look at all connected graphs of size Number of nodes and record what the result for all possible 1 defector permutation. If weighted graph is checked then the chance for the current node to see if it will copy a neighbour goes from 1 to 1/degree of current node. The results are stored in a .csv file.

![Alt text](Images/One_Permutation.PNG?raw=true "One Permutation")

- In the Lattice 4 Payoffs it generates all possible payoff relations (+ examples) for the k=4 lattice graph acording to methods 1 and 2. The results are stored in a .csv file.

![Alt text](Images/Lattice_4_Payoffs.PNG?raw=true "Lattice 4 Payoffs")

- In the Payoffs Test window it goes over all payoff ratios from 1 to 10 with increment of 0.1 and for each one tries to find variables for a=a' and b (the number of minor nodes, it looks up to 20000) such that we can build the LocodiGraph, there is an option for S=0.

![Alt text](Images/PayoffTest.PNG?raw=true "Payoff Test")


## Bugs
- some visual elements are not shown unless the screen gets resized (each time you change the screen), caused when in windows display settings the display scale and layout is not 100%

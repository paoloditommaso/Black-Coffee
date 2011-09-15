What is Black-Coffee?
=====================

Black-Coffee is a headless blackbox test runner for command line applications. 



Compile
------------
To compile just run 'ant' in the project project root.

	ant 

You can find more about Ant at the following address http://ant.apache.org/ 


Usage
-----
The compile step will produce the file 'black-coffee.jar' in the project. 
To run it use the following command line: 

	java -jar black-coffee.jar [options]  

If you are lazy you may prefer to use a bash wrapper to invoke the Java application. 

	#!/bin/bash
	# set path to java using JAVA_HOME if available, otherwise assume it's on the PATH
	JAVA_PATH=${JAVA_HOME:+$JAVA_HOME/jre/bin/}java
	$JAVA_PATH -jar ./black-coffee.jar "$@"

Save the above fragment in a file named 'blackcoffee.sh' (or whatever you like). 
Grant it execute permission et voila.  



 



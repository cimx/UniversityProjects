@echo off
cd  mediator-ws
echo .
echo mediator compile 
	cmd /C mvn clean compile 
	timeout /t 1
	echo mediator run 1
	start "server" mvn exec:java 
	timeout /t 2
	echo mediator run 2
	start "server" mvn exec:java -Dws.i=2 > ..\..\med2.txt 2>&1
cd ..
echo . mediator end
echo .


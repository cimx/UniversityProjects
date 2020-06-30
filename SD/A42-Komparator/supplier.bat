@echo off
cd supplier-ws
echo .
echo supplier compile 
	cmd /C mvn clean compile 
	timeout /t 1
	echo supplier run 1
	start "server" mvn exec:java 
	timeout /t 1
	echo supplier run 2
	start "server" mvn exec:java -Dws.i=2 > ..\..\sup2.txt 2>&1
	timeout /t 1
	echo supplier run 2
	start "server" mvn exec:java -Dws.i=3 > ..\..\sup3.txt 3>&1
cd ..
echo . supplier end
echo .


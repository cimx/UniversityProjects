@echo off

cd supplier-ws-cli
cmd /C mvn clean install -DskipTests
cd ..

cd cc-ws-cli
cmd /C mvn clean compile install -DskipTests
cd ..


cd mediator-ws
cmd /C mvn clean install compile 
start "mediator" mvn exec:java
cd ..




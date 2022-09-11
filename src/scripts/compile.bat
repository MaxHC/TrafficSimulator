@echo off
javac -d "../build" ../simulator/Main*.java ../simulator/controller/*.java ../simulator/model/*.java ../simulator/view/*.java ../simulator/planner/*.java ../simulator/utils/*.java ../simulator/policies/car/*.java ../simulator/policies/light/*.java -Xlint
pause
@echo off
cd ../build/
jar cfm ../scripts/test.jar ../scripts/Manifest.txt simulator/*.class simulator/controller/*.class simulator/model/*.class simulator/view/*.class simulator/planner/*.class simulator/policies/car/*.class simulator/policies/light/*.class simulator/utils/*.class
pause
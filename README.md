# TrayBuddy :)

### Summary
Hey everyone!
This is a little program I keep running to help automate tasks I do a lot. I started out with automating links that I opened frequently, but I ended up adding extraneous applications that I wanted to build and launch quickly.

### Tips
Make sure to have the files "pass.txt" and "api.txt" in the /src/main/resources/ directory. These hold important keys that I did not keep in the repo for obvious reasons. 

You can build a new .jar file using Build -> Build Artifacts -> Build.

In /out/artifacts/trayBuddy2_jar, you should create trayBuddy.bat, which only runs ```start javaw -jar trayBuddy2.jar```

On the startup of your computer, make sure to create a .bat file to run trayBuddy2.bat in the /out/artifacts/trayBuddy2_jar directory. Keeps things modular and easy to change with updates to TrayBuddy.
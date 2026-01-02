# TrayBuddy :)

Hey everyone!
This is a little program I keep running to help automate tasks I do a lot. I started out with automating links that I opened frequently, but I ended up adding extraneous applications that I wanted to build and launch quickly.

### Tips
Make sure to have the files "pass.txt" and "api.txt" in the /src/main/resources/ directory. These hold important keys that I did not keep in the repo for obvious reasons.

(Edit) I forgot to do that and for right now you should get it in the out/artifacts/tray_buddy_jar/ directory after building it with intelliJ

You can build a new .jar file using Build -> Build Artifacts -> Build.

In /out/artifacts/trayBuddy2_jar, you should create trayBuddy.bat, which only runs ```start javaw -jar trayBuddy2.jar```

On the startup of your computer, make sure to create a .bat file to run trayBuddy2.bat in the /out/artifacts/trayBuddy2_jar directory. Keeps things modular and easy to change with updates to TrayBuddy.

## Project Sensitive Knowledge
### Rust Piano
This program is a doozy. Basically, it listens for midi key presses and builds audio in real time to simulate a keyboard, while watching for specific key press patterns to trigger certain things. It was done in multiple languages for modularity and speed, though making it that way also created a lot of knowledge based dependencies.

First, the TrayBuddy Java program launches one of three Rust .exe programs that are bundled into the jar file (you will need to rebuild the .jar file after rust program changes). These three programs represent three states of the piano: Listening for patterns, playing the notes, or both. The fourth case is when nothing is happening, during which all programs are closed.

Secondly for pattern recognition we have Flutter applications. These should (in the best case) be bundled into the Rust.exes. However to save space (and because I couldn't figure it out), the Rust application instead looks for a specific location in the PC to launch a named .exe. This isn't good but I don't particularly care because it works well. Good luck
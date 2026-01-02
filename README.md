# TrayBuddy :)

Hey everyone!
This is a little program I keep running to help automate tasks I do a lot. I started out with plans to Plans to create a website that's controllable by this, and then connect hardware to the website. Hopefully given time I'll be able to make my own web of connected devices I control through this.

On the startup of whatever computer you have, make sure to link to trayBuddy.bat in the /jarBuild directory. Keeps things modular and easy to change with updates to traybuddy

(Edit) I forgot to do that and for right now you should get it in the out/artifacts/tray_buddy_jar/ directory after building it with intelliJ


Project Sensitive Knowledge:

Rust Piano 

This program is a doozy. Basically, it listens for midi key presses and builds audio in real time to simulate a keyboard, while watching for specific key press patterns to trigger certain things. It was done in multiple languages for modularity and speed, though making it that way also created a lot of knowledge based dependencies.
First, the TrayBuddy Java program launches one of three Rust .exe programs that are bundled into the jar file (you will need to rebuild the .jar file after rust program changes). These three programs represent three states of the piano: Listening for patterns, playing the notes, or both. The fourth case is when nothing is happening, during which all programs are closed.
Secondly for pattern recognition we have Flutter applications. These should (in the best case) be bundled into the Rust.exes. However to save space (and because I couldn't figure it out), the Rust application instead looks for a specific location in the PC to launch a named .exe. This isn't good but I don't particularly care because it works well. Good luck
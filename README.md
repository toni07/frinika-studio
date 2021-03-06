Frinika Studio
==============

Frinika Studio is a free and open source multiplatform music workstation software, experimental fork of Frinika.

Visit http://frinika.com for more information about original software.

It is written in Java and should run on Linux, Windows, Mac OS and other operating systems running Java.

Screenshot
----------

![Frinika Studio Screenshot](images/screenshot.png?raw=true)

Features
--------

 * Sequencer
 * Piano roll
 * Amiga-style tracker
 * Notation
 * MIDI and audio recording
 * Soft synths
 * Mixer
 * Effects
 * Mastering
 * VST/VSTi support

License
-------

Remember that this is free software with absolutely NO WARRANTY and no responsibility  
for damaging your system or speakers or hearing or anything else.

Use at own risk and keep volume control down before launching or starting to  
play anything. Otherwise you may experience loud sound or noise that may  
damage your speakers or even your hearing.

Before proceeding you should read and accept the GNU General Public License which is  
in the license.txt file and you can also read more here:

GNU General Public License version 2.0 or later (GPLv2+) 

https://www.gnu.org/licenses/gpl-2.0.html

Installation requirements and run
---------------------------------

Make sure that you have Java 8 or greater installed (http://www.java.com)

If you've downloaded this as a zip file, remember to extract all the files.

From the extracted folder either launch frinika-studio.exe from your desktop  
(or frinika-studio.jar if supported), or enter a command prompt terminal and type:

java -jar frinika-studio.jar

If you want lowest latency possible you can try the following:

Windows users:  
java -DuseASIOAudioServer=true -jar frinika-studio.jar

Linux users:  
Start Jack before launching Frinika and setup audio to use it

Mac OSX (warning: Sometimes using this option causes a terrible noise,  
so turn the volume down before launching. If it happens, try restarting)
(warning 2: VSTs wil not work (yet) on x64 architectures):
(*big warning* 3: You will not be able to record audio sound with OSXAudioServer):
java -DuseOSXAudioServer=true -jar frinika-studio.jar

Structure
---------

 * doc - Documentation + related presentations
 * gradle - Gradle wrapper
 * src - Sources related to building distribution packages
 * modules - Application parts and library modules
 * plugins - Optional plugins
 * tools - Distributable applictions or runnable subprojects and utilities
 * lib - External libraries
 * resources - Related resource files, like sample files, images, etc.

Compiling
---------

Java Development Kit (JDK) version 8 or later is required to build this project.

For project compiling Gradle 4.0 or later build system is used. You can either  
download and install gradle or use gradlew script which will download separate copy of gradle by itself.

Build system website: http://gradle.org  
Build command: gradle distZip

Development
-----------

The Gradle build system provides support for various IDEs. See gradle website for more information.

 * Eclipse 3.7 or later

   Install Gradle integration plugin: http://marketplace.eclipse.org/content/gradle-integration-eclipse-0

 * NetBeans 8.0 or later

   Install Gradle support plugin: http://plugins.netbeans.org/plugin/44510/gradle-support

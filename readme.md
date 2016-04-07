Fishing Bot
=========

This is a program designed to automate the Fishing skill in World of Warcraft. The program uses pixel recognition to detect the fishing bobber, then it proceeds to scan around the bobber ~8 times a second a determines a 'score' for the level of blue in the frame. If the difference of blue changes from its' orignal value to a certain threshold, the program will right-click the bobber to attempt to loot the fish.

[![FishingBot Video](http://i.imgur.com/Uk9f2wD.png)](https://www.youtube.com/watch?v=UP0pyAsMffg)

<iframe width="854" height="480" src="https://www.youtube.com/watch?v=UP0pyAsMffg" frameborder="0" allowfullscreen></iframe>

Instructions: 
-----------------

* Ensure Java is up to date.
* Install SuperMacro if you are using World of Warcraft Patch 1.12.1 (provided in "Suggested Addons")
* Run "FishingBot.jar"

Necessary considerations before using:
-----------------------------------------------------

* Requires Java 8 or above.
* Before each use, you must tell FishingBot where your [Fishing Bobber] tooltip lies. To do this, start fishing manually, press the calibrate button, then tab back into World of Warcraft and hover over the bobber. If it cannot locate your tooltip, you can right click the Calibrate button to define a customized search area.
* World of Warcraft must be on your primary monitor, Full-Screen Windowed Mode, and the graphics settings turned up so the program can clearly see the 'Splash' animation.
* The program must see the 'Fishing Bobber' tooltip. Without knowing where the tooltip lies on your screen, it cannot determine when we are hovering over the bobber.
* It is highly recommended that you zoom in to first-person view before running the Fishing Mode to prevent false positivies from your character model. 
* The program assumes you have your fishing pole equipped and fishing skill learned before using the program.
* Your camera and character should be facing the same direction to ensure that the bobber-finder can locate the bobber correctly.
* Make sure you did not disable tooltips in the World of Warcraft menu before running this program.
* Try not to have full bags as it will impede the ability of the program to loot and continue fishing.

Features needed:
------------------------

* Lure attachment support
* Ban safety
* Timer set by user to quit on

Notes: 
---------

* This program should work on all versions of World of Warcraft. 
* See the video attached for more information.

Contact:
------------

kev070892@gmail.com for any questions.
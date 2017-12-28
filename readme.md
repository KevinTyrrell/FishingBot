### Fishing Bot
=========

This is a program written in Java which automates the Fishing profession in the video game **World of Warcraft**. The program runs outside of the client (does not inject any processes) and simply scans pixels on the screen to look for color patterns. In order to find the Fishing Bobber that floats in the water in-game, the program relies on the fact that the game itself generates a tooltip when the user hovers over the bobber. This is the tell-tale indicator that allows FishingBot to work. After finding the Fishing Bobber, the program will search around the bobber in a circular pattern. It averages the amount of blue in that circle as an integer value. Many times a second it will perform that same operation, and if the amount of blue changed radically between the this scan and the original one, it clicks and loots the fish!

## Outdated Video (Warning)

[![FishingBot Video](http://i.imgur.com/Uk9f2wD.png)](https://www.youtube.com/watch?v=UP0pyAsMffg)

<iframe width="854" height="480" src="https://www.youtube.com/watch?v=UP0pyAsMffg" frameborder="0" allowfullscreen></iframe>

Requirements:
--------------
* Java 8 or above. If you need to install or update your Java version, you can do so [here](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)..
* If playing on World of Warcraft Patch 1.12.1, download the addon called SuperMacro (this is because the /use command didn't exist back then). _The addon is included in the releases section of this repository._

Instructions: 
-----------------

* Run "FishingBot.jar"
* Make sure World of Warcraft is in **full screen windowed mode**.
* Equip a fishing rod in-game.
* Face your character towards the water so that the horizon of the water is at the half way mark of your screen (_see video above for example_).
* Go into first person view in-game (_Recommended_).
* The program must find your [Fishing Bobber] tooltip. /cast Fishing in-game and hover yor mouse over the bobber. You will notice a tooltip come up (_Usually on the bottom-right hand corner_). This is what the program needs to find. Press the calibration button and then hover over the Fishing Bobber. If the calibration fails, you can right click the button to specify a custom search area (_Useful for those who have custom UIs_). Once the program is calibrate, upon closing the calibration will be saved and you (shouldn't usually) need to repeat this process as long as the tooltip remains in the exact same spot next time you run the program.
* Press the 'Start' button and allow the program to have control of your mouse and keyboard.
* If the cursor 'overshoots' the Fishing Bobber, it means your GPU was not fast enough to render the Fishing Bobber tooltip. Lower your scan speed until the issue no longer persists.
* If the program fails to detect the splash of the bobber, it means that the amount of 'blue' that appeared during the splash didn't constitute enough of a change from the original state of the water. Either raise the sensitivity slider or increases your graphical settings until the issue no longer persists.

Extra options:
---------------
* FishingBot will automatically apply lures if one is selected. If you select a lure, it will attempt to be used right after it fades away (_5-10 minutes depending on the lure_). If the quantity section is blank in the options menu, then it will consider the lure supply infinite. If a quantity is specified, it will use only that many lures until the quantity runs to zero. Once the user is out of lures, the program will fish normally without attempting to use more lures.
* FishingBot will automatically logout your character if you are out of lures when the checkbox in the options menu is selected.
* FishingBot will automatically logout your character at a specified time if the checkbox has been selected. 

Ban Safety:
-----------
* FishingBot is undetectable by Warden or any other modern bot/hack/tool detection systems. This is because it runs outside of the World of Warcraft client. All the program does is move your mouse cursor, take screen shots (_In order to scan pixels on the screen_), press a few keyboard keys, and right click. The program does not issue commands to the game executable itself but instead just uses the mouse and keyboard like a normal user.
* While FishingBot cannot be detected, YOUR in-game character CAN BE. Warden is not needed for a ban if your character has been sitting in Moonglade fishing for the past two days straight.
* FishingBot can automatically logout your character based on lures or time. It is strongly recommended to use these especially if you are frequenting one specific spot over and over.

Disclaimers:
------------
* World of Warcraft must be on your system's primary monitor.
* FishingBot works better with computers that have dedicated graphics cards. Higher in-game settings to make the fishing splash more noticable will make it easier on the program to detect fishing splashes.
* FishingBot will struggle once your character has a full inventory. There is not really any way FishingBot could know or react to this so it is the user's job to avoid this from happening.
* FishingBot uses in-game commands to fish and apply lures and uses copy-paste to do this. Non-USA keyboards SHOULD work with this but if any issue arises, you can change your system keyboard to English and it should resolve the issue.

Features in-mind for the future:
--------------------------------
* Automatic log-in if a disconnect occurs.

Contact:
---------

kev070892@gmail.com for any questions.

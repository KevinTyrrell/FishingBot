Description:
Are you tired of deleting all the crap items from your inventory that you get 
from looting corpses? Or are you tired of having to manually click the items 
you want from a corpse? Then this addon is for you. It lets you filter loot 
by quality, item name and item value, and helps you keep your inventory clean. 
Use shift-right-click to loot corpses (loots all items) without having to worry 
what will end up in your inventory.


Installation:
Unzip the LootFilter folder in your AddOns directory.


Usage:
/lf or /lfr or /lootfilter, brings up the control panel
/lf help , shows you the commandline options


Todo:
- add tooltips/help
- add translations


Changelog:

0.9.5
- Non-ASCII characters are now handled properly, and can be used like any other character. This is a major improvement for the non-English World of Warcraft clients.

0.9.4
- Updated .toc to 11200
- Fixed detection of optional dependencies properly (ADDON_LOADED)
- Changed the text of some labels to make things clearer
- Fixed an issue with 'icon' having a 'nil' value
- Minor changes that i forgot

0.9.3.3
- Added the ability to use patterns in the 'SHOULD be filtered' list as well

0.9.3.2
- Updated .toc to 11100

0.9.3.1
- Optional dependencies are now detected properly

0.9.3
- Added the ability to also use patterns in the item list names. When using a pattern it needs to be prefixed by a hash (#). Some examples:
#(.*)cloth
#Major (.*) Potion$
You can read more about patterns here:
http://lua-users.org/wiki/PatternsTutorial
http://www.lua.org/manual/5.1/manual.html#5.4.1
- Added Informant as an optional dependency to filter on item value. This is the addon that is being used by Auctioneer.
- Removed Auctioneer as an optional dependency.

0.9.2
- Item lists are now automatically sorted by name
- Item names with punctuation (,-'") characters are now stored properly
- Matching on item names is now case INsensitive
- Item names only match if the whole name matches, 'linen' will not match 'linen cloth' anymore
- Some cosmetic changes
- Checked if filter on item value is still working, it still is

0.9.1
- now also using item name when searching for looted items in the backpacks instead of just the icon texture
- if filter on item value is turned on, only items that have a known value will be filtered

0.9
- fixed a bug when sending notices
- added /lfr as a command to bring up the control panel
- added PackRat support to filter on item value
- added ability to click on items to add them to the SHOULD NOT or SHOULD field.

0.8
- The value of stackable items is now always the value of one item * the maximum amount in a stack. So for Runecloth the maximum amount would be 20 and for a potion it would be 5.

0.7
- Fixed some issues with nil values
- Added Filter on item value. Auctioneer, LootLink and SellValue are currently supported. If the value of an item is found and it meets the minimum item value requirement then it is not filtered. The value of stackable items is always the item value of 1 * 20. The value entered is in gold, 0.88 gold equals 88 silver.
The filter order is:
1. items that should NOT be filtered
2. items that SHOULD be filtered
3. item value
4. item quality

0.6
- Removed the Chronos dependency, we run on our own now
- Fixed an issue where GetContainerItemLink returns nothing

0.5
- Added option notify on delete
- Fixed some small editbox issues
- You are now able to add items that you always want to filter. The filter order is:
1. items that should NOT be filtered
2. items that SHOULD be filtered
3. item quality


0.4
- Fixed an issue where the editbox would not get focus
- Settings are now saved per server and character

0.3
- Removed the Sea dependency
- Added option to filter quest items
- Removed notices when items passed the filter

0.2
- Initial release 
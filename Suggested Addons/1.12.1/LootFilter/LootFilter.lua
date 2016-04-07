
LootFilterVars = {
};

 
LootFilter = {
	VERSION = "0.9.5",
	LOOT_TIMEOUT = 20,
	LOOT_PARSE_DELAY = 7,
	REALMPLAYER= "",

	qualities= {
		[1]= "Crap (Grey)",
		[2]= "Common (White)",
		[3]= "Uncommon (Green)",
		[4]= "Rare (Blue)",
		[5]= "Epic (Purple)",
		[6]= "Legendary (Orange)",
		[7]= "Artifact (Red)",
		[8]= "Quest"
	},
	timerArr= {},
	hooked= false,
	lastUpdate= 0,
	itemValueSupport= 0,
	hasFocus= 0;
};


LootFilter.print= function (value)
	if (value == nil) then
		value= "";
	end;
	DEFAULT_CHAT_FRAME:AddMessage("Loot Filter - "..value, 1.0, 1.0, 1.0);
end;

LootFilter.schedule= function (delay, func, ...)
	table.insert(LootFilter.timerArr, {time=time()+delay, func=func, args=arg});
	if (not LootFilter.hooked) then
		LootFilter.hook();
	end;
end;

LootFilter.WorldFrame_OnUpdate= function ()
	Original_WorldFrame_OnUpdate();
	local curtime= time();
	if (LootFilter.lastUpdate < curtime) then
		if (table.getn(LootFilter.timerArr) == 0) then
			LootFilter.unhook();
			return;
		end;
		for i=1, table.getn(LootFilter.timerArr), 1 do
			if (LootFilter.timerArr[i].time <= curtime) then
				func= LootFilter.timerArr[i].func;
				args= LootFilter.timerArr[i].args;
				table.remove(LootFilter.timerArr,i);
				func(unpack(args));
				return;
			end;
		end;
		LootFilter.lastUpdate= curtime+1;
	end;
end;

LootFilter.hook= function ()
	Original_WorldFrame_OnUpdate= WorldFrame_OnUpdate;
	WorldFrame_OnUpdate= LootFilter.WorldFrame_OnUpdate;
	LootFilter.hooked= true;
end;

LootFilter.unhook= function ()
	WorldFrame_OnUpdate= Original_WorldFrame_OnUpdate;
	LootFilter.hooked= false;
end;

LootFilter.banner= function (value)
end;

LootFilter.setTitle= function()
	LootFilterFrameTitleText:SetText("Loot Filter v"..LootFilter.VERSION);
end;

LootFilter.matches= function(itemName, searchName)
	if (itemName == nil) or (searchName == nil) then
		return false;
	end;
	if (string.find(searchName, "#", 1, true) == 1) then
		if (string.find(string.lower(itemName), string.lower(string.sub(searchName, 2)))) then
			return true;
		end;
	elseif (string.lower(itemName) == string.lower(searchName)) then
		return true;
	end;
	return false;
end;

LootFilter.deleteItemFromInventory= function (searchItem)
	local item= searchItem;
	local pipe= string.find(item, "|", 1, true);
	searchItem= string.sub(item, 1, pipe-1);
	searchName= string.sub(item, pipe+1);
	for j=0 , 4 , 1 do
		x = GetContainerNumSlots(j);
		for i=0 , x , 1 do
			local item = GetContainerItemInfo(j, i);
			if LootFilter.matches(item, searchItem) then
				local itemName= "";
				local itemlink= GetContainerItemLink(j,i);
				if (itemlink ~= nil) then
					for a,itemid in string.gfind(itemlink, "([^:]+):(%w+)") do
						itemid= tonumber(itemid);
						if (itemid >= 1) then
							local itemName, itemLink, itemRarity, itemMinLevel, itemType, itemSubType, itemStackCount = GetItemInfo(itemid)
							if LootFilter.matches(itemName, searchName) then
								local delete= false;

								for i=1, table.getn(LootFilterVars[LootFilter.REALMPLAYER].namesdelete), 1 do
									if LootFilter.matches(searchName, LootFilterVars[LootFilter.REALMPLAYER].namesdelete[i]) then
										delete= true;
										break;
									end;
								end;

								if (not delete) then
									if (LootFilterVars[LootFilter.REALMPLAYER].qualities[8]) then
										if (itemType == "Quest") then -- this is a quest item remove from stack and leave it
											table.remove(LootFilterVars[LootFilter.REALMPLAYER].itemStack, 1);
											return true;
										end;
									end;
									
						
									if (LootFilterVars[LootFilter.REALMPLAYER].filterItemValue == true) and 
										(LootFilterVars[LootFilter.REALMPLAYER].itemValue ~= nil)  and 
										(LootFilterVars[LootFilter.REALMPLAYER].itemValue ~= "")  and 
										(LootFilter.itemValueSupport > 0) then

										local value= -1;
										
										if (LootFilter.itemValueSupport == 1) then
											itemData = Informant.GetItem(itemid);
											if (itemData ~= nil) and (itemData['sell'] ~= nil) then
												value = tonumber(itemData['sell']);
											end;
										elseif (LootFilter.itemValueSupport == 2) and (ItemLinks) then
											if (ItemLinks[itemName]) then
												value= tonumber(ItemLinks[itemName].p);
											end;
										elseif (LootFilter.itemValueSupport == 3) and (SellValues) then
											itemShortName= InvList_ShortenItemName(itemName);
											if (SellValues[itemShortName]) then
												value= tonumber(SellValues[itemShortName]);
											end;
										elseif (LootFilter.itemValueSupport == 4) and (PackRatDB["items"]) then
											if (PackRatDB["items"][tostring(itemid)]) then
												value= tonumber(PackRatDB["items"][tostring(itemid)]["p"]);
											end;
										end;
										if (value ~= nil) and (value > 0) then
											if (itemStackCount ~= nil) and (itemStackCount > 1) then
												value= value * itemStackCount;
											end;
											value= tonumber(value / 10000);
											if (tonumber(value) > tonumber(LootFilterVars[LootFilter.REALMPLAYER].itemValue)) then
												table.remove(LootFilterVars[LootFilter.REALMPLAYER].itemStack, 1);
												return true;
											end;
										end;
										if (value == -1) then -- no item value found
											table.remove(LootFilterVars[LootFilter.REALMPLAYER].itemStack, 1);
											return true;
										end;
									end;


									if (LootFilterVars[LootFilter.REALMPLAYER].qualities[itemRarity+1]) then
										table.remove(LootFilterVars[LootFilter.REALMPLAYER].itemStack, 1);
										return true;
									end;
								end;

								PickupContainerItem(j, i);
								if (CursorHasItem()) then
									DeleteCursorItem();
									table.remove(LootFilterVars[LootFilter.REALMPLAYER].itemStack, 1);
									if (LootFilterVars[LootFilter.REALMPLAYER].notifydelete) then
										LootFilter.print("Item "..itemlink.." was deleted.");
									end;
									return true;
								else
									return false;
								end;
				
							end;

						end;
					end;
				else -- item got moved?
					return false; 
				end;
			end
		end
	end;
	return false;
end

LootFilter.lootValid= function (name, quality)
	if (name == nil) then
		return true;
	end;
	for i=1, table.getn(LootFilterVars[LootFilter.REALMPLAYER].names), 1 do
		if LootFilter.matches(name, LootFilterVars[LootFilter.REALMPLAYER].names[i]) then
			return true;
		end;
	end;
	return false;
end;

LootFilter.findItems= function (maxtime)
	numitems= table.getn(LootFilterVars[LootFilter.REALMPLAYER].itemStack);
	if (numitems == 0) then
		return;
	end;

	if (time() > maxtime) then -- timeout, the item could not be found remove it from stack
		table.remove(LootFilterVars[LootFilter.REALMPLAYER].itemStack, 1);
		LootFilter.schedule(1, LootFilter.findItems, time()+LootFilter.LOOT_TIMEOUT);
		return;
	end;

	if (LootFilter.deleteItemFromInventory(LootFilterVars[LootFilter.REALMPLAYER].itemStack[1])) then -- loot succesful
		LootFilter.schedule(1, LootFilter.findItems, time()+LootFilter.LOOT_TIMEOUT);
	else -- could not find loot, lets try again
		LootFilter.schedule(1, LootFilter.findItems, maxtime);
	end;
end


LootFilter.getNames= function ()
	local result= "";
	for i=1, table.getn(LootFilterVars[LootFilter.REALMPLAYER].names), 1 do
		result= result..LootFilterVars[LootFilter.REALMPLAYER].names[i].."\n";
	end;
	this:SetText(result);
end;

LootFilter.getNamesDelete= function ()
	local result= "";
	for i=1, table.getn(LootFilterVars[LootFilter.REALMPLAYER].namesdelete), 1 do
		result= result..LootFilterVars[LootFilter.REALMPLAYER].namesdelete[i].."\n";
	end;
	this:SetText(result);
end;

LootFilter.setNames= function ()
	LootFilterVars[LootFilter.REALMPLAYER].names= {};
	local result= LootFilterEditBox1:GetText().."\n";
	if (result ~= nil) then
		for w in string.gfind(result, "[^\n]+\n") do
			w = string.gsub(w, "\n", "");
			table.insert(LootFilterVars[LootFilter.REALMPLAYER].names, w);
		end;
	end;
end;
LootFilter.setNamesDelete= function ()
	LootFilterVars[LootFilter.REALMPLAYER].namesdelete= {};
	local result= LootFilterEditBox2:GetText().."\n";
	if (result ~= nil) then
		for w in string.gfind(result, "[^\n]+\n") do
			w = string.gsub(w, "\n", "");
			table.insert(LootFilterVars[LootFilter.REALMPLAYER].namesdelete, w);
		end;
	end;
end;

LootFilter.setItemValue= function ()
	local value= tonumber(LootFilterEditBox3:GetText());
	if (value == nil) or (value == 0) or (value == "") then
		value= "";
		LootFilterCheckboxItemValue:SetChecked(false);
		LootFilterVars[LootFilter.REALMPLAYER].filterItemValue= false;
	end;
	LootFilterVars[LootFilter.REALMPLAYER].itemValue= value;
end;
LootFilter.getItemValue= function ()
	if (LootFilterVars[LootFilter.REALMPLAYER].itemValue == nil) or (LootFilterVars[LootFilter.REALMPLAYER].itemValue == "") then
		value= "";
		LootFilterCheckboxItemValue:SetChecked(false);
		LootFilterVars[LootFilter.REALMPLAYER].filterItemValue= false;
	else
		value= LootFilterVars[LootFilter.REALMPLAYER].itemValue;
	end;
	this:SetText(value);
end;


LootFilter.command= function (cmd)
	args= {};
	i= 1;
	for w in string.gfind(cmd, "%w+") do
		args[i]= w;
		i= i + 1;
	end;

	if (table.getn(args) == 0) then
		table.sort(LootFilterVars[LootFilter.REALMPLAYER].namesdelete);
		table.sort(LootFilterVars[LootFilter.REALMPLAYER].names)
		if (not LootFilterOptions:IsShown()) then
			LootFilterOptions:Show();
		else
			LootFilterOptions:Hide();
		end;
		return;
	end;

	if (args[1] == "on") then
		LootFilterVars[LootFilter.REALMPLAYER].enabled= true;
		LootFilter.print("Loot Filter turned on.");
		return;
	end;
	if (args[1] == "off") then
		LootFilterVars[LootFilter.REALMPLAYER].enabled= false;
		LootFilter.print("Loot Filter turned off.");
		return;
	end;
	if (args[1] == "notify") then
		if (LootFilterVars[LootFilter.REALMPLAYER].notifydelete) then
			LootFilterVars[LootFilter.REALMPLAYER].notifydelete= false;
			LootFilter.print("Notify on delete has been turned off.");
		else
			LootFilterVars[LootFilter.REALMPLAYER].notifydelete= true;
			LootFilter.print("Notify on delete has been turned on.");
		end;
		return;
	end;
	if (args[1] == "status") then
		if (LootFilterVars[LootFilter.REALMPLAYER].enabled) then
			LootFilter.print("Version "..LootFilter.VERSION.." is turned on.");
		else
			LootFilter.print("Version "..LootFilter.VERSION.." is turned off.");
		end;
		if (LootFilterVars[LootFilter.REALMPLAYER].notifydelete) then
			LootFilter.print("Notify on delete is turned on.");
		else
			LootFilter.print("Notify on delete is turned off.");
		end;
		for i=1, table.getn(LootFilterVars[LootFilter.REALMPLAYER].qualities), 1 do
			if (LootFilterVars[LootFilter.REALMPLAYER].qualities[i]) then
				LootFilter.print(i..". "..LootFilter.qualities[i].." [Not filtered]");
			else
				LootFilter.print(i..". "..LootFilter.qualities[i].." [Filtered]");
			end;
		end;
		if (table.getn(LootFilterVars[LootFilter.REALMPLAYER].names) ~= 0) then
			LootFilter.print("Items that contain the following (partial) names will be kept:");
			for i=1, table.getn(LootFilterVars[LootFilter.REALMPLAYER].names), 1 do
				LootFilter.print(i..". "..LootFilterVars[LootFilter.REALMPLAYER].names[i].." [Not filtered]");
			end;
		end;
		if (table.getn(LootFilterVars[LootFilter.REALMPLAYER].namesdelete) ~= 0) then
			LootFilter.print("Items that contain the following (partial) names will be deleted:");
			for i=1, table.getn(LootFilterVars[LootFilter.REALMPLAYER].namesdelete), 1 do
				LootFilter.print(i..". "..LootFilterVars[LootFilter.REALMPLAYER].namesdelete[i].." [Filtered]");
			end;
		end;
		return;
	end;

	if (args[1] == "quality") then
		if (args[2] ~= nil) then
			local number= tonumber(args[2]);
			if (LootFilterVars[LootFilter.REALMPLAYER].qualities[number] ~= nil) then
				if (LootFilterVars[LootFilter.REALMPLAYER].qualities[number]) then
					LootFilterVars[LootFilter.REALMPLAYER].qualities[number]= false;
					LootFilter.print("Set "..LootFilter.qualities[number].." to [Filtered]");
				else
					LootFilterVars[LootFilter.REALMPLAYER].qualities[number]= true;
					LootFilter.print("Set "..LootFilter.qualities[number].." to [Not Filtered]");
				end;
			end;
			return;
		end;
	end;

	if (args[1] == "item") then
		if (args[2] ~= nil) then
			local number= tonumber(args[2]);
			if (args[2] == tostring(number)) then -- remove item from list with this number
				if (LootFilterVars[LootFilter.REALMPLAYER].names[number] ~= nil) then
					LootFilter.print("Item "..LootFilterVars[LootFilter.REALMPLAYER].names[number].." removed.");
					table.remove(LootFilterVars[LootFilter.REALMPLAYER].names, number);
				end;
			else -- add the string
				local result= "";
				for i=2, table.getn(args), 1 do
					if (result == "") then
						result= args[i];
					else
						result= result.." "..args[i];
					end;
				end;
				table.insert(LootFilterVars[LootFilter.REALMPLAYER].names, result);
				LootFilter.print("Item "..result.." added to keep list.");
			end;
			return;
		end;
	end;
	if (args[1] == "itemd") then
		if (args[2] ~= nil) then
			local number= tonumber(args[2]);
			if (args[2] == tostring(number)) then -- remove item from list with this number
				if (LootFilterVars[LootFilter.REALMPLAYER].namesdelete[number] ~= nil) then
					LootFilter.print("Item "..LootFilterVars[LootFilter.REALMPLAYER].namesdelete[number].." removed.");
					table.remove(LootFilterVars[LootFilter.REALMPLAYER].namesdelete, number);
				end;
			else -- add the string
				local result= "";
				for i=2, table.getn(args), 1 do
					if (result == "") then
						result= args[i];
					else
						result= result.." "..args[i];
					end;
				end;
				table.insert(LootFilterVars[LootFilter.REALMPLAYER].namesdelete, result);
				LootFilter.print("Item "..result.." added delete list.");
			end;
			return;
		end;
	end;

	LootFilter.showHelp();

end;

LootFilter.getOption= function (num)
	local labelString = getglobal(this:GetName().."Text");
	local label= "";
	if (num == 99) then
		this:SetChecked(LootFilterVars[LootFilter.REALMPLAYER].enabled);
		label= "Enable Loot Filter";
	elseif (num == 199) then
		this:SetChecked(LootFilterVars[LootFilter.REALMPLAYER].notifydelete);
		label= "Notify on delete";
	elseif (num == 299) then
		this:SetChecked(LootFilterVars[LootFilter.REALMPLAYER].filterItemValue);
		label= "Item value (Gold):";
	elseif (LootFilterVars[LootFilter.REALMPLAYER].qualities[num] ~= nil) then
		if (LootFilterVars[LootFilter.REALMPLAYER].qualities[num]) then
			this:SetChecked(false);
		else
			this:SetChecked(true);
		end;
		label= LootFilter.qualities[num].." items";
	end;

	labelString:SetText(label);
end;

LootFilter.setOption= function(num)
	local check;
	if (this:GetChecked()) then
		check= true;
	else
		check= false;
	end;
	if (num == 99) then
		LootFilterVars[LootFilter.REALMPLAYER].enabled= check;
	elseif (num == 199) then
		LootFilterVars[LootFilter.REALMPLAYER].notifydelete= check;
	elseif (num == 299) then
		LootFilterVars[LootFilter.REALMPLAYER].filterItemValue= check;
	elseif (LootFilterVars[LootFilter.REALMPLAYER].qualities[num] ~= nil) then
		if (check) then
			LootFilterVars[LootFilter.REALMPLAYER].qualities[num]= false;
		else
			LootFilterVars[LootFilter.REALMPLAYER].qualities[num]= true;
		end;
	end;
end;

LootFilter.showHelp= function ()
	LootFilter.print("Loot Filter "..LootFilter.VERSION.." usage:");
	LootFilter.print("/lf on/off , turns filtering on or off");
	LootFilter.print("/lf notify , turn notify on delete on or off");
	LootFilter.print("/lf status , shows you all filter information");
	LootFilter.print("/lf item <string> , adds a string that should not be filtered");
	LootFilter.print("/lf item <number> , removes a string that should not be filtered");
	LootFilter.print("/lf itemd <string> , adds a string that should be filtered");
	LootFilter.print("/lf itemd <number> , removes a string that should be filtered");
	LootFilter.print("/lf quality <number> , toggles quality filtering");
end;

LootFilter.updateFocus= function (num , value)
	if (value) then
		this:SetFocus();
		LootFilter.hasFocus= num;
	else
		this:ClearFocus();
		LootFilter.hasFocus= 0;
	end;
end;

LootFilter.findItemWithLock= function ()
	for j=0 , 4 , 1 do
		x = GetContainerNumSlots(j);
		for i=0 , x , 1 do
			local _, _, locked = GetContainerItemInfo(j,i);
			if (locked) then
				local itemlink= GetContainerItemLink(j,i);
				if (itemlink ~= nil) then
					for a,itemid in string.gfind(itemlink, "([^:]+):(%w+)") do
						itemid= tonumber(itemid);
						if (itemid >= 1) then
							local itemName = GetItemInfo(itemid);
							return itemName;
						end;
					end;
				end;
			end;
		end;
	end;
	return "";
end;

LootFilter.OnEvent= function ()

	if (event == "LOOT_OPENED") and (LootFilterVars[LootFilter.REALMPLAYER].enabled) then
		numitems= GetNumLootItems();
		for i=1, numitems, 1 do
			if (not LootSlotIsCoin(i)) then
				icon, name, quantity, quality= GetLootSlotInfo(i);
				if (icon ~= nil) then
					if (not LootFilter.lootValid(name, quality)) then
						table.insert(LootFilterVars[LootFilter.REALMPLAYER].itemStack, icon.."|"..name);
					end;
				end;
			end;
		end;
	end;

	if (event == "LOOT_CLOSED") then
		LootFilter.schedule(LootFilter.LOOT_PARSE_DELAY, LootFilter.findItems, time()+LootFilter.LOOT_TIMEOUT);
	end;

	if (event == "ITEM_LOCK_CHANGED") then
		if (LootFilter.hasFocus > 0) then
			itemName= LootFilter.findItemWithLock();
			if (itemName ~= nil) and (itemName ~= "") then
				if (LootFilter.hasFocus == 1) then
					LootFilterEditBox1:SetText(LootFilterEditBox1:GetText()..itemName.."\n");
				elseif (LootFilter.hasFocus == 2) then
					LootFilterEditBox2:SetText(LootFilterEditBox2:GetText()..itemName.."\n");
				end;
			end;
		end;
	end;

	if (event == "ADDON_LOADED") then

		if (arg1 == "LootFilter") then
			LootFilter.REALMPLAYER= GetCVar("realmName")..UnitName("player");

			if (LootFilterVars[LootFilter.REALMPLAYER] == nil) then
				LootFilterVars[LootFilter.REALMPLAYER]= {};
			end;

			if (LootFilterVars[LootFilter.REALMPLAYER].qualities == nil) then
				if (LootFilterVars.qualities ~= nil) then
					LootFilterVars[LootFilter.REALMPLAYER].qualities= LootFilterVars.qualities;
					LootFilterVars.qualities= nil;
				else
					LootFilterVars[LootFilter.REALMPLAYER].qualities= {};
				end;
			end;
			if (LootFilterVars[LootFilter.REALMPLAYER].names == nil) then
				if (LootFilterVars.names ~= nil) then
					LootFilterVars[LootFilter.REALMPLAYER].names= LootFilterVars.names;
					LootFilterVars.names= nil;
				else
					LootFilterVars[LootFilter.REALMPLAYER].names= {};
				end;
			end;
			if (LootFilterVars[LootFilter.REALMPLAYER].namesdelete == nil) then
				LootFilterVars[LootFilter.REALMPLAYER].namesdelete= {};
			end;
			if (LootFilterVars[LootFilter.REALMPLAYER].itemStack == nil) then
				if (LootFilterVars.itemStack ~= nil) then
					LootFilterVars[LootFilter.REALMPLAYER].itemStack= LootFilterVars.itemStack;
					LootFilterVars.itemStack= nil;
				else
					LootFilterVars[LootFilter.REALMPLAYER].itemStack= {};
				end;
			end;
			if (LootFilterVars[LootFilter.REALMPLAYER].enabled == nil) then
				if (LootFilterVars.enabled ~= nil) then
					LootFilterVars[LootFilter.REALMPLAYER].enabled= LootFilterVars.enabled;
					LootFilterVars.enabled= nil;
				else
					LootFilterVars[LootFilter.REALMPLAYER].enabled= true;
				end;
			end;
			if (LootFilterVars[LootFilter.REALMPLAYER].notifydelete == nil) then
				LootFilterVars[LootFilter.REALMPLAYER].notifydelete= true;
			end;
			if (LootFilterVars[LootFilter.REALMPLAYER].filterItemValue == nil) then
				LootFilterVars[LootFilter.REALMPLAYER].filterItemValue= false;
			end;
			if (LootFilterVars[LootFilter.REALMPLAYER].itemValue == nil) then
				LootFilterVars[LootFilter.REALMPLAYER].itemValue= "";
			end;
			for i=1, 8, 1 do
				if (LootFilterVars[LootFilter.REALMPLAYER].qualities[i] == nil) then
					LootFilterVars[LootFilter.REALMPLAYER].qualities[i]= true;
				end;
			end;
		end;
		LootFilter.checkDepencies();
	end;
end;

LootFilter.checkDepencies= function ()
	if (IsAddOnLoaded("Informant")) then
		LootFilter.itemValueSupport= 1; -- Informant
	elseif (IsAddOnLoaded("LootLink")) then
		LootFilter.itemValueSupport= 2; -- LootLink
	elseif (IsAddOnLoaded("SellValue")) then
		LootFilter.itemValueSupport= 3; -- SellValue
	elseif (IsAddOnLoaded("PackRat")) then
		LootFilter.itemValueSupport= 4; -- PackRat
	end;
	if (LootFilter.itemValueSupport ~= 0) then
		LootFilterCheckboxItemValue:Show();
		LootFilterEditBox3:Show();
		LootFilterTextBackground3:Show();
	end;
end;

LootFilter.OnLoad= function ()

	SLASH_LOOTFILTER1= "/lootfilter";
	SLASH_LOOTFILTER2= "/lf";
	SLASH_LOOTFILTER3= "/lfr";
	SlashCmdList["LOOTFILTER"] = LootFilter.command;

	this:RegisterEvent("LOOT_OPENED");
	this:RegisterEvent("LOOT_CLOSED");
	this:RegisterEvent("ADDON_LOADED");
	this:RegisterEvent("ITEM_LOCK_CHANGED");
end;

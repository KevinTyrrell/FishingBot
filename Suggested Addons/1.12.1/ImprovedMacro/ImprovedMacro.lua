ImprovedMacro_SetItemSlot = nil;

function ImprovedMacro_OnLoad()
	IMCMD_OnLoad();
end

------------------------------------------
-----
----- Macros
-----
------------------------------------------
function ImprovedMacro_GetItemName(bag, slot)
	local bagNumber = bag;
	ImprovedMacroTooltip:SetOwner(UIParent, "ANCHOR_NONE");
	if ( type(bagNumber) ~= "number" ) then
		bagNumber = tonumber(bag);
	end
	if (bagNumber <= -1) then
		ImprovedMacroTooltip:SetInventoryItem("player", slot);
	else
		ImprovedMacroTooltip:SetBagItem(bag, slot);
	end
	return ImprovedMacroTooltipTextLeft1:GetText();
end

------------------------------------------
-----
----- /Commands
-----
------------------------------------------

EquipmentSets = { };

function IMCMD_OnLoad()
	if (Cosmos_RegisterChatCommand and Hanul_InitLoaded) then
		id = "LEAVEPARTY";
		func = function(msg) LeaveParty(); end
		Cosmos_RegisterChatCommand ( id, IMCMD_LEAVE_COMM, func, IMCMD_LEAVE_COMM_INFO );

		id = "TEQUIP";
		func = function(msg) Equip(msg); end
		Cosmos_RegisterChatCommand ( id, IMCMD_EQUIP_COMM, func, IMCMD_EQUIP_COMM_INFO );

		id = "TEQUIP_OFFHAND";
		func = function(msg) EquipOffhand(msg); end
		Cosmos_RegisterChatCommand ( id, IMCMD_EQUIP_OH_COMM, func, IMCMD_EQUIP_OH_COMM_INFO );

		id = "TUNEQUIP";
		func = function(msg) Unequip(msg); end
		Cosmos_RegisterChatCommand ( id, IMCMD_UNEQUIP_COMM, func, IMCMD_UNEQUIP_COMM_INFO );

		id = "TUSE";
		func = function(msg) UseItem(msg); end
		Cosmos_RegisterChatCommand ( id, IMCMD_USE_COMM, func, IMCMD_USE_COMM_INFO );

		id = "TUSETYPE";
		func = function(msg) UseItemByType(msg); end
		Cosmos_RegisterChatCommand ( id, IMCMD_USETYPE_COMM, func, IMCMD_USETYPE_COMM_INFO );

		id = "TSSET";
		func = function(msg) SaveSet(msg); end
		Cosmos_RegisterChatCommand ( id, IMCMD_SSET_COMM, func, IMCMD_SSET_COMM_INFO );

		id = "TLSET";
		func = function(msg) LoadSet(msg); end
		Cosmos_RegisterChatCommand ( id, IMCMD_LSET_COMM, func, IMCMD_LSET_COMM_INFO );

		id = "TLISETS";
		func = function() ListSets(); end
		Cosmos_RegisterChatCommand ( id, IMCMD_LISTSETS_COMM, func, IMCMD_LISTSETS_COMM_INFO );

		id = "TDSETS";
		func = function(msg) DelSet(msg); end
		Cosmos_RegisterChatCommand ( id, IMCMD_DSET_COMM, func, IMCMD_DSET_COMM_INFO );
	else
		SlashCmdList["LEAVEPARTY"] = function() LeaveParty(); end
		for i = 1, table.getn(IMCMD_LEAVE_COMM) do setglobal("SLASH_LEAVEPARTY"..i, IMCMD_LEAVE_COMM[i]);  end

		SlashCmdList["EQUIP"] = function(msg) Equip(msg); end
		for i = 1, table.getn(IMCMD_EQUIP_COMM) do setglobal("SLASH_EQUIP"..i, IMCMD_EQUIP_COMM[i]);  end
		
		SlashCmdList["EQUIP_OFFHAND"] = function(msg) EquipOffhand(msg); end
		for i = 1, table.getn(IMCMD_EQUIP_OH_COMM) do setglobal("SLASH_EQUIP_OFFHAND"..i, IMCMD_EQUIP_OH_COMM[i]); end
		
		SlashCmdList["UNEQUIP"] = function(msg) Unequip(msg); end
		for i = 1, table.getn(IMCMD_UNEQUIP_COMM) do setglobal("SLASH_UNEQUIP"..i, IMCMD_UNEQUIP_COMM[i]); end
		
		SlashCmdList["USE"] = function(msg) UseItem(msg); end
		for i = 1, table.getn(IMCMD_USE_COMM) do setglobal("SLASH_USE"..i, IMCMD_USE_COMM[i]); end
		
		SlashCmdList["USETYPE"] = function(msg) UseItemByType(msg); end
		for i = 1, table.getn(IMCMD_USETYPE_COMM) do setglobal("SLASH_USETYPE"..i, IMCMD_USETYPE_COMM[i]); end
		
		SlashCmdList["SSET"] = function(msg) SaveSet(msg); end
		for i = 1, table.getn(IMCMD_SSET_COMM) do setglobal("SLASH_SSET"..i, IMCMD_SSET_COMM[i]); end
		
		SlashCmdList["LSET"] = function(msg) LoadSet(msg); end
		for i = 1, table.getn(IMCMD_LSET_COMM) do setglobal("SLASH_LSET"..i, IMCMD_LSET_COMM[i]); end
		
		SlashCmdList["TLISETS"] = function() ListSets(); end
		for i = 1, table.getn(IMCMD_LISTSETS_COMM) do setglobal("SLASH_TLISETS"..i, IMCMD_LISTSETS_COMM[i]); end

		SlashCmdList["TDSETS"] = function(msg) DelSet(msg); end
		for i = 1, table.getn(IMCMD_DSET_COMM) do setglobal("SLASH_TDSETS"..i, IMCMD_DSET_COMM[i]); end
	end
end

function UseItem(msg, numberOfTimes)
	if ( ( not msg ) or ( strlen(msg) == 0 ) ) then
		DEFAULT_CHAT_FRAME:AddMessage(IMCMD_USE_COMM_INFO);
		return;
	end
	local usedNumberOfTimes = 0;
	if ( not numberOfTimes ) then
		numberOfTimes = 1;
	end
	for i = 1, 19, 1 do
		if (ImprovedMacro_GetItemName(-1, i) == msg) then
			UseInventoryItem(y);
			usedNumberOfTimes = usedNumberOfTimes + 1;
			if ( usedNumberOfTimes >= numberOfTimes ) then
				return true;
			end
		end
	end
	for i = 0, 4, 1 do
		local numSlot = GetContainerNumSlots(i);
		for y = 1, numSlot, 1 do
			if (ImprovedMacro_GetItemName(i, y) == msg) then
				UseContainerItem(i, y);
				usedNumberOfTimes = usedNumberOfTimes + 1;
				if ( usedNumberOfTimes >= numberOfTimes ) then
					return true;
				end
			end
		end
	end
	DEFAULT_CHAT_FRAME:AddMessage(format(IMCMD_ERROR_UNKITEM, msg));
	return false;
end

function UseItemByType(msg, numberOfTimes)
	if ( ( not msg ) or ( strlen(msg) == 0 ) ) then
		DEFAULT_CHAT_FRAME:AddMessage(IMCMD_USETYPE_COMM_INFO);
		return;
	end
	local usedNumberOfTimes = 0;
	if ( not numberOfTimes ) then
		numberOfTimes = 1;
	end
	for i = 0, 4, 1 do
		local numSlot = GetContainerNumSlots(i);
		for y = 1, numSlot, 1 do
			if (Sea.wow.item.classifyInventoryItem(i, y) == msg) then
				UseContainerItem(i, y);
				usedNumberOfTimes = usedNumberOfTimes + 1;
				if ( usedNumberOfTimes >= numberOfTimes ) then
					return true;
				end
			end
		end
	end
	DEFAULT_CHAT_FRAME:AddMessage(format(IMCMD_ERROR_NOITEM, msg));
	return false;
end

function GetCurrentSet()
	local table = { };
	for i = 1, 19, 1 do
		table[i] = ImprovedMacro_GetItemName(-1, i);
	end
	return table;
end

function SaveSet(msg)
	if(msg and msg ~= "") then
		if (EquipmentSets[UnitName("player")] == nil ) then 
			EquipmentSets[UnitName("player")] = { };
		end
		EquipmentSets[UnitName("player")][msg] = GetCurrentSet();
		DEFAULT_CHAT_FRAME:AddMessage(format(IMCMD_ERROR_SAVED, msg));
	else
		DEFAULT_CHAT_FRAME:AddMessage(IMCMD_SSET_COMM_INFO);
	end
end

function LoadSet(msg)
	local playername = UnitName("player");
	if(msg and msg ~= "") then
		if (not EquipmentSets[playername][msg]) then
			DEFAULT_CHAT_FRAME:AddMessage(format(IMCMD_ERROR_UNKSET, msg));
			return;
		end
		if (EquipmentSets[playername][msg][16] == ImprovedMacro_GetItemName(-1, 17) and EquipmentSets[playername][msg][17] ~= ImprovedMacro_GetItemName(-1, 16)) then
			PickupInventoryItem(16);
			BagItem();
			PickupInventoryItem(17);
			PickupInventoryItem(16);
		elseif (EquipmentSets[playername][msg][16] == ImprovedMacro_GetItemName(-1, 17)) then
			PickupInventoryItem(17);
			PickupInventoryItem(16);
		elseif (EquipmentSets[playername][msg][17] == ImprovedMacro_GetItemName(-1, 16)) then
			PickupInventoryItem(16);
			PickupInventoryItem(17);
		end
		
		if (EquipmentSets[playername][msg][12] == ImprovedMacro_GetItemName(-1, 11) and EquipmentSets[playername][msg][11] ~= ImprovedMacro_GetItemName(-1, 12)) then
			PickupInventoryItem(12);
			PickupInventoryItem(11);
			PickupInventoryItem(12);
		elseif (EquipmentSets[playername][msg][12] == ImprovedMacro_GetItemName(-1, 11)) then
			PickupInventoryItem(11);
			PickupInventoryItem(12);
		elseif (EquipmentSets[playername][msg][11] == ImprovedMacro_GetItemName(-1, 12) and EquipmentSets[playername][msg][12] ~= ImprovedMacro_GetItemName(-1, 11)) then
			ImprovedMacro_SetItemSlot = 12;
			EquipSecondryslots(EquipmentSets[playername][msg][12], 1);
			Equip(EquipmentSets[playername][msg][11], 1);
		elseif (EquipmentSets[playername][msg][11] == ImprovedMacro_GetItemName(-1, 12)) then
			PickupInventoryItem(12);
			PickupInventoryItem(11);
		end

		if (EquipmentSets[playername][msg][13] == ImprovedMacro_GetItemName(-1, 14) and EquipmentSets[playername][msg][14] ~= ImprovedMacro_GetItemName(-1, 13)) then
			PickupInventoryItem(13);
			PickupInventoryItem(14);
			PickupInventoryItem(13);
		elseif (EquipmentSets[playername][msg][13] == ImprovedMacro_GetItemName(-1, 14)) then
			PickupInventoryItem(14);
			PickupInventoryItem(13);
		elseif (EquipmentSets[playername][msg][14] == ImprovedMacro_GetItemName(-1, 13) and EquipmentSets[playername][msg][13] ~= ImprovedMacro_GetItemName(-1, 14)) then
			ImprovedMacro_SetItemSlot = 14;
			EquipSecondryslots(EquipmentSets[playername][msg][14], 1);
			Equip(EquipmentSets[playername][msg][13], 1);
		elseif (EquipmentSets[playername][msg][14] == ImprovedMacro_GetItemName(-1, 13)) then
			PickupInventoryItem(13);
			PickupInventoryItem(14);
		end

		freeslots = nil;
		local item2bag_num = Get_Item2Bag(playername, msg);

		for i = 1, 19, 1 do
			if (not EquipmentSets[playername][msg][i] or EquipmentSets[playername][msg][i] == "") then 
				PickupInventoryItem(i);
				if item2bag_num >= 2 and CursorHasItem() then
					BagItem2();
				else
					BagItem(i);
				end
			elseif (EquipmentSets[playername][msg][i] ~= ImprovedMacro_GetItemName(-1, i)) then
				if (i == 17) then
					ImprovedMacro_SetItemSlot = 17;
					EquipSecondryslots(EquipmentSets[playername][msg][17], 1);
				elseif (i == 12) then
					ImprovedMacro_SetItemSlot = 12;
					EquipSecondryslots(EquipmentSets[playername][msg][12], 1);
				elseif (i == 14) then
					ImprovedMacro_SetItemSlot = 14;
					EquipSecondryslots(EquipmentSets[playername][msg][14], 1);
				else
					Equip(EquipmentSets[playername][msg][i]);
				end
			end
		end
		if(CursorHasItem()) then
			BagItem();
		end
		DEFAULT_CHAT_FRAME:AddMessage(format(IMCMD_ERROR_LOADED, msg));
	else
		DEFAULT_CHAT_FRAME:AddMessage(IMCMD_LSET_COMM_INFO);
	end
end

function ListSets()
	local playername = UnitName("player");
	if ( not EquipmentSets[playername] or not EquipmentSets ) then
		DEFAULT_CHAT_FRAME:AddMessage(IMCMD_ERROR_UNKLIST);
		return;
	end
	DEFAULT_CHAT_FRAME:AddMessage(IMCMD_TLIST_HEADER);
	for key,value in EquipmentSets[playername] do
		DEFAULT_CHAT_FRAME:AddMessage("- "..key);
	end
end

function DelSet(msg)
	local playername = UnitName("player");
	if(not msg or msg == "") then
		DEFAULT_CHAT_FRAME:AddMessage(IMCMD_DSET_COMM_INFO);
		return;
	end
	if(not EquipmentSets[playername][msg]) then
		DEFAULT_CHAT_FRAME:AddMessage(format(IMCMD_ERROR_UNKSET, msg));
		return;
	end
	EquipmentSets[playername][msg] = nil;
	DEFAULT_CHAT_FRAME:AddMessage(format(IMCMD_ERROR_DELETED, msg));
end

function Equip(msg, quiet)
	if(not msg or msg == "") then
		if (not quiet) then
			DEFAULT_CHAT_FRAME:AddMessage(IMCMD_EQUIP_COMM_INFO);
		end
		return;
	end
	if(CursorHasItem()) then
		if (not quiet) then
			DEFAULT_CHAT_FRAME:AddMessage(IMCMD_ERROR_HOLDING);
		end
		return;
	end
	for i = 0, 4, 1 do
		local numSlot = GetContainerNumSlots(i);
		for y = 1, numSlot, 1 do
			if (ImprovedMacro_GetItemName(i, y) == msg) then
				PickupContainerItem(i,y);
				AutoEquipCursorItem();
				return true;
			end
		end
	end
	
	if (not quiet) then
		DEFAULT_CHAT_FRAME:AddMessage(format(IMCMD_ERROR_UNKITEM, msg));
	end
	return;
end

function EquipOffhand(msg, quiet)
	if(not msg or msg == "") then
		if (not quiet) then
			DEFAULT_CHAT_FRAME:AddMessage(IMCMD_EQUIP_COMM_INFO);
		end
		return;
	end
	if(CursorHasItem()) then
		if (not quiet) then
			DEFAULT_CHAT_FRAME:AddMessage(IMCMD_ERROR_HOLDING);
		end
		return;
	end
	for i = 0, 4, 1 do
		local numSlot = GetContainerNumSlots(i);
		for y = 1, numSlot, 1 do
			if (ImprovedMacro_GetItemName(i, y) == msg) then
				PickupContainerItem(i,y);
				PickupInventoryItem(17);
				if(CursorHasItem()) then
					PickupContainerItem(i,y);
				end
				return true;
			end
		end
	end
	
	if (not quiet) then
		DEFAULT_CHAT_FRAME:AddMessage(format(IMCMD_ERROR_UNKITEM, msg));
	end
	return;
end

function EquipSecondryslots(msg, quiet)
	if(not msg or msg == "") then
		if (not quiet) then
			DEFAULT_CHAT_FRAME:AddMessage(IMCMD_EQUIP_COMM_INFO);
		end
		return;
	end
	if(CursorHasItem()) then
		if (not quiet) then
			DEFAULT_CHAT_FRAME:AddMessage(IMCMD_ERROR_HOLDING);
		end
		return;
	end
	for i = 0, 4, 1 do
		local numSlot = GetContainerNumSlots(i);
		for y = 1, numSlot, 1 do
			if (ImprovedMacro_GetItemName(i, y) == msg) then
				PickupContainerItem(i,y);
				if ( ImprovedMacro_SetItemSlot ~= nil ) then
					PickupInventoryItem(ImprovedMacro_SetItemSlot);
					ImprovedMacro_SetItemSlot = nil;
				end	
				if(CursorHasItem()) then
					PickupContainerItem(i,y);
				end
				return true;
			end
		end
	end
	
	if (not quiet) then
		DEFAULT_CHAT_FRAME:AddMessage(format(IMCMD_ERROR_UNKITEM, msg));
	end
	return;
end

function Unequip(msg)
	if(not msg or msg == "") then
		DEFAULT_CHAT_FRAME:AddMessage(IMCMD_UNEQUIP_COMM_INFO);
		return false;
	end
	if(CursorHasItem()) then
		DEFAULT_CHAT_FRAME:AddMessage(IMCMD_ERROR_HOLDING);
		return false;
	end
	for i = 1, 19, 1 do
		if (ImprovedMacro_GetItemName(-1, i) == msg) then
			PickupInventoryItem(i);
			BagItem();
			return;
		end
	end
	if(not quiet) then
		DEFAULT_CHAT_FRAME:AddMessage(format(IMCMD_ERROR_UNKITEM, msg));
	end
end

function BagItem(slot)
	for i = 0, 4, 1 do
		local numSlot = GetContainerNumSlots(i);
		for y = 1, numSlot, 1 do
			local texture, itemCount, locked = GetContainerItemInfo(i, y);
			if (not texture) then
				PickupContainerItem(i,y);
				return true;
			end
		end
	end
	DEFAULT_CHAT_FRAME:AddMessage(IMCMD_ERROR_SPACE);
	if ( slot ) then
		EquipCursorItem(slot);
	else
		AutoEquipCursorItem();
	end
	return false;
end

freeslots = nil;
function Get_Item2Bag(pname, set)
	local num_item2bag = 0;

	for i = 1, 19, 1 do
		if ((not EquipmentSets[pname][set][i] or EquipmentSets[pname][set][i] == "") and ImprovedMacro_GetItemName(-1, i)) then
			num_item2bag = num_item2bag + 1;
		end
	end

	return num_item2bag;
end

function PutItemIntoBag(bag)
	if ( bag == 0 ) then
		PutItemInBackpack();
	else
		PutItemInBag(19 + bag);
	end
end

function GetFreeSlotInBag(bag)
	local num = GetContainerNumSlots(bag);
	local freeslot = 0;
	for i = 1, num, 1 do
		if not GetContainerItemInfo(bag, i) then
			freeslot = freeslot + 1;
		end
	end

	return freeslot;
end

function BagItem2()
	if(freeslots == nil) then
		freeslots = {};
			for i = 0, 4 do
				freeslots[i] = GetFreeSlotInBag(i);
			end
	end

	for i = 0, 4 do
		if(freeslots[i] > 0) then
			PutItemIntoBag(i);
			freeslots[i] = freeslots[i] - 1;
			return true;
		end
	end

	DEFAULT_CHAT_FRAME:AddMessage(IMCMD_ERROR_SPACE);
	return false;
end

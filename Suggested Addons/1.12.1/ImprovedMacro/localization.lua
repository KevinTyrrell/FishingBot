-- Version : 
--		* English - vjeux
--		* French - Elzix
--		* German - DoctorVanGogh
--		* Korean - Arbi

IMCMD_EQUIP_COMM		= {"/착용","/equip","/착"};
IMCMD_EQUIP_COMM_INFO	= "/착용 또는 /착 <아이템 이름>, 해당 아이템을 착용합니다.";
IMCMD_EQUIP_OH_COMM		= {"/방패착용", "/보조착용", "/equipoffhand"};
IMCMD_EQUIP_OH_COMM_INFO= "/방패착용 <아이템 이름>, 보조장비나 방패를 착용합니다.";
IMCMD_UNEQUIP_COMM		= {"/벗기","/unequip","/벗"};
IMCMD_UNEQUIP_COMM_INFO	= "/벗기 또는 /벗 <아이템 이름>, 해당 아이템을 벗습니다.";
IMCMD_SSET_COMM			= {"/저장","/saveset"};
IMCMD_SSET_COMM_INFO	= "/저장 <이름>, 지정한 이름으로 장비세팅을 저장합니다.";
IMCMD_LSET_COMM			= {"/로드","/loadset"};
IMCMD_LSET_COMM_INFO	= "/로드 <이름>, 장비세팅을 불러냅니다.";
IMCMD_LISTSETS_COMM			= {"/목록","/listsets"};
IMCMD_LISTSETS_COMM_INFO	= "저장된 모든 장비세팅을 출력합니다.";
IMCMD_DSET_COMM			= {"/삭제","/delset"};
IMCMD_DSET_COMM_INFO	= "/삭제 <이름>, 지정한 이름의 장비 세팅을 삭제합니다.";
IMCMD_USE_COMM			= {"/사용","/use"};
IMCMD_USE_COMM_INFO		= "/사용 또는 /use <아이템 이름>, 지정한 아이템을 사용합니다.";
IMCMD_USETYPE_COMM		= {"/종류별사용","/usetype"};
IMCMD_USETYPE_COMM_INFO	= "/종류별 사용 또는 /usetype <아이템 종류>, 지정한 아이템 종류와 유사한 아이템을 사용합니다. 지정가능한 종류 : 보석, 광물, 약초, 기계공학, 응급 치료, 가죽, 실, 연금술, 낚시, 음식, 음료, 주술사, 흑마법사, 퀘스트 아이템, 무기, 갑옷, 방패, 원거리 장비, 화살및 탄환, 옷감.";
IMCMD_RUNMACRO_COMM		= {"/매크로실행"};
IMCMD_RUNMACRO_COMM_INFO= "/매크로실행 <번호>, 해당 매크로를 실행합니다.";
IMCMD_LEAVE_COMM	= {"/파탈","/파티탈퇴","/leaveparty"};
IMCMD_LEAVE_COMM_INFO = "/파탈 또는 /파티탈퇴 명령으로 파티를 떠날 수 있습니다.";

IMCMD_ERROR_UNKITEM		= "아이템 '%s'|1이;가; 없습니다!";
IMCMD_ERROR_NOITEM		= "'%s'|1과;와; 유사한 아이템 종류가 없습니다!";
IMCMD_ERROR_SAVED		= "장비셋 '%s'|1이;가; 저장되었습니다.";
IMCMD_ERROR_LOADED		= "장비셋 '%s'|1이;가; 로드되었습니다.";
IMCMD_ERROR_DELETED		= "장비셋 '%s'|1이;가; 삭제되었습니다.";
IMCMD_ERROR_HOLDING		= "무언가를 이미 들고 있는것으로 보입니다.";
IMCMD_ERROR_SPACE		= "가방에 충분한 공간이 없습니다!";
IMCMD_ERROR_UNKSET		= "장비셋 '%s'|1이;가; 없습니다!";
IMCMD_ERROR_UNKLIST		= "장비셋이 존재하지 않습니다.";

IMCMD_ERROR_MACRO		= "%s번 매크로는 존재하지 않습니다!";
IMCMD_ERROR_MACRO1		= "비어있는 슬롯이 필요합니다!";

IMCMD_TLIST_HEADER		= "장비세팅 목록:";

BINDING_HEADER_RUNMACRO = "매크로 실행 단축키";
BINDING_NAME_RUNMACRO1	= "1번 매크로 실행";
BINDING_NAME_RUNMACRO2	= "2번 매크로 실행";
BINDING_NAME_RUNMACRO3	= "3번 매크로 실행";
BINDING_NAME_RUNMACRO4	= "4번 매크로 실행";
BINDING_NAME_RUNMACRO5	= "5번 매크로 실행";
BINDING_NAME_RUNMACRO6	= "6번 매크로 실행";
BINDING_NAME_RUNMACRO7	= "7번 매크로 실행";
BINDING_NAME_RUNMACRO8	= "8번 매크로 실행";
BINDING_NAME_RUNMACRO9	= "9번 매크로 실행";
BINDING_NAME_RUNMACRO10	= "10번 매크로 실행";
BINDING_NAME_RUNMACRO11	= "11번 매크로 실행";
BINDING_NAME_RUNMACRO12	= "12번 매크로 실행";
BINDING_NAME_RUNMACRO13	= "13번 매크로 실행";
BINDING_NAME_RUNMACRO14	= "14번 매크로 실행";
BINDING_NAME_RUNMACRO15	= "15번 매크로 실행";
BINDING_NAME_RUNMACRO16	= "16번 매크로 실행";
BINDING_NAME_RUNMACRO17	= "17번 매크로 실행";
BINDING_NAME_RUNMACRO18	= "18번 매크로 실행";

--[[if ( GetLocale() == "frFR" ) then
	-- Traduction par Elzix

	IMCMD_EQUIP_COMM		= {"/equip"};
	IMCMD_EQUIP_COMM_INFO	= "/equip <nom de l'objet>, ?uipe l'objet";
	IMCMD_UNEQUIP_COMM		= {"/desequip", "/unequip"};
	IMCMD_UNEQUIP_COMM_INFO	= "/desequip <nom de l'objet>, retire l'objet";
	IMCMD_SSET_COMM			= {"/sauveset", "/saveset"};
	IMCMD_SSET_COMM_INFO	= "/sauveset <nombre (1-4)>, Enregistre l'?uipement actuel dans le num?o selection?;
	IMCMD_LSET_COMM			= {"/chargeset", "/loadset"};
	IMCMD_LSET_COMM_INFO	= "/chargeset (1-4)>, Charge l'?uipement du num?o selectionn?;

elseif ( GetLocale() == "deDE" ) then
	-- Translation by ??? and pc

	IMCMD_EQUIP_COMM		= {"/equip"};
	IMCMD_EQUIP_COMM_INFO	= "/equip <Gegenstandsname>, zieht den genannten Gegenstand an";
	IMCMD_UNEQUIP_COMM		= {"/unequip"};
	IMCMD_UNEQUIP_COMM_INFO	= "/unequip <Gegenstandsname>, zieht den genannten Gegenstand aus";
	IMCMD_SSET_COMM			= {"/saveset"};
	IMCMD_SSET_COMM_INFO	= "/saveset <Nummer (1-4)>, speichert das momentane Ausr?tungsset";
	IMCMD_LSET_COMM			= {"/loadset"};
	IMCMD_LSET_COMM_INFO	= "/loadset <Nummer (1-4)>, l? das genannte Ausr?tungsset";

end]]

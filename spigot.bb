With LunaticFamily, you can marry other players, adopt them, and enter blood brotherhoods with them. Create your own family tree and display it as a list or in the Advancements screen as a family tree.


[ATTACH=full]828419[/ATTACH]
I recommend using BetterAdvancements to have a larger Advancements screen.


FEATURES
- 100% customizable messages and other values
- customize commands and subcommand
- marry other players
- adopt other players
- enter siblinghoods, to have a sibling without having parents
- display your family tree in advancement screen
- supports MySQL and SQLite
- supports Paper, Bungeecoord and Velocity (on proxy, only proxy server needs to be configured)
- Placeholders with PlaceholderAPI

COMMANDS
[SPOILER=" /family - base command for plugin"][/SPOILER][SPOILER=" /family - base command for plugin"][/spoiler][SPOILER=" /family - base command for plugin"][/spoiler][SPOILER=" /family - base command for plugin"][/spoiler][SPOILER=" /family - base command for plugin"][/spoiler][SPOILER=" /family - base command for plugin"][/spoiler][SPOILER=" /family - base command for plugin"][/spoiler][SPOILER=" /family - base command for plugin"][/spoiler][SPOILER=" /family - base command for plugin"][/spoiler][SPOILER=" /family - base command for plugin"]
/family background - change the background of your family tree
/family list - show a list of your family
/family tree - update your family tree
/family marry - access marry command via family command
/family adopt - access adopt command via family command
/family sibling - access sibling command via family command
/family gender - access gender command via family command
/family reload - admin command to reload config
/family delete - admin command to delete a player from database
/family help - show help for family command
/family create - create a player in the database
[/SPOILER]
[SPOILER=" /marry - marry other players"][/SPOILER][SPOILER=" /marry - marry other players"][/spoiler][SPOILER=" /marry - marry other players"][/spoiler][SPOILER=" /marry - marry other players"][/spoiler][SPOILER=" /marry - marry other players"][/spoiler][SPOILER=" /marry - marry other players"][/spoiler][SPOILER=" /marry - marry other players"]
/marry propose <player> - propose to a player
/marry accept - accept marriage proposal
/marry deny - deny marriage proposal
/marry divorce - divorce from your partner
/marry gift - gift the item in your main hand to your partner
/marry heart - change the color of the heart of your marriage in marriage list
/marry list - show a list of all married players
/marry kiss - kiss your partner
/marry priest <player> <player> - marry two other players
/marry set - admin command to set a marriage
/marry unset - admin command to unset a marriage
/marry help - show help for marry command
[/SPOILER]
[SPOILER=" /adopt - adopt other players"][/SPOILER][SPOILER=" /adopt - adopt other players"][/spoiler][SPOILER=" /adopt - adopt other players"][/spoiler][SPOILER=" /adopt - adopt other players"][/spoiler][SPOILER=" /adopt - adopt other players"][/spoiler][SPOILER=" /adopt - adopt other players"][/spoiler][SPOILER=" /adopt - adopt other players"][/spoiler][SPOILER=" /adopt - adopt other players"][/spoiler][SPOILER=" /adopt - adopt other players"]
/adopt propose <player> - propose to adopt a player
/adopt accept - accept adopt proposal
/adopt deny - deny adopt proposal
/adopt kickout <player> - cancel adoption as a parent
/adopt moveout - cancel adoption as a child
/adopt set - admin command to set adoption
/adopt unset - admin command to unset adoption
/adopt help - show help for adopt command
[/SPOILER]
[SPOILER=" /sibling - enter siblinghoods with other players"][/SPOILER][SPOILER=" /sibling - enter siblinghoods with other players"][/spoiler][SPOILER=" /sibling - enter siblinghoods with other players"][/spoiler][SPOILER=" /sibling - enter siblinghoods with other players"][/spoiler][SPOILER=" /sibling - enter siblinghoods with other players"][/spoiler][SPOILER=" /sibling - enter siblinghoods with other players"][/spoiler][SPOILER=" /sibling - enter siblinghoods with other players"][/spoiler][SPOILER=" /sibling - enter siblinghoods with other players"][/spoiler][SPOILER=" /sibling - enter siblinghoods with other players"][/spoiler][SPOILER=" /sibling - enter siblinghoods with other players"]
/sibling propose <player> - propose to enter sibling with a player
/sibling accept - accept siblinghood proposal
/sibling deny - deny siblinghood proposal
/sibling unsibling - cancel siblinghood
/sibling set - admin command to set a siblinghood
/sibling unset - admin command to unset a siblinghood
/sibling help - show help for sibling command
[/SPOILER]
[SPOILER=" /gender - specify your gender"][/SPOILER][SPOILER=" /gender - specify your gender"][/spoiler][SPOILER=" /gender - specify your gender"][/spoiler][SPOILER=" /gender - specify your gender"][/spoiler][SPOILER=" /gender - specify your gender"][/spoiler][SPOILER=" /gender - specify your gender"][/spoiler][SPOILER=" /gender - specify your gender"][/spoiler][SPOILER=" /gender - specify your gender"][/spoiler][SPOILER=" /gender - specify your gender"][/spoiler][SPOILER=" /gender - specify your gender"][/spoiler][SPOILER=" /gender - specify your gender"]
/gender set - set your gender
/gender info - show your gender
/gender info <player> - show players gender
/gender help - show help for gender command
[/SPOILER]
[SPOILER=" /priest - setup new relationships as a priest"]
/priest stats - show your stats as a priest
/priest sibling <player> <player> - setup the Siblinghood of two players as a priest
/priest adopt  <player> <player> - setup the Adoption of two players as a priest
/priest marry <player> <player> - setup the Marriage of two players as a priest
[/SPOILER]

PLACEHOLDERS
[SPOILER="Placeholder"]
You need to put a "lunaticfamily_" in front, for example "%lunaticfamily_player%".

%rel_lunaticfamily_relation% -> returns the relation to a player, if they are not related, it returns an empty string

%player% -> name of the player

%gender% - gender of a player
%gender_emoji% - gender emoji, can be specified in lang.yml

%marriage_emoji% -> returns a colored heart, depending if the player is married or not (color can be specified with /marry emoji <color>)
%marriage_emojiStatus% -> depending if the player is married, it returns a colored or not colored heart (color can be specified with /marry emoji <color>)
%marriage_partner% -> the name of the partner
%marriage_priest% -> the name of the priest, if there is one
%marriage_date% -> date of marriage

%adoptionAsParent_firstChild_emoji% -> returns a colored heart, depending if the player has a child
%adoptionAsParent_firstChild_emojiStatus% -> depending if the player has a child, it returns a colored or not colored emoji  (color can be specified by the child with /adopt emoji <color>)
%adoptionAsParent_firstChild_child% -> the name of the child
%adoptionAsParent_firstChild_priest% -> the name of the priest, if there is one
%adoptionAsParent_firstChild_date% -> date of adoption

%adoptionAsParent_secondChild_emoji% -> returns a colored heart, depending if the player has a second child
%adoptionAsParent_secondChild_emojiStatus% -> depending if the player has a second child, it returns a colored or not colored emoji  (color can be specified by the child with /adopt emoji <color>)
%adoptionAsParent_secondChild_child% -> the name of the child
%adoptionAsParent_secondChild_priest% -> the name of the priest, if there is one
%adoptionAsParent_secondChild_date% -> date of adoption

%adoptionAsChild_emoji% -> returns a colored heart, depending if the player is adopted or not
%adoptionAsChild_emojiStatus% -> depending if the player is adopted, it returns a colored or not colored emoji  (color can be specified with /adopt emoji <color>)
%adoptionAsChild_child% -> the name of the child
%adoptionAsChild_priest% -> the name of the priest, if there is one
%adoptionAsChild_date% -> date of adoption

Non player related placeholders:

%marriages_count% -> total amount of active marriages
%adoptions_count% -> total amount of adopted players
%siblinghoods_count% -> total amount of siblinghoods

%marriages_<index>_player1% -> name of first player
%marriages_<index>_player2% -> name of second player
%marriages_<index>_emoji% -> colored emoji of this marriage
%marriages_<index>_priest% -> priest of marriage, if there is one
%marriages_<index>_date% -> date of marriage

%siblinghoods_<index>_player1% -> name of first player
%siblinghoods_<index>_player2% -> name of second player
%siblinghoods_<index>_emoji% -> colored emoji of this siblinghood
%siblinghoods_<index>_priest% -> priest of siblinghood, if there is one
%siblinghoods_<index>_date% -> date of siblinghood

%adoptions_<index>_parent% -> name of parent
%adoptions_<index>_child% -> name of child
%adoptions_<index>_emoji% -> colored emoji of this adoption
%adoptions_<index>_priest% -> priest of adoption, if there is one
%adoptions_<index>_date% -> date of adoption
[/SPOILER]

PERMISSIONS
[SPOILER="Permission"]
  lunaticfamily.adopt - Use /adopt
  lunaticfamily.marry - Use /marry
  lunaticfamily.sibling - Use /marry
  lunaticfamily.gender - Use /gender
  lunaticfamily.family - Use /family
  lunaticfamily.family.background - Use /family background
  lunaticfamily.marry.priest - Use /marry priest
  lunaticfamily.marry.gift - Use /marry gift
  lunaticfamily.marry.heart: -  Use /marry heart
  lunaticfamily.marry.heart.color.* - Use /marry heart <color>
  lunaticfamily.marry.heart.hex - Use /marry heart <hex>
  lunaticfamily.family.list - Use /family list
  lunaticfamily.family.list.others - Use /family list <player>
  lunaticfamily.admin.adopt - Use adopt admin commands
  lunaticfamily.admin.marry - Use marry admin commands
  lunaticfamily.admin.sibling - Use sibling admin commands
  lunaticfamily.admin.gender - Use gender admin commands
  lunaticfamily.admin.reload - Use reload command
[/SPOILER]

DEPENDENCIES
(Required) LunaticLib

(Optional) CrazyAdvancementAPI - needed to display family tree in advancement screen (if you not using newest server version, you need to use an older version fitting your server version)

(Optional) Vault - needed to withdraw economy
(Optional) PlaceholderAPI - needed to use placeholder


If you discover any bugs or have ideas for new features, please don't hesitate to open an Issue on GitHub or contact me on Discord. I appreciate your input!

Discord: janschuri
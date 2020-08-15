# PermTrigger [Bukkit/Spigot](https://www.spigotmc.org/resources/PermTrigger.82779/).
A plug-in for Spigot that triggers commands based upon permssion assignments.

---
This plug-in allows you to define commands to be executed at the console when a specific permission is added or removed from a player.

It will work without a permission plugin but performs better with Groupmanager 2.6+ or LuckPerms 5.1+.

After first run it will create a demo perm_triggers.json in the plugin/PermTriggers/ folder.

You can set to load (and refresh) from a json **file** or to pull the file from a **webserver**.

---
Example perm_triggers.json

```
# This file contains permission triggers.
# 
# Triggers are sets of commands to be issue at the console
# when a permission is added to, or removed from a player.
# 
# Commands can have prefixes to perform specific tasks.
# '#broadcast' '#tell'
{
  "player.creative": {
    "forced": true,
    "added": [
      "gamemode creative {player}",
      "#broadcast {player} has become enlightened!"
    ],
    "removed": [
      "gamemode survival {player}",
      "#broadcast {player} has equipped leaded boots."
    ]
  },
  "player.speed": {
    "added": [
      "effect give {player} speed 1700 5 true",
	  "effect give {player} haste 1700 3 true",
      "#tell You have become fast!"
    ],
    "removed": [
      "effect clear {player} speed",
	  "effect clear {player} haste",
      "#tell Slow down speedy!"
    ]
  },
  "player.op": {
    "added": [
      "op {player}"
    ],
    "removed": [
      "deop {player}"
    ]
  }
}
```


---

**Bug reports**  
[https://github.com/ElgarL/PermTrigger/issues](https://github.com/ElgarL/PermTrigger/issues)

**Support**  
Support my Dev work via PayPal - [Donate](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=95VVUXYDUCDH8&source=url).  
I hang out on the Towny Discord if anyone needs help
[https://discord.gg/gnpVs5m](https://discord.gg/gnpVs5m)

# Bloodmoon Tides Minecraft Server Mod

Bloodmoon Tides is a server-side Fabric Minecraft mod that adds bloodmoon events, water damage, and light-ignoring hostile mob spawning during scheduled bloodmoon nights. It is a Minecraft server mod for Fabric Loader and Fabric API, targeting Minecraft 1.21.4 with Java 21.

## Install

1. Install Java 21.
2. Install Fabric Loader 0.16.10 for Minecraft 1.21.4 on the server.
3. Install Fabric API 0.119.3+1.21.4 in the server's `mods` folder.
4. Build the mod with:

```bash
./gradlew build
```

5. Copy the generated `build/libs/bloodmoon-events-1.0.0.jar` file into the server's `mods` folder.
6. Start or restart the server.

The mod runs on the server. No client-side mod installation is required for the implemented features.

## Use

1. Start a Minecraft 1.21.4 Fabric server with the mod installed.
2. Play in the Overworld.
3. A bloodmoon is active during the night of every eighth in-game day, from time `13000` through `22999`.
4. During a bloodmoon:
   - Players touching water or bubble columns receive drowning damage every 10 ticks.
   - Zombies attempt to spawn around each non-spectator Overworld player every second.
   - Bloodmoon zombie spawning does not check block light, so zombies can spawn in bright areas.
   - Each player area is limited to 32 nearby monsters before another zombie spawn is attempted.
5. No configuration file or commands are required.

The project targets Minecraft 1.21.4. The `26.3` version reference does not correspond to the Minecraft and Fabric versions defined by this project.

---

More Minecraft mod projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/874ef1f1-e404-4bcd-8c6e-7200fb10580b?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=874ef1f1-e404-4bcd-8c6e-7200fb10580b

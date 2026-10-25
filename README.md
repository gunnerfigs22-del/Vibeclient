# Vibe Client (Fabric, Minecraft 26.3)

Requirements: JDK 25+, Gradle 9.6.0 (Loom 1.17), Fabric Loader 0.19.5+, Fabric API 0.160.5+26.3.

Setup:
    gradle wrapper --gradle-version 9.6.0
    ./gradlew build        # jar in build/libs/
    ./gradlew runClient    # test in a dev instance

Menu: press Right Shift (rebindable). Drag windows by their header, left click a module to toggle.
Modules: Freecam, StashFinder, PlayerActivity (optional quick keys can be set in Controls).

Install straight into your mods folder:
    ./gradlew installMod -PmodsDir="<path to your instance's mods folder>"

Add a module: extend Module, register it in ModuleManager, add a lang entry.

Notes on 26.x: the game is unobfuscated, so there are no mappings. Names are Mojang's
(Minecraft, KeyMapping, GuiGraphicsExtractor). Dependencies use implementation, not modImplementation.
Use on singleplayer or servers that allow modified clients.

## Build without installing anything
Push this folder to a GitHub repo. The Actions tab builds it automatically;
download the "vibeclient-jar" artifact, unzip it, and put the jar (not -sources) in your mods folder.

# Litematica Printer

![GitHub issues](https://img.shields.io/github/issues-raw/aleksilassila/litematica-printer)
![GitHub pull requests](https://img.shields.io/github/issues-pr-raw/aleksilassila/litematica-printer)
![GitHub all releases](https://img.shields.io/github/downloads/aleksilassila/litematica-printer/total)
![GitHub Repo stars](https://img.shields.io/github/stars/aleksilassila/litematica-printer)

This extension adds printing functionality for [Litematica fabric](https://github.com/maruohon/litematica) 1.20.4 1.21 
and 1.21.4 versions. Printer allows players to build big structures more quickly by automatically placing the correct blocks around you.

![Demo](printer_demo.gif)

## Installation

1. Download and install [Fabric](https://fabricmc.net/use/installer/) if you haven't already.
2. Download the latest Litematica Printer release for your Minecraft version from the
   [releases page](https://github.com/aleksilassila/litematica-printing/releases/latest) (The files can be found under
   "assets").
3. Download [Litematica + MaLiLib](https://www.curseforge.com/minecraft/mc-mods/litematica)
   and [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api/) (≠ Fabric).
4. Place the downloaded .jar files in your `mods/` folder.

[If this is the first fabric mod you are installing, here's an informative video on how to install Fabric mods.](https://www.youtube.com/watch?v=x7gmfib4gHg)

## How To Use

Using the printer is straightforward: You can toggle the feature by pressing `CAPS_LOCK` by default. To configure
variables such as
printing speed and range, open Litematica's settings by pressing `M + C` and navigate to "Generic" tab. Printer's
configuration can be
found at the bottom of the page. You can also rebind the printing toggle under "Hotkeys" tab. Holding down `V` by
default will also
print regardless if the printer is toggled on or off.

## 2b2t Grim placements
This printer is Grim compatible if the `printerGrimRotate` option is enabled in the general settings. This option is enabled by default.
There is no need anymore to enable the `printerRotatePlayer` option.

### Other features added by this fork:
- Airplace (printerAirPlace). A bit buggy but turning up printerTickDelay makes it more reliable.
- Working inventory management. The default litematica printer has issues on 2b with inventory management. This fork fixes that.
- autoConvertSchematicToLitematicOnLoad. This setting auto converts schematic files to litematic files when loading them. 
    This is useful when using schematics produced by Rebanes Mapartcraft Website [text](https://rebane2001.com/mapartcraft/).
    Without a litematica file baritone won't work with the `#litematica` command

## Issues

This is a 2b2t specific fork of printer and might not work on other anarchy servers besides 2b2t. If you have issues 
with the printer, **do not** bother the original creator ofLitematica (maruohon) with them.
If you have issues with printer on 2b2t you can message me on discord `@icetank` and we can try and figure out your config issue.

### List of know issues

Currently, the following features are still broken or missing:

- Placing liquids (printing **in** liquids works though)
- Current algorithm for placing rails isn't perfect,
  sometimes it can't place all the rails (to avoid placing anything incorrectly).

Also, I have decided that features that fix existing builds,
such as automatic excavation or correcting incorrectly placed blocks are out of the scope of this mod.

## Building and Contributing

I recommend Intellij for developing. Just clone the repo and open the project. Make sure to use at least java jdk 17 for 1.20.4
and java jdk 21 for newer versions or gradlew will complain. The jar file can be build using the build task under
`v{version} > Tasks > build > build`.

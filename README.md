# Litematica Printer

![GitHub issues](https://img.shields.io/github/issues-raw/aleksilassila/litematica-printer)
![GitHub pull requests](https://img.shields.io/github/issues-pr-raw/aleksilassila/litematica-printer)
![GitHub all releases](https://img.shields.io/github/downloads/aleksilassila/litematica-printer/total)
![GitHub Repo stars](https://img.shields.io/github/stars/aleksilassila/litematica-printer)

This extension adds printing functionality for [Litematica fabric](https://github.com/maruohon/litematica) 1.21, 1.20.4, 1.20.0 ,1.19, 1.18 and 1.17 versions. Printer allows players to build
big structures more quickly by automatically placing the correct blocks around you.

![Demo](printer_demo.gif)

## Warning Minecraft Version 1.21
Since there is no official release of Litematica for 1.21, the printer uses a update fork of it and MaliLib. <br>
This fork is not official and may contain bugs or issues. If you encounter any issues with the printer, please report them to the fork's repository.<br>
This fork is not guaranteed secure as it isn't an official release.<br>
1.21 Litematica repository: [Litematica](https://github.com/sakura-ryoko/litematica)<br>
1.21 MaliLib repository: [MaliLib](https://github.com/sakura-ryoko/malilib)<br>
USE AT YOUR OWN RISK

## Download

Check the GitHub release page for the latest version of the printer for your Minecraft version:
[Releases](https://github.com/IceTank/litematica-printer/releases)

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

## 2b2t Grim bypass
This printer is Grim compatible if the `printerRotatePlayer` option is enabled in the printer settings. This option is enabled by default.

I recommend to use the FreeLook option when printer as you will be able to look around while the player rotates to place blocks.
To do this go to Litematica Settings > Generic > `printerFreeLookToggle` and set it to a keybind. Before printing press it to activate FreeLook. 
There is also `printerFreeLookThirdPerson` which will auto switch to third person view when FreeLook is active.
Both `togglePrintingMode` and `printerFreeLookToggle` can be set to the same keybind to activate them at the same time.
The FreeLook is programmed to attempt to move you in the direction you are looking at even if the player is rotated. 

## Issues

If you have issues with the printer, **do not** bother the original creator of
Litematica (maruohon) or the original creator of litematica-printer (aleksilassila)
with them. Contact me instead. Feature requests or bugs for this Fork can
be reported via [GitHub issues](https://github.com/icetank/litematica-printer/issues),
or via Discord (@IceTank).

Before creating an issue, make sure you are using the latest version of the mod.
To make fixing bugs easier, include the following information in your issue:

- Minecraft version
- Litematica version
- Printer version
- Detailed description of how to reproduce the issue
- If you can, any additional information, such as error logs, screenshots or **the incorrectly printed schematics**.

### List of know issues

Currently, the following features are still broken or missing:

- Placing liquids (printing **in** liquids works though)
- Current algorithm for placing rails isn't perfect,
  sometimes it can't place all the rails (to avoid placing anything incorrectly).

Also, I have decided that features that fix existing builds,
such as automatic excavation or correcting incorrectly placed blocks are out of the scope of this mod.

## Building and Contributing

Each Minecraft version has its own submodule, that has the default fabric mod development tasks
and contains the version-specific code. To reduce the amount of work I have to do to make
it work for multiple Minecraft versions, I created this hacky gradle script that copies the
common code over to the other version implementations. Currently, the script copies everything,
except `implementation/` folder, which should therefore be the only places containing any
version specific code.

If you want to make changes to the mod, I would recommend you to first implement them for
the latest Minecraft version (1.19), and then running the `syncImplementations` gradle task,
found **in the same subproject** as your changes, to copy the common code of that submodule
to the other implementations. After that you will only have to write / copy manually
the version-specific code (found in the `implementation` folder) to the other versions and do some testing to ensure
everything works.

Contributions are welcome and appreciated! I have recently rewritten the whole project,
so that it would be much easier to work with.

Also, if you know a better way to develop for multiple
Minecraft versions that doesn't involve multiple git branches or hacky gradle scripts
(perhaps a way to share common code between the implementations?), please let me know.

Useful gradle tasks:

- `[v1_19/v1_18/v1_17]:syncImplementations`
    - Copy over common code to other implementations
- `buildAll`
    - Build all implementations and copy their jars to `build/` directory for easy distribution.
- `[v1_19/v1_18/v1_17]:runClient`
    - Start the target Minecraft version

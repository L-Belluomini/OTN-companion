<p align="center">
  <img src= https://github.com/L-Belluomini/OTN/blob/main/app/src/main/res/drawable/otn_logo_shield.png width="200" height="200">

# OTN-Companion

## Introduction

*Open Tak Navigation* is an open source project for the TAK environment.

The project started back when the VNS (Vehicle Navigation Sytem) was not yet publicly available, with the same prime objective to deliver an offline routing instrument for the ATAK app.
OTN manages this making use of [Open Street Map](https://www.openstreetmap.org) data and the [GraphHopper](https://www.graphhopper.com/) routing engine, but the project aims to integrate many other improvements and features.

OTN-Companion is the complementary tool to create graphs for the [OTN](https://github.com/L-Belluomini/OTN) ATAK plugin.

It is built based on Java, and easily runs on both Windows and Linux devices.

For technical informations on Graphhopper visit [this link](https://github.com/graphhopper/graphhopper/tree/master/docs).

**A new release is on the way. The project is soon to be out of beta.**

## For users

1. Download the latest OTN-C release from the GitHub page, no installation required.
2. Download the OpenStreetMap .pbf file of the area you are interested in from [Geofabrik](https://download.geofabrik.de/index.html).
3. Launch the OTN-C program. You will be presented with the Area pane. 
4. Upload the .pbf file through the “load OSM file” button. <br>
This will generate an area element which will be loaded in the workflow table. <br>
All changes to the area element will generate a new area element in the workflow table.
5. Once a .pbf file is uploaded,  move on to the Profiles and Graph panes. <br>
There are two default profiles already set in the profiles table but you can add new ones, delete them or change their properties from this pane. <br>
**N.B.: At least one profile must be provided to generate a graph.**
6. On to the Graph pane, set the directory in which your graph will be generated with the “select storage Dir” button.
7. Generate your graph with the “generate” button at the bottom. <br>
An info dialog will appear once your graph has been successfully created.
8. Congrats, you now have a graph to use in OTN! For OTN usage visit the [OTN GitHub page](https://github.com/L-Belluomini/OTN).<br>

## Current features

* New and shiny GUI.
* Area trimming.
* Support for multiple profiles in a single graph.
* More vehicle types available.
* Support for different routing options for each profile.
* Possibility to include the polyline of the graph border.
* Manual available [here](https://github.com/MightyBakedPotato/OTN-manual/releases/tag/v.1.8).

## Future Features

* Area editing and trimming with set operations.
* Area trimming using admin boundaries (with pre-loaded nations' borders).
* DTED support.
* Support for custom profiles.
* Integration with TAKX (dependent on TPC release schedule).
* Server/microservice version.

## For developers

Simple Java project menaged by Maven.

## Libraries

* Gson
* Graphhopper
* Picocli

## Contact

For any issue, suggestion or question, feel free to leave an issue on the [GitHub issues page](https://github.com/L-Belluomini/OTN-companion/issues).

## License

check the [license.md](https://github.com/L-Belluomini/OTN-companion/blob/main/LICENSE)

---

plugin developed and tested on a huawei p10 light (android 8.0 API 26) 4gb ram kirin658 cpu atak version 4.7.0.3 developer build (28/07/2022).<br>

OTN-C runs on Java 11.<br>

Windows users may encounter problems with ram allocation.

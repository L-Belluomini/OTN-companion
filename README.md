
# OTNcompanion
## Introduction
This is the complementary tool for generating graphs for the OTN Atak plugin using Graphhopper.
It is built based on Java, and easily runs on Windows and Linux devices.
For technical informations on Graphhopper visit [this link](https://github.com/graphhopper/graphhopper/tree/master/docs).
To download the OSM data you are interested in, go to [geofabrik](https://download.geofabrik.de/) select you region and download the .pbf file
## For users
1. On the GitHub project page, go to [releases](https://github.com/L-Belluomini/OTN-companion/releases) and download the .jar file
2. Open the comand prompt
3. In the cmd, launch Java to check if the java7 (or later) version is installed
4. Move to the .jar directory
5. The comand java -cp OTN.jar com.OTN.cliInterface prints the general help informations
6. The comand java -cp OTN.jar com.OTN.cliInterface generate prints the generate comand help informations
7. Connect your android smartphone with Atak installed
8. Enable data transfer on your phone
9. Go to internal/atak/tools and create the /OTN/cache directory
10. Copy the content of storageDir in /cache
11. The OTNcompanion setup is done, for OTN usage visit the [OTN GitHub page](https://github.com/L-Belluomini/OTN).
## For developers
Simple Java project menaged by Maven
## Libraries
* Gson
* Graphhopper
* Picocli
## Future features
TBD
## Contact
For any issue, suggestion or question, feel free to leave an issue on the [GitHub issues page](https://github.com/L-Belluomini/OTN-companion/issues).
## License
check the [license.md](https://github.com/L-Belluomini/OTN-companion/blob/main/LICENSE)

---
plugin developed and tested on a huawei p10 light (android 8.0 API 26)
4gb ram
kirin658 cpu
atak version 4.4.0.0 developer build (latest on github @ 2022-02-05)

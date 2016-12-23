HdM-CSM-SmartHome Project: Helmut
=================================

**OpenHAB2-binding to connect an Android Wear watch**

Instructions:
-------------

1.  install openhab2 as described here: http://docs.openhab.org/developers/development/ide.html
2.  check out this repository and import it into the eclipse workspace
3.  enter the IP-address to the raspberry pi connected to the doorbell in src/main/resources/services/piface.cfg
4.  boot the raspberry pi
5.  use openHAB_Smarthome_Runtime.launch to launch openHAB


Item channels:
--------------

*   watchface_indicator_1: Light indicator
*   watchface_indicator_2: windows indicator
*   watchface_indicator_3: pills indicator
*   watchface_indicator_4: door indicator
*   watchface_indicator_5: oven indicator
*   watchface_indicator_6: unused
*   vibration: bell notification
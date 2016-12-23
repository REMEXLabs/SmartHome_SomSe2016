HdM-CSM-SmartHome Project: Helmut
=================================

**Android Wear App with Phone companion app to connect an Android Wear Smartwatch to OpenHAB**


Instructions:
-------------

1.  set up and run OpenHAB2 with the Smarthome/Helmut-Binding
2.  check out this repo and import the project in Android Studio
3.  make sure you have recent versions of the android SDK and build tools
4.  build the module "phone" and run it on the phone connected to the watch
5.  build the module "watch" and run it on the connected smartwatch
6.  open "SH Setup" and press "Setup"
7.  if the auto-setup fails (the app can't find your OpenHAB-installation via mDNS),
    manually enter the OpenHAB-address:
    *   open a Chrome browser at chrome://inspect
    *   click "inspect" at the Smarthome-entry
    *   navigate to Local Storage > openhab in the "Resources"-tab
    *   enter the address (i.e. http://192.168.0.100:8080) as value for key "openhab_address"
    *   restart SH Setup (kill the app, then reopen SH Setup) and press "Setup" again
8.  select the "Helmut" watchface on the watch
9.  test stuff using the test activities and the openHAB-web-interface


Features:
----------

* single tap on watchface: speech recognition (example: "Licht im Partyraum blau", "Alle Lichter aus")
* triple tap on watchface: emergency call (contact can be defined via phone-app)
* watchface_indicator_1: Light indicator
* watchface_indicator_2: windows indicator
* watchface_indicator_3: pills indicator
* watchface_indicator_4: door indicator
* watchface_indicator_5: oven indicator
* watchface_indicator_6: unused
* vibration: bell notification
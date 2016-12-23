# SmartCarePi

Das Python-Script zur Funktionalität der Buttons der SmartCareBox. 

Das Script wurde für Python 3.x geschrieben, Python 2.x wird nicht unterstützt.
Vor dem Start müssen die Python-Bibliotheken installiert werden:

`pip install requests`

`pip install pifacedigitalio`

`pip install socketIO-client`

(Angenommen der Python-Pakagemanager pip ist installiert).


Soald der Raspberry-Pi mit dem PiFace zusammen mit den Buttons verbunden ist, kann das Script mit

`python3 smarthome.py`

gestartet werden.


__Sobald ein Button gedrückt wird, wird ein Post-Request an den Server gesendet__

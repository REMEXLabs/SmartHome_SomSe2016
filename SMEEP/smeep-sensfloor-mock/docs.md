Der einfachste Zugang ist wohl über die socket.io - Schnittstelle. (http://socket.io/) Alternativ kann man die Rohdaten der Sensoren einfach über TCP auf Port 4000 empfangen.

In Javascript kann man (wenn die socket.io Bibliothek entsprechend verlinkt / bei node.js "required" ist) nun folgendermaßen zum SensFloor-Empfänger verbinden:

var socket = io.connect(url:8000); 

z.B. wenn Ihr den eingebauten Wlan-AP benutzt: "http://192.168.5.5:8000". 
Falls Ihr den Empfänger übers LAN verwendet: Der Hostname ist aufgedruckt, evtl wird dieser aber nicht verteilt, dann muß die entsprechende IP im Router / DHCP-Server nachgeschlagen werden. Wichtig ist dabei immer, zu Port 8000 zu verbinden. Es ist keine Authentifizierung notwendig, d.h. jeder, der in das Netzwerk verbinden darf in dem der Empfänger hängt, kann die Daten empfangen.

Der Empfänger stellt verschiedene socket.io-Events zur Verfügung, die folgendermaßen verwendet werden können:
socket.on('raw', function(data){...});
"data" ist immer ein Javascript-Objekt (JSON) mit verschiedenen Feldern, abhängig vom Event.
Besonders interessant könnten für euch folgende Events sein:Event 'cluster'     - wird mit konstanter Frequenz (50 mal pro Sekunde) versendet
data.points : Array an Vektoren [{x: ..., y: ..., c: ...}]. 

Jeder Vektor beschreibt ein einzelnes Sensorfeld. x und y sind die Position (Schwerpunkt des Sensordreiecks), c ist die aktuelle Kapazität. 
Faktisch eine Art "Punktwolke", damit super geeignet für weitere Verarbeitung der Daten.
Das Array enthält allerdings auch solche Punkte, deren Wert sich im Vergleich zum vorherigen Zustand nicht geändert hat, es wird also immer der vollständige Kapazitätszustand der Installation übermittelt.

Zum Koordinatensystem: Der Ursprung liegt genau in der Mitte der Installation, die Einheit ist "Meter".

Die x-Achse verläuft entlang der längeren Dimension, und so wie es verbaut ist "nach hinten" in positiver Richtung. Zur Orientierung: Der Punkt an dem das Stromkabel befestigt ist, liegt also bei {x: 2, y: -1,5}.

data.cog : Vektor im Format {x: ..., y: ...}, der den aktuellen Schwerpunkt der Kapazitätswerte der gesamten Installation beinhaltetEvent 'raw'  - pro eintreffender Sensornachricht am Transceiver wird eins dieser Events emittiert.

data.raw : Byte Array mit einer Sensornachricht im Rohdatenformat (17 Bytes, erstes Byte 0xFD, 2 Bytes Installations-ID, 2 Bytes Modulkoordinaten, letzte 8 Bytes: Kapazitätswerte der 8 Sensorfelder eines Moduls)Dann gibt es noch die (für euch wahrscheinlich vorerst weniger relevanten) Events 'state' (Zuordnung von Kapazitätswerten zur Modul-ID), und 'fall' (Erkannte Stürze auf der Installation).
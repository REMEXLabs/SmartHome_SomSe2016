Server
- PHP 5.5
  - mod_rewrite aktiviert
- MySQL 
- NodeJS

Hinweise
- alle Ordner unter /var/www/html/ ablegen. Beispiel
  - push: http://smartcare.mi.hdm-stuttgart.de/push/public/
  - api: http://smartcare.mi.hdm-stuttgart.de/api/public/

### Datenbank
- Die SQL-Datei smartcare.sql importieren

### api
-  php composer.phar ausführen (abhängigkeiten werden geladen)
-  unter config > autoload > global.php & local.php Datenbankdaten ergänzen 

#### push
- In ordner node init ausführen
- server.js Zeile 82 url anpassen
- public/server.js Zeile 3 url anpassen (index.html in dem ordner dient zum testen)
- server.js mit node starten (am besten mit forever das der server auch im Hintergrund läuft)


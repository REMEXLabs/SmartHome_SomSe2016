var express = require('express'),
    app = express(),
    server = require('http').createServer(app),
    io = require('socket.io').listen(server),
    conf = require('./config.json');
    fs                  = require('fs'),
    request = require('request'),
    mysql               = require('mysql'),
    connectionsArray    = [],
    connection          = mysql.createConnection({
      host        : 'localhost',
      user        : 'test',
      password    : 'test',
      database    : 'smartcare',
      port        : 3306
    }),
    POLLING_INTERVAL = 3000,
    pollingTimer = null;


// Webserver
// auf den Port x schalten
server.listen(conf.port, "0.0.0.0");

app.configure(function(){
  // statische Dateien ausliefern
  app.use(express.static(__dirname + '/public'));
});

// wenn der Pfad / aufgerufen wird
app.get('/', function (req, res) {
  // so wird die Datei index.html ausgegeben
  res.sendfile(__dirname + '/public/index.html');
});

var pollingLoop = function () {

  // Make the database query
  var query = connection.query('SELECT * FROM device'),
      users = []; // this array will contain the result of our db query

  // set up the query listeners
  query
      .on('error', function(err) {
        // Handle error, and 'end' event will be emitted after this as well
        console.log( err );
        updateSockets( err );

      })
      .on('result', function( user ) {
        // it fills our array looping on each user row inside the db
        console.log(user);
        users.push( user );
      })
      .on('end',function(){
        // loop on itself only if there are sockets still connected
        if(connectionsArray.length) {
          pollingTimer = setTimeout( pollingLoop, POLLING_INTERVAL );

          updateSockets({users:users});
        }
      });

};


// Websocket
io.sockets.on('connection', function (socket) {
  // der Client ist verbunden

  if (!connectionsArray.length) {
    //pollingLoop();
  }

  //socket.emit('notification', { zeit: new Date(), text: 'Connected with Server!' });
  // wenn ein Benutzer einen Text senden
  socket.on('notification', function (data) {
    // so wird dieser Text an alle anderen Benutzer gesendet

    var patientId = 1;

    request.post({url:'http://smartcare.mi.hdm-stuttgart.de/api/public/notification.json', form: {
      zeit: new Date(),
      text: data.text,
      patientId: patientId
    }}, function(err,httpResponse,body){
      console.log('err: ' + err);
      console.log('httpResponse: ' + httpResponse);
      console.log('body: ' + body);
    });

    io.sockets.emit('notification', {
      zeit: new Date(),
      text: data.text,
      patientId: patientId
    });
  });

  //console.log( 'A new socket is connected!' );
  //connectionsArray.push( socket );
});

// Portnummer in die Konsole schreiben
console.log('Server is running!');

var updateSockets = function ( data ) {
  // store the time of the latest update
  data.time = new Date();
  // send new data to all the sockets connected
  connectionsArray.forEach(function( tmpSocket ){
    //tmpSocket.volatile.emit( 'notification' , data );
  });
};

$(document).ready(function(){
  // WebSocket
  var socket = io.connect('http://smartcare.mi.hdm-stuttgart.de:8080/');
  // neue Nachricht
  socket.on('notification', function (data) {
    var zeit = new Date(data.zeit);
    $('#content').append(
        $('<div></div>').append(
            // Uhrzeit
            $('<span>').text('[' +
                zeit.getDate() + '.' + zeit.getMonth() + '.' + zeit.getFullYear() + ' '
                +
                (zeit.getHours() < 10 ? '0' + zeit.getHours() : zeit.getHours())
                + ':' +
                (zeit.getMinutes() < 10 ? '0' + zeit.getMinutes() : zeit.getMinutes())
                + '] '
            ),
            // Text
            $('<span>').text(data.text))
    );
    // nach unten scrollen
    $('body').scrollTop($('body')[0].scrollHeight);
  });
  // Nachricht senden
  function senden(){
    // Eingabefelder auslesen
    var text = $('#text').val();
    // Socket senden

    if(text != ''){
      socket.emit('notification', {
        text: text
      });
      // Text-Eingabe leeren
      $('#text').val('');
    }
  }
  // bei einem Klick
  $('#senden').click(senden);
  // oder mit der Enter-Taste
  $('#text').keypress(function (e) {
    if (e.which == 13) {
      senden();
    }
  });
});
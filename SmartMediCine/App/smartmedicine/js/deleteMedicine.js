$(function(){ 
/**
* The Ready Function will be loaded when the table is loaded
*
*/
   $('#bootstrap-table').ready(function() {
   	  loadSzenarios();
   	  checkAuth();
   });
    		
/**
* This is the Click-Function which is handling the Button-Click Event for adding new Szenarios
*
*/
   function loadSzenarios() {
   	
   }
   
   
   // Your Client ID can be retrieved from your project in the Google
   // Developer Console, https://console.developers.google.com
   var CLIENT_ID = '964242476289-2tua7gt8lf9r9sr6deavpcb1aovgsgkq.apps.googleusercontent.com';

   var SCOPES = ["https://www.googleapis.com/auth/calendar.readonly"];
   var arrCalendarResults = [];
   var objCalendarResults;
   var arrSingelEvents = [];

   /**
    * Check if current user has authorized this application.
    */
   function checkAuth() {
     gapi.auth.authorize(
       {
         'client_id': CLIENT_ID,
         'scope': SCOPES.join(' '),
         'immediate': true
       }, handleAuthResult);
   }

   /**
    * Handle response from authorization server.
    *
    * @param {Object} authResult Authorization result.
    */
   function handleAuthResult(authResult) {
     var authorizeDiv = document.getElementById('authorize-div');
     if (authResult && !authResult.error) {
       // Hide auth UI, then load client library.
       authorizeDiv.style.display = 'none';
       loadCalendarApi();
     } else {
       // Show auth UI, allowing the user to initiate authorization by
       // clicking authorize button.
       authorizeDiv.style.display = 'inline';
     }
   }

   /**
    * Initiate auth flow in response to user clicking authorize button.
    *
    * @param {Event} event Button click event.
    */
   function handleAuthClick(event) {
     gapi.auth.authorize(
       {client_id: CLIENT_ID, scope: SCOPES, immediate: false},
       handleAuthResult);
     return false;
   }

   /**
    * Load Google Calendar client library. List upcoming events
    * once client library is loaded.
    */
   function loadCalendarApi() {
     gapi.client.load('calendar', 'v3', listUpcomingEvents);
   }

   /**
    * Print the summary and start datetime/date of the next ten events in
    * the authorized user's calendar. If no events are found an
    * appropriate message is printed.
    */
   function listUpcomingEvents() {
     var request = gapi.client.calendar.events.list({
       'calendarId': 'primary',
       'timeMin': (new Date()).toISOString(),
       'showDeleted': false,
       'singleEvents': true,
       'maxResults': 10,
       'orderBy': 'startTime'
     });

     request.execute(function(resp) {
       var events = resp.items;
       if (events.length > 0) {
         for (i = 0; i < events.length; i++) {
           var event = events[i];
           var when = event.start.dateTime;
           if (!when) {
             when = event.start.date;
           }
           objCalendarResults = new Object();
       	  var krankheit = event.summary.substr(0, event.summary.indexOf("//"));
           var medikament = event.summary.substr(event.summary.indexOf("//")+2, event.summary.length);
           var einnahme = when;
           
	          objCalendarResults.krankheit = krankheit;
	          objCalendarResults.medikament = medikament;
	          objCalendarResults.einnahmen = einnahme;
           arrCalendarResults.push(objCalendarResults);
           document.getElementById("tbodyMedicineList").innerHTML += "<tr><td><img src='img/blau.png' width='10' height='10'/></td><td>"+krankheit+"</td><td>"+medikament+"</td><td>"+einnahme+"</td><td><button type='button' class='btn btn-danger'><span class='glyphicon glyphicon-trash' aria-hidden='true'></span></button></td></tr>"; 
        }
         
         removeDuplicate(arrCalendarResults);

       }
		
     });
   }
   
   function removeDuplicate(arr){
	    var tmp = [];
	    for(var i = 0; i < arr.length; i++){
	    	
	        tmp.push(arr[i]);
	    }
	    return tmp;
	}
});
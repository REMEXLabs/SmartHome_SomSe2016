angular.module('starter.controllers', ['ionic', 'ui.router'])

.controller('PatientsCtrl', function($scope, Patients, $state, $interval) {
  // With the new view caching in Ionic, Controllers are only called
  // when they are recreated or on app start, instead of every page change.
  // To listen for when this page is active (for example, to refresh data),
  // listen for the $ionicView.enter event:
  //
  //$scope.$on('$ionicView.enter', function(e) {
  //});
  $scope.addPatient = function(){
   $state.go('tab.patient-add');
  } 

  function fetchPatients(){
    Patients.all().then(function(patients){
    $scope.patients = patients.data;

    for (var j=0; j < patients.data.length; j++){
      $scope.patients[j].datetime = moment($scope.patients[j].datetime).format("HH:mm");
    }

    for (var i=0; i < patients.data.length; i++){
      if ($scope.patients[i].isAsleep == 0){
        $scope.patients[i].isAsleep = false;
      }
      else{
        $scope.patients[i].isAsleep = "icon ion-ios-moon pull-left positive";
      }

    }
  })
  }   

  fetchPatients();
$interval(function() {
    fetchPatients();
  }, 3000);
  

  $scope.remove = function(patient) {
 
    Patients.remove(patient);
  };
})

.controller('PatientDetailCtrl', function($scope, $stateParams, $http, Patients, $interval) {

  function fetchonePatient(){
    Patients.get($stateParams.patientId).then(function(onePatient){
    $scope.patient = onePatient.data[0].patient;
    $scope.patientDevices = onePatient.data[0].devices;
    for (var i=0; i < onePatient.data[0].devices.length; i++){
      if(onePatient.data[0].devices[i].state == "ON" || onePatient.data[0].devices[i].state == "OPEN" || onePatient.data[0].devices[i].state == "PLAY"){
        $scope.patientDevices[i].state = true;
      }
      else{
        $scope.patientDevices[i].state = false;
      }
      switch($scope.patientDevices[i].name){
        case "Fernseher": 
          $scope.patientDevices[i].icon = "ion-monitor";
          break;
        case "Rollladen":
          $scope.patientDevices[i].icon = "ion-ios-sunny";
          break;
        case "Licht":
          $scope.patientDevices[i].icon = "ion-ios-lightbulb";
          break;
        case "Radio":
          $scope.patientDevices[i].icon = "ion-headphone";
          break;
        case "T\u00fcr":
          $scope.patientDevices[i].icon = "ion-home";
          break;
      }
    }
    $scope.patientHeartRates = onePatient.data[0].heartrates;
    $scope.patientHeartRatesLast = $scope.patientHeartRates[$scope.patientHeartRates.length-1].value;
    $scope.patientSleepCycles = onePatient.data[0].sleepcycles;
    $scope.patientSleepCyclesLastTo = $scope.patientSleepCycles[$scope.patientSleepCycles.length-1].dateto;
    $scope.patientSleepCyclesLastFrom = $scope.patientSleepCycles[$scope.patientSleepCycles.length-1].datefrom;
    $scope.patientSleepCyclesLast = moment($scope.patientSleepCyclesLastTo).diff(moment($scope.patientSleepCyclesLastFrom), 'hours')
    $scope.toggleChange = function(itemID, itemState, itemName) {
      if(itemState == true && itemName == "Radio"){
        itemState = 5;
      }else if (itemState == false && itemName == "Radio"){
        itemState = 4;
      }
      else if(itemState == true && itemName == "Rollladen" || itemState == true && itemName == "T\u00fcr"){
        itemState = 6;
      }
      else if(itemState == false && itemName == "Rollladen" || itemState == false && itemName == "T\u00fcr"){
        itemState = 7;
      }
      else if(itemState == true)
      {
        itemState = 1;
      }
      else {
        itemState = 2;
      }
      itemID = parseInt(itemID);
      var state = itemState;
      //var data ={patientdeviceId: itemID, state: itemState};


    $http.put('http://smartcare.mi.hdm-stuttgart.de/api/public/patientdevice.json/' + itemID, "state="+state)
            .success(function (state, status) {
                console.log("HTTP POST: " + status);
            })
            .error(function (state, status) {
              console.log("Nicht gut");
            });

 //     console.log(data);
      console.log(itemID);
      console.log(itemState); 
    console.log('Push Notification Change');
  };
  })

  }

  fetchonePatient();

 $interval(function() { 
  fetchonePatient();
}, 5000);
})

.controller('PatientAddCtrl', function($scope) {
})

.controller('PatientHeartCtrl', function($scope,$stateParams, $interval, Patients) {
  function fetchHeartrate(){
    Patients.get($stateParams.patientId).then(function(onePatient){
        var patientHeartRates = onePatient.data[0].heartrates;
        var durchschnitt = 0;
        var datum = new Array;
        var datumTag = new Array;
        var wert = new Array;
        var datum_chart = new Array;
        var durchgang = 1;
        var wertTag = new Array;
        var wertStunde = new Array;
        
        for (var i = 0; i < patientHeartRates.length; i++) {
            datum[i] = moment(patientHeartRates[i].date);
            datum[i] = moment(datum[i].format('MM/DD/YYYY'));
            datumTag[i] = moment(patientHeartRates[i].date);
            datumTag[i] = moment(datumTag[i].format('MM/DD/YYYY HH:mm'));
        }  
        var teiler = 0;
  
        for (var j = 0; j < patientHeartRates.length-1; j++){ 
          var k = j + 1;
          ++teiler; 
            if(moment(datum[j]).isSame(datum[k]) && k < patientHeartRates.length-1){
                durchschnitt += parseInt(patientHeartRates[j].value);
            }
            else{
                durchschnitt += parseInt(patientHeartRates[j].value);
                durchschnitt = durchschnitt / teiler;
                teiler = 1;
                datum_chart.push(datum[j]._i);
                wert.push(durchschnitt);
                durchschnitt = 0;
                durchschnitt += parseInt(patientHeartRates[k].value);
                ++durchgang;
            }
        }

    $scope.labels = datum_chart;
    console.log(datum_chart);
    $scope.series = ['Puls'];
    $scope.data = [
        wert
    ];


    console.log($scope.data, 'first');


      
      var today = moment("2016-05-14").startOf('day'); 
      today = moment(today.format('MM/DD/YYYY HH:mm'));


      var l = 0;
      for (var j = 0; j < patientHeartRates.length-1; j++){ 
          if(moment(datumTag[j]).isSame(today)){
            wertTag.push(parseInt(patientHeartRates[j].value));
            today = moment("2016-05-14").hours(l+3).minutes(0).seconds(0).milliseconds(0);
            today = moment(today.format('MM/DD/YYYY HH:mm'));
            l = l + 3;
            j = 0;
          }
      }
    $scope.dataTag = [wertTag];

    var lasthour = moment().startOf('minute'); 
    var lasthourRemoveOne = moment(lasthour).subtract(1.00, 'H');
    

    lasthour = moment(lasthour.format('MM/DD/YYYY HH:mm'));
    lasthourRemoveOne = moment(lasthourRemoveOne.format('MM/DD/YYYY HH:mm'));

    var chart_labels = new Array;


    var datumzwischen = moment(datumTag[0]);
    console.log(datumzwischen, "erster wert");
    var maxHour = 60;

    for (var v = 0; v <= patientHeartRates.length-1; v++){
      if(moment(datumTag[v]).isBetween(lasthourRemoveOne, lasthour, 'HH')){
        
        //console.log(datumTag[v], datumzwischen, 'fooo');
        //console.log(moment(datumTag[v]).diff(datumzwischen, 'minutes'), 'dazwischen');
        if(Math.abs(moment(datumTag[v]).diff(datumzwischen, 'minutes')) < 1){
            continue;
        } else {
            datumzwischen = datumTag[v];
        } 
            diffDate = Math.abs(moment(datumTag[v]).diff(moment(), 'minutes'))

            wertStunde[60-diffDate] = parseInt(patientHeartRates[v].value);
            console.log(datumTag[v], moment(), diffDate, 'aaaa');
            //chart_labels[]push("Wert"); 
            datumzwischen = datumTag[v];

    
      //  if(moment(datumTag[v]).isBefore(lasthourvariabel75)){
      //    console.log("test1");
          
      //  }
      //  else if (moment(datumTag[v]).isBefore(lasthourvariabel60)){
      //    console.log("test2");
       //   wertStunde[v] = parseInt(patientHeartRates[v].value);
       // }
        //else if(moment(datumTag[v]).isBefore(lasthourvariabel45)){
       //   console.log("test3");
      //    wertStunde[v] = parseInt(patientHeartRates[v].value);
       // }
        //else if(moment(datumTag[v]).isBefore(lasthourvariabel30)){
        //  console.log("test4");
       //   wertStunde[v] = parseInt(patientHeartRates[v].value);
       // }
      //  else if(moment(datumTag[v]).isBefore(lasthourvariabel15)){
     //     console.log("test5");
    //      wertStunde[v] = parseInt(patientHeartRates[v].value);
       // }
      }
    }

    $scope.dataStunde = [wertStunde];
//$scope.labelsStunde =[]; 
//$scope.labelsStunde = chart_labels;

    
    console.log(wertStunde);
    });

    var foo = [];
    var today = moment();
     today.subtract(1, 'hours')
    foo.push(today.format('HH:mm').toString() );
    for(var k = 60; k >= 1; k-- ){
        if(k%10 == 0 && k != 60)
        { 
          foo.push(today.add(10, 'minutes').format('HH:mm').toString() );
        }
        else
        {
           foo.push(""); 
        }
    }
    foo.push(today.add(10, 'minutes').format('HH:mm').toString() );

    $scope.labelsStunde = foo;
}

    $scope.labelsTag = ["0:00 Uhr", "03:00 Uhr", "06:00 Uhr", "09:00 Uhr", "12:00 Uhr", "15:00 Uhr", "18:00 Uhr", "21:00 Uhr"];
    
    
    
    //$scope.labelsStunde = ["0:00 Uhr", "03:00 Uhr", "06:00 Uhr", "09:00 Uhr", "12:00 Uhr", "15:00 Uhr", "18:00 Uhr", "21:00 Uhr"];
    fetchHeartrate();
    $interval(function() {
      fetchHeartrate();
    }, 3000);
    
})

.controller('PatientSleepCtrl', function($scope, $stateParams, Patients) {
  Patients.get($stateParams.patientId).then(function(onePatient){
    var patientSleepCycles = onePatient.data[0].sleepcycles;
    var datum = new Array;
    var wert = new Array;

    for (var i = 0; i < patientSleepCycles.length; i++) {
            datum[i] = moment(patientSleepCycles[i].datefrom);
            datum[i] = moment(datum[i].format('MM/DD/YYYY'));
            datum[i] = datum[i]._i;
            wert[i] = moment(patientSleepCycles[i].dateto).diff(moment(patientSleepCycles[i].datefrom), 'hours');
        }  
    $scope.labels = datum;
    $scope.series = ['Schlafphasen'];
    $scope.data = [
        wert
    ];
  })
})

.controller('AccountCtrl', function($scope) {
  $scope.settings = {
    enableFriends: true
  };
});

// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js
angular.module('starter', ['ionic', 'starter.controllers', 'starter.services', 'chart.js', 'ngCordova'])

.run(function($ionicPlatform, $cordovaLocalNotification) {
  $ionicPlatform.ready(function() {
    var socket = io.connect('http://smartcare.mi.hdm-stuttgart.de:8080/');
    
    socket.on('notification', function (data) {
    var  text = data.text;
    alert("Notruf abgesetzt");
 //   $cordovaLocalNotification.schedule({message: "Notruf abgesetzt"});
    // ...
});



    // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
    // for form inputs)
    if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
      cordova.plugins.Keyboard.disableScroll(true);

    }
    if (window.StatusBar) {
      // org.apache.cordova.statusbar required
      StatusBar.styleDefault();
    }
  });
})

.config(function($stateProvider, $urlRouterProvider, $httpProvider) {
$httpProvider.defaults.headers.common = {};
  $httpProvider.defaults.headers.post = {};
  $httpProvider.defaults.headers.put = {};
  $httpProvider.defaults.headers.patch = {};

  // Ionic uses AngularUI Router which uses the concept of states
  // Learn more here: https://github.com/angular-ui/ui-router
  // Set up the various states which the app can be in.
  // Each state's controller can be found in controllers.js
  $stateProvider

  // setup an abstract state for the tabs directive
    .state('tab', {
    url: '/tab',
    abstract: true,
    templateUrl: 'templates/tabs.html'
  })

  // Each tab has its own nav history stack:

  .state('tab.patients', {
      url: '/patients',
      views: {
        'tab-patients': {
          templateUrl: 'templates/tab-patients.html',
          controller: 'PatientsCtrl'
        }
      }
    })
    .state('tab.patient-detail', {
      url: '/patients/:patientId',
      views: {
        'tab-patients': {
          templateUrl: 'templates/patient-detail.html',
          controller: 'PatientDetailCtrl'
        }
      }
    })
    .state('tab.patient-add', {
      url: '/patients/patient-add',
      views: {
        'tab-patients': {
          templateUrl: 'templates/patient-add.html',
          controller: 'PatientAddCtrl'
        }
      }
    })
    .state('tab.patient-heart', {
      url: '/patients/:patientId/patient-heart',
      views: {
        'tab-patients': {
          templateUrl: 'templates/patient-heart.html',
          controller: 'PatientHeartCtrl'
        }
      }
    })
    .state('tab.patient-sleep', {
      url: '/patients/:patientId/patient-sleep',
      views: {
        'tab-patients': {
          templateUrl: 'templates/patient-sleep.html',
          controller: 'PatientSleepCtrl'
        }
      }
    })
  .state('tab.account', {
    url: '/account',
    views: {
      'tab-account': {
        templateUrl: 'templates/tab-account.html',
        controller: 'AccountCtrl'
      }
    }
  });

  // if none of the above states are matched, use this as the fallback
  $urlRouterProvider.otherwise('/tab/patients');

});

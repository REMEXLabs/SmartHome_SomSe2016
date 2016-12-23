angular.module('starter', ['ionic', 'starter.controllers', 'starter.services', 'chart.js', 'ngCordova'])

.run(function($ionicPlatform, $cordovaLocalNotification) {
  $ionicPlatform.ready(function() {
    var socket = io.connect('http://smartcare.mi.hdm-stuttgart.de:8080/');
    
    socket.on('notification', function (data) {
    var  text = data.text;
    alert("Notruf abgesetzt");
});

    if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
      cordova.plugins.Keyboard.disableScroll(true);

    }
    if (window.StatusBar) {
      StatusBar.styleDefault();
    }
  });
})

.config(function($stateProvider, $urlRouterProvider, $httpProvider) {
$httpProvider.defaults.headers.common = {};
  $httpProvider.defaults.headers.post = {};
  $httpProvider.defaults.headers.put = {};
  $httpProvider.defaults.headers.patch = {};
  $stateProvider

    .state('tab', {
    url: '/tab',
    abstract: true,
    templateUrl: 'templates/tabs.html'
  })


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

  $urlRouterProvider.otherwise('/tab/patients');

});

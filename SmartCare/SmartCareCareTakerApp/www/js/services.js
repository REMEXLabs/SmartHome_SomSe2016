angular.module('starter.services', [])

.factory('Patients', function($http, $stateParams) {
 var patients = [];
 var onePatient =[];
 var patientsAll = [];

  return {
    all: function() {
       return $http.get("http://smartcare.mi.hdm-stuttgart.de/api/public/carerpatient.json").then(function(response){
        patients = response;
        return patients;         
       });     
    },
    remove: function(patient) {
      patients.splice(patients.indexOf(patient), 1);
    },
    get: function(patientId) {
      for (var i = 0; i < patients.data.length; i++) {
        if (patients.data[i].patientId == parseInt(patientId)) {
          return $http.get("http://smartcare.mi.hdm-stuttgart.de/api/public/patient.json/" + patientId).then(function(response){
              onePatient = response;
              return onePatient;
          });
        };
      }
      return null;
    }
  };
});

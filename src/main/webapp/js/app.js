'use strict';

angular.module('login', []).controller('appControl', function($scope, $http) {

    $scope.startlogin = function() {
        let message = {'action': 'start'};
        console.log("We're in LOGIN");
        $http({
            url: genUrl,
            method: 'GET'
            }).then(function(response) {
              $log.debug("AppAcc Svc : " + response.status + ' ' + response.statusText + " %o - OK");
              q.resolve(response.data.requestQueryString);
            }, function(response) {
              $log.debug("AppAcc Svc : " + response.status + ' ' + response.statusText + " %o - FAILED", response.data);
              q.reject(response.data.error);
            });
    }
  
});
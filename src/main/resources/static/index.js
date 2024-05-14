angular.module('timeline',[]).controller('indexController', function ($scope, $http) {
    const contextPath = 'http://192.168.0.229:8189/timeline/api/v1'; // for office


    $scope.loadParameters = function () {
        $http.get(contextPath + '/parameters')
            .then(function (response) {
                $scope.ParametersList = response.data;
                $scope.functions = ParametersList.functions;
            });

    };

    $scope.loadParameters();
});
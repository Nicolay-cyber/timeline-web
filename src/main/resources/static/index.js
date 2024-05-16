angular.module('timeline',[]).controller('indexController', function ($scope, $http) {
    //const contextPath = 'http://192.168.0.229:8189/timeline/api/v1'; // for office
    const contextPath = 'http://localhost:8189/timeline/api/v1'; // for offline


    $scope.loadParameters = function () {
        $http.get(contextPath + '/parameters')
            .then(function (response) {
                $scope.ParametersList = response.data;
                $scope.functions = ParametersList.functions;
            });

    };

    $scope.loadParameters();
});
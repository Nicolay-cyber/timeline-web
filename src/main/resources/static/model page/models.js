angular.module('timeline', [])
    .controller('modelsController', function ($scope, $http, $window) {
        //const contextPath = 'http://192.168.0.157:8189/timeline/api/v1'; // for home
        const contextPath = 'http://localhost:8189/timeline/api/v1';
        //const contextPath = 'http://192.168.0.229:8189/timeline/api/v1'; // for office

        $scope.loadModels = function () {
            $http.get(contextPath + '/models').then(function (response) {
                $scope.ModelsList = response.data;
            });
        };

        $scope.openModel = function (model) {
            $window.location.href = `model-details.html?id=${model.id}`;
        };

        $scope.loadModels();
    });

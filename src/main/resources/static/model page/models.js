angular.module('timeline', []).controller('modelsController', function ($scope, $http, $sce) {
    // Set the context path based on the environment
    //const contextPath = 'http://192.168.0.229:8189/timeline/api/v1'; // for office
    //const contextPath = 'http://192.168.0.157:8189/timeline/api/v1'; // for home
    const contextPath = 'http://localhost:8189/timeline/api/v1'; // for offline

    $scope.loadModels = function () {
        $http.get(contextPath + '/models').then(function (response) {
            $scope.ModelsList = response.data;
        });
    };


    $scope.openModel = function (model) {
        sessionStorage.setItem('selectedModel', JSON.stringify(model));
        //window.open('model-details.html', '_blank');
        window.location.href = 'model-details.html';
    };

    $scope.loadModels();
})


    .controller('modelDetailsController', function ($scope, $http, $location, $timeout) {
        const contextPath = 'http://localhost:8189/timeline/api/v1'; // for offline
        const modelData = sessionStorage.getItem('selectedModel');
        if (modelData) {
            $scope.model = JSON.parse(modelData);
            $timeout(function () {
                if (window.MathJax) {
                    MathJax.typeset();
                }
            }, 0);
        }
    });

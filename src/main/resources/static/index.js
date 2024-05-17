angular.module('timeline',[]).controller('indexController', function ($scope, $http) {
    const contextPath = 'http://192.168.0.229:8189/timeline/api/v1'; // for office
    //const contextPath = 'http://localhost:8189/timeline/api/v1'; // for offline


    $scope.loadGraphData = function (parameterId) {
        $http.get(contextPath + '/graph/' + parameterId)
            .then(function (response) {
                // Handle the response and update the chart part
                $scope.parameterLine = response.data;
                // Update the chart with the new data
                updateChart($scope.parameterLine);
            });
    };

    $scope.loadParameters = function () {
        $http.get(contextPath + '/parameters')
            .then(function (response) {
                $scope.ParametersList = response.data;
                $scope.functions = ParametersList.functions;
            });

    };

    $scope.loadParameters();

    function updateChart(data) {
        // Assuming myChart is the Chart instance
        myChart.data.labels = data.labels;
        myChart.data.datasets[0].data = data.datasets[0].data; // Assuming only one dataset

        // Update the chart
        myChart.update();
    }


    // Add click event handler to table rows
    angular.element(document).ready(function () {
        angular.element('table tr').on('click', function () {
            var parameterId = angular.element(this).data('id');
            $scope.loadGraphData(parameterId);
        });
    });

});
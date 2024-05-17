angular.module('timeline',[]).controller('indexController', function ($scope, $http) {
    const contextPath = 'http://192.168.0.229:8189/timeline/api/v1'; // for office
    //const contextPath = 'http://localhost:8189/timeline/api/v1'; // for offline


    $scope.loadParameters = function () {
        $http.get(contextPath + '/parameters').then(function (response) {
            $scope.ParametersList = response.data;
        });
    };

    let ctx = document.getElementById("myChart").getContext("2d");
    let myChart = new Chart(ctx, {
        type: "line",
        data: {
            labels: [],
            datasets: [{
                label: "label",
                data: [],
                backgroundColor: "rgba(153,205,1,0.6)",
            }],
        }
    });


    $scope.loadGraphData = function (parameterId) {
        $http.get(contextPath + '/graph/' + parameterId).then(function (response) {
            let data = response.data;
            updateChart(data.labels, data.points, data.parameterName);
        });
    };

    $scope.loadParameters();

    function updateChart(labels, points, parameterName) {
        myChart.data.labels = labels;
        myChart.data.datasets[0].data = points;
        myChart.data.datasets[0].label = parameterName;
        myChart.update();
    }

    angular.element(document).ready(function () {
        angular.element('table tr').on('click', function () {
            var parameterId = angular.element(this).data('id');
            $scope.loadGraphData(parameterId);
        });
    });

});
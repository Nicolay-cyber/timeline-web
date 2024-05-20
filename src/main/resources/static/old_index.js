angular.module('timeline',[]).controller('indexController', function ($scope, $http, $sce) {
    //const contextPath = 'http://192.168.0.229:8189/timeline/api/v1'; // for office
    const contextPath = 'http://192.168.0.157:8189/timeline/api/v1'; // for home
    //const contextPath = 'http://localhost:8189/timeline/api/v1'; // for offline

    $scope.loadParameters = function () {
        $http.get(contextPath + '/parameters').then(function (response) {
            $scope.ParametersList = response.data;
/*

            $scope.$applyAsync(function() {
                MathJax.typesetPromise(); // Render MathJax after AngularJS has updated the DOM
            });*/
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

    $scope.loadGraphData = function (parameter) {
        $http.get(contextPath + '/graph/' + parameter.id).then(function (response) {
            let data = response.data;
            updateChart(data.labels, data.points, data.parameterName);
            $('#parameterTitle').text(parameter.name);
            $('#parameterDescription').text(parameter.description);
            $scope.functionList = parameter.functions;
            console.log($scope.functionList);
        });
    };
/*
    $scope.renderMath = function (expression) {
        return $sce.trustAsHtml(expression);
    };*/
    $scope.loadParameters();


    function updateChart(labels, points, parameterName) {
        myChart.data.labels = labels;
        myChart.data.datasets[0].data = points;
        myChart.data.datasets[0].label = parameterName;
        myChart.update();
    }
/*

    angular.element(document).ready(function () {
        angular.element('table tr').on('click', function () {
            var parameterId = angular.element(this).data('id');
            $scope.loadGraphData(parameterId);
        });
    });

// Создание нового математического поля
    var MQ = MathQuill.getInterface(2);
    var answerSpan = document.getElementById('answer');
    var answerMathField = MQ.MathField(answerSpan, {
        handlers: {
            edit: function() {
                var enteredMath = answerMathField.latex(); // Get entered math in LaTeX format
                // Возможная обработка введенной формулы
            }
        }
    });
*/

    /*/// Обработка нажатия кнопки
    document.getElementById('sendFormulaButton').addEventListener('click', function() {
        var enteredMath = answerMathField.latex(); // Получить введенную формулу в формате LaTeX
        $http.post(contextPath + '/parameters', { formula: enteredMath })
            .then(function(response) {
                alert('Formula sent successfully!');
                // Дополнительная обработка успешного ответа
            })
            .catch(function(error) {
                alert('Error sending formula.');
                console.error(error);
            });
    });*/
});


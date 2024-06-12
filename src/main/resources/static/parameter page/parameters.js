angular.module('timeline', []).controller('indexController', function ($scope, $http, $sce, $timeout) {
    // Set the context path based on the environment
    //const contextPath = 'http://192.168.0.229:8189/timeline/api/v1'; // for office
    //const contextPath = 'http://192.168.0.157:8189/timeline/api/v1'; // for home
    const contextPath = 'http://localhost:8189/timeline/api/v1'; // for offline

    // Function to open the modal for adding a new parameter
    $scope.openNewParameterModal = function () {
        $scope.newParameter = {
            name: '',
            abbreviation: '',
            description: '',
            functions: []
        };
        $scope.newFunction = {
            startPoint: '',
            endPoint: '',
            expression: '',
            relatedParameterIds: []
        };

        $('#newParameterFunctionList').empty();
        $('#addParameterModal').modal('show');
    };
// Function to convert relatedParameterIds string to array of Long
    $scope.processRelatedParameterIds = function (idsString) {
        return idsString.split(',').map(id => parseInt(id.trim()));
    };
    // Function to add a new function field
    $scope.addFunction = function () {

        let newFunctionCopy = angular.copy($scope.newFunction);
        newFunctionCopy.expression = '\\[' + answerMathField.latex() + '\\]';
        newFunctionCopy.relatedParameterIds = $scope.processRelatedParameterIds($scope.newFunction.relatedParameterIds);
        $scope.newParameter.functions.push(newFunctionCopy);

        answerMathField.latex('');
        $scope.newFunction = {
            startPoint: '',
            endPoint: '',
            expression: '',
            relatedParameterIds: ''
        };

        $timeout(function () {
            MathJax.typesetPromise();
        });
    };
    $scope.editFunction = function (index) {
        let func = $scope.newParameter.functions[index];
        $scope.newFunction = angular.copy(func);
        $scope.newFunction.relatedParameterIds = func.relatedParameterIds.join(', ');
        answerMathField.latex(func.expression.replace('\\[', '').replace('\\]', ''));
        $scope.newParameter.functions.splice(index, 1);
        $scope.$applyAsync(function () {
            MathJax.typesetPromise();
        });
    };

    $scope.deleteFunction = function (index) {
        $scope.newParameter.functions.splice(index, 1); // Удалить функцию
        $scope.$applyAsync(function () {
            MathJax.typesetPromise();
        });
    };
// Function to load parameters
    $scope.loadParameters = function () {
        $http.get(contextPath + '/parameters').then(function (response) {
            $scope.ParametersList = response.data;
            if ($scope.ParametersList.length > 0) {
                // Automatically load data for the first parameter
                $scope.loadGraphData($scope.ParametersList[0]);
            }
        });
    };

    // Function to add the new parameter
    $scope.addParameter = function () {
        $http.post(contextPath + '/parameters', $scope.newParameter).then(function (response) {
            console.log('new parameter added successfully');
            // Reload the parameters list after adding the new parameter
            $scope.loadParameters(); // Добавить эту строку для обновления списка параметров
            $('#addParameterModal').modal('hide');
        });
    };

    // Function to delete the parameter
    $scope.deleteParameter = function (parameterId) {
        if (confirm('Are you sure you want to delete this parameter?')) {
            $http.delete(contextPath + '/parameters/' + parameterId).then(function () {
                console.log('parameter deleted successfully');
                $scope.loadParameters();
            });
        }
    };


    // Initialize Chart.js
    let ctx = document.getElementById("myChart").getContext("2d");
    let myChart = new Chart(ctx, {
        type: "line",
        data: {
            labels: [],
            datasets: [{
                data: [], // Remove the 'label' field
                backgroundColor: "rgba(153,205,1,0.6)",
            }],
        },
        options: {
            legend: {
                display: false // Disable legend display
            },
            scales: {
                xAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'X Axis Label' // Set default label for x-axis
                    }
                }],
                yAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'Y Axis Label' // Set default label for y-axis
                    }
                }]
            }
        }
    });

    // Variable to store the ID of the currently displayed parameter
    $scope.currentParameterId = null;

    // Function to load graph data for a parameter
    $scope.loadGraphData = function (parameter) {
        // Check if the new parameter is different from the current one
        if (parameter.id !== $scope.currentParameterId) {
            $http.get(contextPath + '/graph/' + parameter.id).then(function (response) {

// Extract x and y values from points


                // Update the chart, parameter title, and description
                updateChart(response.data.labels, response.data.points, parameter.name, parameter.unit.abbreviation);
                $('#parameterTitle').text(parameter.name + ' (' + parameter.abbreviation + '), ' + parameter.unit.abbreviation);
                $('#parameterDescription').text(parameter.description);
// Clear the points list before adding new items
                $('#pointsList').empty();
                // Add points to the points list
                if (parameter.points && parameter.points.length > 0) {
                    parameter.points.forEach(function (point) {
                        $('#pointsList').append('<div class="point-item mr-2">' + ' (' + point.x + ', ' + point.y + ') ' + '</div>');
                    });
                } else {
                    $('#pointsList').append('<p>No data</p>');
                }
                // Clear the function list before adding new items
                $('#functionListHTML').empty();

                // Iterate over each function, render it, and add it to the list
                if (parameter.functions && parameter.functions.length > 0) {
                    parameter.functions.forEach(function (func) {
                        $('#functionListHTML').append('<li class="list-group-item">' + 'Inderval: ' + func.startPoint + ' - ' + func.endPoint + '<br>' + $scope.renderMath(func.expression) + '</li>');
                    });
                } else {
                    $('#functionListHTML').append('<li class="list-group-item">No data</li>');
                }

                $scope.$applyAsync(function () {
                    if (typeof MathJax !== 'undefined') {
                        MathJax.typesetPromise(); // Render MathJax after AngularJS has updated the DOM
                    }
                });

                // Update the ID of the current parameter
                $scope.currentParameterId = parameter.id;
            });
        }
    };


    // Function to render math expressions using $sce.trustAsHtml
    $scope.renderMath = function (expression) {
        return $sce.trustAsHtml(expression);
    };

    // Function to update the chart with new data
    function updateChart(labels, points, parameterName, parameterAbbreviation) {
        myChart.data.labels = labels;
        myChart.data.datasets[0].data = points;
        // Update the axis labels
        myChart.options.scales.xAxes[0].scaleLabel.labelString = 'Time, years'; // Use provided label or default
        myChart.options.scales.yAxes[0].scaleLabel.labelString = parameterName + ', ' + parameterAbbreviation || 'Y Axis'; // Use provided label or default

        myChart.update();
    }


    // Create a new MathQuill field
    var MQ = MathQuill.getInterface(2);
    var answerSpan = document.getElementById('newFormulaField');
    var answerMathField = MQ.MathField(answerSpan, {
        handlers: {
            edit: function () {
                var enteredMath = answerMathField.latex(); // Get entered math in LaTeX format
                $scope.newFunction.expression = enteredMath;
            }
        }
    });
    $scope.loadParameters();
});

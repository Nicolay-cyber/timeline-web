angular.module('timeline', ['ui.bootstrap']).controller('indexController', function ($scope, $http, $sce, $timeout) {
    // Set the context path based on the environment
    //const contextPath = 'http://192.168.0.229:8189/timeline/api/v1'; // for office
    const contextPath = 'http://192.168.0.157:8189/timeline/api/v1'; // for home
    //const contextPath = 'http://localhost:8189/timeline/api/v1'; // for offline

    // Function to load units of measurement
    $scope.loadUnits = function () {
        $http.get(contextPath + '/units').then(function (response) {
            $scope.units = response.data;
        });
    };

    $scope.currentParameter = null;
    // Initialize new parameter
    $scope.newParameter = {
        name: '',
        abbreviation: '',
        description: '',
        functions: [],
        points: [],
        unit: null
    };
    // Function to handle selection of unit from typeahead
    $scope.onSelectUnit = function ($item, $model, $label) {
        $scope.newParameter.unit = $item;
    };
    // Function to format the input field
    $scope.formatInput = function ($model) {
        return $model ? $model.name + ' (' + $model.abbreviation + ')' : '';
    };
    // Function to open the modal for adding a new parameter
    $scope.openNewParameterModal = function () {
        $scope.newParameter = {
            name: '',
            abbreviation: '',
            description: '',
            functions: [],
            points: [],
            unit: null
        };
        $scope.newFunction = {
            startPoint: '',
            endPoint: '',
            expression: '',
            tagParamExpression: '',
            relatedParameterIds: []
        };
        $scope.newPoint = {
            x: '',
            y: ''
        };

        $('#newParameterFunctionList').empty();
        $('#addParameterModal').modal('show');
    };
// Function to convert relatedParameterIds string to array of Long
    $scope.processRelatedParameterIds = function (idsString) {
        if (typeof idsString !== 'string' || idsString.trim() === '') {
            return [];
        }
        return idsString.split(',').map(id => parseInt(id.trim()));
    };
    // Function to add a new function field
    $scope.addFunction = function () {

        let newFunctionCopy = angular.copy($scope.newFunction);
        newFunctionCopy.tagParamExpression = '\\[' + answerMathField.latex() + '\\]';
        newFunctionCopy.relatedParameterIds = $scope.processRelatedParameterIds($scope.newFunction.relatedParameterIds);
        $scope.newParameter.functions.push(newFunctionCopy);

        answerMathField.latex('');
        $scope.newFunction = {
            startPoint: '',
            endPoint: '',
            expression: '',
            tagParamExpression: '',
            relatedParameterIds: ''
        };

        $timeout(function () {
            MathJax.typesetPromise();
        });
    };
    $scope.editFunction = function (index) {
        let func = $scope.newParameter.functions[index];
        $scope.newFunction = angular.copy(func);
        answerMathField.latex(func.tagParamExpression.replace('\\[', '').replace('\\]', ''));
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

    // Function to add a new point field
    $scope.addPoint = function () {

        let newPointCopy = angular.copy($scope.newPoint);
        $scope.newParameter.points.push(newPointCopy);

        $scope.newPoint = {
            x: '',
            y: ''
        };
    };
    $scope.editPoint = function (index) {
        let point = $scope.newParameter.points[index];
        $scope.newPoint = angular.copy(point);
        $scope.newParameter.points.splice(index, 1);
    };

    $scope.deletePoint = function (index) {
        $scope.newParameter.points.splice(index, 1); // remove the point
    };


// Function to load parameters
    $scope.loadParameters = function () {
        $http.get(contextPath + '/parameters').then(function (response) {
            $scope.ParametersList = response.data;
            if ($scope.ParametersList.length > 0) {
                if ($scope.currentParameter == null) {
                    $scope.loadGraphData($scope.ParametersList[0]);
                } else {
                    $scope.loadGraphData($scope.currentParameter);
                }
            }
        });
    };

    // Function to add the new parameter or edit the existing
    $scope.addParameter = function () {
        if (!$scope.newParameter.name || !$scope.newParameter.abbreviation || !$scope.newParameter.unit) {
            $('#errorModal').modal('show');
            return;
        }

        const request = $scope.newParameter.id
            ? $http.put(`${contextPath}/parameters/${$scope.newParameter.id}`, $scope.newParameter)
            : $http.post(`${contextPath}/parameters`, $scope.newParameter);

        request.then(function (responseParameter) {
            console.log('Parameter saved successfully');
            $scope.loadParameters();

            $http.get(contextPath + '/graph/' + responseParameter.data.id).then(function (response) {

                // Update the chart, parameter title, and description
                updateChart(response.data.labels, response.data.points, responseParameter.data.name, responseParameter.data.unit.abbreviation);

                $timeout(function () {
                    MathJax.typeset();
                }, 0);

                });

            $scope.currentParameter = responseParameter.data;
            $('#addParameterModal').modal('hide');
            $timeout(function () {
                MathJax.typesetPromise();
            });
        });
    };

    $scope.editParameter = function (parameterId) {
        $scope.isEditing = true; // Set editing flag
        $scope.newParameter = angular.copy($scope.currentParameter);
        $('#addParameterModal').modal('show');
        $timeout(function () {
            MathJax.typeset();
        }, 0);
    };



    $scope.confirmDeleteParameter = function (parameterId) {
        $http.get(contextPath + '/parameters/' + parameterId + '/dependent-functions').then(function (response) {
            $scope.dependentFunctions = response.data;
            $scope.parameterToDelete = parameterId;

            // Check if there are dependent functions to display the modal
            if ($scope.dependentFunctions.length > 0) {
                $('#confirmDeleteModal').modal('show');
                $timeout(function () {
                    MathJax.typeset();
                }, 0);
            } else {
                // If no dependent functions, directly delete
                $scope.deleteParameter();
            }
        });
    };

    // Function to delete the parameter
    $scope.deleteParameter = function () {
        if (confirm('Are you sure you want to delete this parameter?')) {
            $http.delete(contextPath + '/parameters/' + $scope.parameterToDelete).then(function () {
                console.log('parameter deleted successfully');
                $scope.currentParameter = null;
                $scope.loadParameters();
                $('#confirmDeleteModal').modal('hide');
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
                backgroundColor: 'rgba(85, 85, 85, 0.2)', // Темно серый цвет с прозрачностью 0.2
                borderColor: 'rgba(85, 85, 85, 1)', // Цвет линии графика
            }],
        },
        options: {
            legend: {
                display: false // Disable legend display
            },
            scales: {
                xAxes: [{
                    ticks: {
                        autoSkip: true,
                        maxTicksLimit: 15
                    },
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
    // Function to load graph data for a parameter
    $scope.loadGraphData = function (parameter) {
        // Check if the new parameter is different from the current one
        if ($scope.currentParameter == null || parameter.id !== $scope.currentParameter.id) {
            $http.get(contextPath + '/graph/' + parameter.id).then(function (response) {

                // Update the chart, parameter title, and description
                updateChart(response.data.labels, response.data.points, parameter.name, parameter.unit.abbreviation);

                $timeout(function () {
                    MathJax.typeset();
                }, 0);

                // Update the ID of the current parameter
                $scope.currentParameter = angular.copy(parameter);
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
                $scope.newFunction.tagParamExpression = enteredMath;
            }
        }
    });
    $scope.loadParameters();
    $scope.loadUnits();

});

angular.module('timeline', []).controller('indexController', function ($scope, $http, $sce) {
    // Set the context path based on the environment
    const contextPath = 'http://192.168.0.229:8189/timeline/api/v1'; // for office
    //const contextPath = 'http://192.168.0.157:8189/timeline/api/v1'; // for home
    //const contextPath = 'http://localhost:8189/timeline/api/v1'; // for offline

    // Initialize new parameter object
    $scope.newParameter = {
        name: '',
        abbreviation: '',
        description: '',
        functions: []
    };

    $scope.newFunction = {
        startPoint: '',
        endPoint: '',
        expression: ''
    };

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
            expression: ''
        };

        // Debugging log
        console.log('Clearing function list');
        console.log($('#newParameterFunctionList'));

        $('#newParameterFunctionList').empty();
        $('#addParameterModal').modal('show');
    };

    // Function to add a new function field
    $scope.addFunction = function () {
        let newFunctionCopy = angular.copy($scope.newFunction);
        newFunctionCopy.expression = '\\[' + answerMathField.latex() + '\\]'; // Capture the LaTeX from MathQuill
        console.log("newFunctionCopy: " + newFunctionCopy.expression)
        $scope.newParameter.functions.push(newFunctionCopy);

        $scope.newFunction = {
            startPoint: '',
            endPoint: '',
            expression: ''
        };

        // Clear MathQuill field
        answerMathField.latex('');

        // Iterate over each function, render it, and add it to the list
        $scope.newParameter.functions.forEach(function (func) {
            $('#newParameterFunctionList').append('<li class="list-group-item">' +
                'Interval: ' + func.startPoint + ' - ' + func.endPoint + '<br>' + $scope.renderMath(func.expression)
                + '</li>');
        });

        $scope.$applyAsync(function () {
            MathJax.typesetPromise(); // Render MathJax after AngularJS has updated the DOM
        });
    };


    // Function to add the new parameter
    $scope.addParameter = function () {
        $http.post(contextPath + '/parameters', $scope.newParameter).then(function (response) {
            // Reload the parameters list after adding the new parameter
            $scope.loadParameters();
            $('#addParameterModal').modal('hide');
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
    let currentParameterId = null;

    // Function to load graph data for a parameter
    $scope.loadGraphData = function (parameter) {
        // Check if the new parameter is different from the current one
        if (parameter.id !== currentParameterId) {
            $http.get(contextPath + '/graph/' + parameter.id).then(function (response) {
                let data = response.data;

                // Update the chart, parameter title, and description
                updateChart(data.labels, data.points, parameter.name, parameter.abbreviation);
                $('#parameterTitle').text(parameter.name + ', ' + parameter.abbreviation);
                $('#parameterDescription').text(parameter.description);

                // Clear the function list before adding new items
                $('#functionListHTML').empty();

                // Iterate over each function, render it, and add it to the list
                parameter.functions.forEach(function (func) {
                    $('#functionListHTML').append('<li class="list-group-item">' + $scope.renderMath(func.expression) + '</li>');
                });

                $scope.$applyAsync(function () {
                    MathJax.typesetPromise(); // Render MathJax after AngularJS has updated the DOM
                });

                // Update the ID of the current parameter
                currentParameterId = parameter.id;
            });
        }
    };

    // Load parameters when the controller is initialized
    $scope.loadParameters();

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
});

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

    // Function to add the new parameter or edit the existing
    $scope.addParameter = function () {
        if ($scope.newParameter.id) {
            // Update existing parameter
            $http.put(contextPath + '/parameters/' + $scope.newParameter.id, $scope.newParameter).then(function (response) {
                console.log('Parameter updated successfully');
                $scope.loadParameters(); // Reload parameters list
                $('#addParameterModal').modal('hide');
            });
        } else {
            // Add new parameter
            $http.post(contextPath + '/parameters', $scope.newParameter).then(function (response) {
                console.log('New parameter added successfully');
                $scope.loadParameters(); // Reload parameters list
                $('#addParameterModal').modal('hide');
            });
        }
    };
    $scope.editParameter = function (parameterId) {
        // Fetch the parameter details from API or use existing data
        // Assuming you have a way to fetch parameter details by ID

        $scope.newParameter = $scope.currentParameter // Assuming response.data contains parameter details

        $scope.isEditing = true; // Set editing flag
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
        $scope.currentParameter = parameter;

        // Check if the new parameter is different from the current one
        if (parameter.id !== $scope.currentParameterId) {
            $http.get(contextPath + '/graph/' + parameter.id).then(function (response) {

                // Update the chart, parameter title, and description
                updateChart(response.data.labels, response.data.points, parameter.name, parameter.unit.abbreviation);

                $timeout(function () {
                    MathJax.typeset();
                }, 0);

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

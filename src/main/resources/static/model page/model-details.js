angular.module('timeline', [])
    .filter('nl2br', function($sce) {
        return function(text) {
            if (!text) return text;

            // Заменяем пробелы в начале строк на неразрывные пробелы
            const formattedText = text.replace(/(^|\n)(\s+)/g, function(match, p1, p2) {
                return p1 + p2.replace(/ /g, '&nbsp;');
            });

            // Заменяем символы новой строки на <br>
            const newText = formattedText.replace(/\n/g, '<br/>');
            return $sce.trustAsHtml(newText);
        };
    })
    .controller('modelDetailsController', function ($scope, $http, $sce, $location, $timeout, $window) {
        //const contextPath = 'http://192.168.0.157:8189/timeline/api/v1'; // for home
        const contextPath = 'http://localhost:8189/timeline/api/v1';
        //const contextPath = 'http://192.168.0.229:8189/timeline/api/v1'; // for office

        const searchParams = new URLSearchParams($location.absUrl().split('?')[1]);
        const modelId = searchParams.get('id');

        $scope.searchQuery = '';
        $scope.filteredParameters = [];

        $scope.loadModelDetails = function () {
            if (modelId) {
                $http.get(contextPath + `/models/${modelId}`).then(function (response) {
                    $scope.model = response.data;
                    $scope.editableModel = angular.copy($scope.model);
                    drawChart($scope.model);
                    loadParameters();

                    $timeout(function () {
                        // Настройка MathJax
                        MathJax.Hub.Config({
                            tex2jax: {
                                inlineMath: [['$', '$'], ['\\(', '\\)']],
                                displayMath: [['$$', '$$'], ['\\[', '\\]']]
                            },
                            "HTML-CSS": {scale: 100},
                            displayAlign: "left",
                            displayIndent: "2em"
                        });
                        MathJax.Hub.Queue(["Typeset", MathJax.Hub]);
                    });

                });
            }
        };

        function loadParameters() {
            $http.get(contextPath + '/parameters').then(function (response) {
                $scope.allParameters = response.data;
            });
        }
        $scope.filterParameters = function () {
            if ($scope.allParameters) {
                // Exclude already added parameters
                const addedParamIds = $scope.editableModel.parameters.map(param => param.id);
                $scope.filteredParameters = $scope.allParameters.filter(param =>
                    param.tag.toLowerCase().includes($scope.searchQuery.toLowerCase()) &&
                    !addedParamIds.includes(param.id)
                );
            } else {
                $scope.filteredParameters = [];
            }
        };
        $scope.addParameter = function (parameter) {
            if (!$scope.editableModel.parameters) {
                $scope.editableModel.parameters = [];
            }
            if (!$scope.editableModel.parameters.some(p => p.id === parameter.id)) {
                $scope.editableModel.parameters.push(parameter);
                $scope.searchQuery = '';
                $scope.filteredParameters = [];
            }
        };

        $scope.removeParameter = function (parameter) {
            $scope.editableModel.parameters = $scope.editableModel.parameters.filter(p => p.id !== parameter.id);
        };
        function drawChart(model) {
            const ctx = document.getElementById('modelChart').getContext('2d');
            const datasets = [];
            const fixedColors = ['#3A6D80', '#F3CD53', '#D56729', '#9D402D'];

            model.modelGraphDto.yvalues.forEach((yvalue, index) => {
                datasets.push({
                    label: yvalue.parameterName + ",\n " + yvalue.unit.name + ' (' + yvalue.unit.abbreviation + ')',
                    data: yvalue.points.map((point, index) => ({
                        x: model.modelGraphDto.xvalues[index],
                        y: point
                    })),
                    borderColor: index < 4 ? fixedColors[index] : getRandomColor(),
                    fill: false
                });
            });

            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: model.modelGraphDto.xvalues,
                    datasets: datasets
                },
                options: {
                    legend: {
                        display: true,
                        position: "right",
                        labels: {
                            fontSize: 14,
                            fontStyle: 'bold'
                        }
                    },
                    scales: {
                        xAxes: [{
                            ticks: {
                                autoSkip: true,
                                maxTicksLimit: 25
                            },
                            scaleLabel: {
                                display: true,
                                labelString: 'X Axis Label',
                                fontSize: 14,
                                fontStyle: 'bold'
                            }
                        }],
                        yAxes: [{
                            scaleLabel: {
                                display: true,
                                labelString: 'Y Axis Label',
                                fontSize: 14,
                                fontStyle: 'bold'
                            }
                        }]
                    },
                },
            });
        }

        function getRandomColor() {
            const letters = '0123456789ABCDEF';
            let color = '#';
            for (let i = 0; i < 6; i++) {
                color += letters[Math.floor(Math.random() * 16)];
            }
            return color;
        }
        $scope.editModel = function (model) {
            $scope.editableModel = angular.copy(model); // Создаем копию модели для редактирования
            $('#editModelModal').modal('show');
        };

        $scope.saveModel = function () {
            $http.put(contextPath + '/models/' + $scope.editableModel.id, $scope.editableModel)
                .then(function (response) {
                    $scope.model = response.data; // Обновляем оригинальную модель данными с сервера
                    $('#editModelModal').modal('hide'); // Закрываем модальное окно
                    $scope.loadModelDetails();
                }, function (error) {
                    console.error('Error saving model:', error);
                });
        };

        $scope.deleteModel = function () {
            if (confirm('Are you sure you want to delete this model?')) {
                $http.delete(contextPath + '/models/' + $scope.model.id).then(function () {
                    console.log('model deleted successfully');
                    $('#confirmDeleteModal').modal('hide');
                    $window.location.href = `models.html`;
                });
            }
        };


        $scope.loadModelDetails();

    });

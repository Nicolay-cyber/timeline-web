angular.module('timeline', [])
    .controller('modelDetailsController', function ($scope, $http, $location, $timeout) {
        const contextPath = 'http://localhost:8189/timeline/api/v1';
        const searchParams = new URLSearchParams($location.absUrl().split('?')[1]);
        const modelId = searchParams.get('id');

        $scope.loadModelDetails = function () {
            if (modelId) {
                $http.get(contextPath + `/models/${modelId}`).then(function (response) {
                    $scope.model = response.data;
                    drawChart($scope.model);

                    $timeout(function () {
                        MathJax.typesetPromise();
                    });

                });
            }
        };

        $scope.loadModelDetails();

        function drawChart(model) {
            const ctx = document.getElementById('modelChart').getContext('2d');
            const datasets = [];

            model.modelGraphDto.yvalues.forEach(yvalue => {
                datasets.push({
                    label: yvalue.parameterName,
                    data: yvalue.points.map((point, index) => ({
                        x: model.modelGraphDto.xvalues[index],
                        y: point
                    })),
                    borderColor: getRandomColor(),
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
                    scales: {
                        x: {
                            type: 'linear',
                            position: 'bottom'
                        }
                    }
                }
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

    });

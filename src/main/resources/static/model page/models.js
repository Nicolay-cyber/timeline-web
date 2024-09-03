angular.module('timeline', [])
    .service('configService', function($http) {
        let configData = {};

        this.loadConfig = function() {
            return $http.get('../config.yaml').then(function(response) {
                // Используйте js-yaml для парсинга YAML-файла
                configData = jsyaml.load(response.data);
            });
        };

        this.getConfig = function() {
            return configData;
        };
    });
angular.module('timeline')
    .controller('modelsController', function($scope, $http, $window, configService) {
        // Загрузка конфигурации при инициализации контроллера
        configService.loadConfig().then(function() {
            const contextPath = configService.getConfig().contextPath;

            $scope.loadModels = function() {
                $http.get(contextPath + '/models').then(function(response) {
                    $scope.ModelsList = response.data;
                });
            };

            $scope.openModel = function(model) {
                $window.location.href = `model-details.html?id=${model.id}`;
            };

            $scope.loadModels();
        }).catch(function(error) {
            console.error('Ошибка загрузки конфигурации:', error);
        });
    });

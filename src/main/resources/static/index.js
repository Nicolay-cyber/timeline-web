angular.module('timeline', []).controller('indexController', function ($scope, $http) {
    const contextPath = 'http://192.168.0.229:8189/timeline/api/v1'; // for office

    $scope.loadParameters = function () {
        $http.get(contextPath + '/parameters')
            .then(function (response) {
                $scope.ParametersList = response.data;
            });
    };
/*
    $scope.deleteStudent = function (studentId) {
        $http.get(contextPath + '/students/delete/' + studentId)
            .then(function (response) {
                $scope.loadStudents();
            });
    }

    $scope.changeScore = function (studentId, delta) {
        $http({
            url: contextPath + '/students/change_score',
            method: 'GET',
            params: {
                studentId: studentId,
                delta: delta
            }
        }).then(function (response) {
            $scope.loadStudents();
        });
    }*/

    $scope.loadParameters();
});
(function () {
    angular
        .module('tvGuideApp', ['ngResource', 'ngRoute'])
        .controller('tvGuideCtrl', tvGuideCtrl)
        .controller('tvGuideJumbotronCtrl', tvGuideJumbotronCtrl)
        .controller('tvGuideChannelsCtrl', tvGuideChannelsCtrl)
        .controller('tvChannelsGroupCtrl', tvChannelsGroupCtrl)
        .factory('tvGuideService', tvGuideService)
        .factory('tvGuideChannelsService', tvGuideChannelsService)
        .filter('tvGuideTime', tvGuideTime)
        .filter('channelsFilter', channelsFilter)
        .directive('tvJsonEdit', tvJsonEdit)
        .config(routeProvider)
//		.run(tune)
    ;

    // ====== <tvGuide> ======

    function tvGuideJumbotronCtrl() {
        var jc = this;
        this.currentDay = (new Date()).getDay();
        this.dayPart = 0;
        this.days = ["Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресение"];
        this.dayParts = ["Сейчас", "Утро", "День", "Вечер", "Ночь"];
        this.getDay = function () {
            return jc.days[jc.currentDay];
        };
        this.setDay = function (n) {
            jc.currentDay = n;
        };
        this.getDayPart = function () {
            return jc.dayParts[jc.dayPart];
        };
        this.setDayPart = function (n) {
            jc.dayPart = n;
        };
    }

    function tvGuideCtrl(tvGuideService) {
        var tvGuideCtrl = this;
        tvGuideCtrl.getModel = function () {
            return tvGuideService.model;
        };
    }

    function tvGuideService($resource, $http) {
        var tvGuideService = {};
        tvGuideService.model = {};

        tvGuideService.loadJson = function () {
            return $http.get("data/json?stop.ge.time=-2m&start.le.time=%2B2h")
                .then(function (response) {
                    tvGuideService.model = response.data;
                    tvGuideService.model.originGroups = angular.extend([], tvGuideService.model.groups);
                });
        };

        tvGuideService.saveGroups = function () {
            $http.put("data/groups", tvGuideService.model.groups)
                .error(function (err) {
                    console.error(err);
                });
        };

        tvGuideService.loadJson();
        return tvGuideService;
    }

    function tvGuideTime() {
        return function (t) {
            return t ? t.slice(8, 10) + ":" + t.slice(10, 12) : "";
        };
    }

    // ====== </tvGuide> ======

    // ====== <tvGuideChannels> ======

    function tvGuideChannelsCtrl(tvGuideService, tvGuideChannelsService) {
        tvGuideChannelsCtrl = this;
        tvGuideChannelsCtrl.getModel = function () {
            return tvGuideService.model;
        };
        tvGuideChannelsCtrl.setFilterGroup = function (value) {
            tvGuideChannelsCtrl.filterGroup = value;
        };

        tvGuideChannelsCtrl.getGroups = function () {
            return tvGuideChannelsService.model;
        };

        tvGuideChannelsCtrl.loadGroups = function () {
            tvGuideChannelsService.load();
        };

        tvGuideChannelsCtrl.countUnbinded = function (items) {
            var n = 0;
            angular.forEach(items, function (item) {
                if (!item.guideId) {
                    n++;
                }
            });
            return n;
        };

        tvGuideChannelsCtrl.countUnbinded = function (items) {
            var n = 0;
            angular.forEach(items, function (item) {
                if (!item.guideId) n++;
            });
            return n;
        };
    }

    function channelsFilter() {
        return function (arr, filterName, filterGroup, filterBinded) {
            var result = [];
            if (arr) {
                for (var i = 0; i < arr.length; i++) {
                    if ((!filterName || arr[i].name.toLowerCase().indexOf(filterName.toLowerCase()) >= 0) &&
                        (!filterGroup || arr[i].group.toLowerCase().indexOf(filterGroup.toLowerCase()) >= 0) &&
                        (!filterBinded || !arr[i].guideId)) {
                        result.push(arr[i]);
                    }
                }
            }
            return result;
        };
    }

    function tvGuideChannelsService($http) {
        var tvGuideChannelsService = {};

        tvGuideChannelsService.load = function () {
            return $http.get("data/rest/channels")
                .then(function (response) {
                    tvGuideChannelsService.model = response.data;
                });
        };

        return tvGuideChannelsService;
    }

    // ====== </tvGuideChannels> ======

    // ====== </tvChannelsGroup> ======
    function tvChannelsGroupCtrl(tvGuideService) {
        tvChannelsGroupCtrl = this;
        var wasModified = false;

        tvChannelsGroupCtrl.isModified = function () {
            return wasModified;
        };

        tvChannelsGroupCtrl.getModel = function () {
            return tvGuideService.model;
        };

        tvChannelsGroupCtrl.reorder = function (index) {
            var s = tvGuideService.model.groups[index + 1];
            tvGuideService.model.groups[index + 1] = tvGuideService.model.groups[index];
            tvGuideService.model.groups[index] = s;

            tvChannelsGroupCtrl.checkModified();
        };

        tvChannelsGroupCtrl.checkModified = function () {
            wasModified = !angular.equals(tvGuideService.model.groups, tvGuideService.model.originGroups);
        };

        tvChannelsGroupCtrl.save = function () {
            tvGuideService.saveGroups();
            wasModified = false;
        };

        tvChannelsGroupCtrl.revert = function () {
            tvGuideService.loadJson();
            wasModified = false;
        };
    }

    // ====== </tvChannelsGroup> ======

    function routeProvider($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'pages/guide.html'
            })
            .when('/channels', {
                templateUrl: 'pages/channels.html',
                controller: 'tvGuideChannelsCtrl',
                controllerAs: "gcc"
            })
            .when('/groups', {
                templateUrl: 'pages/groups.html',
                controller: 'tvChannelsGroupCtrl',
                controllerAs: "cg"
            });
    }

    function tune() {
    }

    function tvJsonEdit() {
        return {
            restrict: 'E',
            transclude: true,
            templateUrl: 'pages/jsonEdit.html',
            scope: {
                json: '=',
                onsetjson: '&',
                fileName: '@'
            },
            link: function ($scope, element) {
                $scope.tab = 0;
                $scope.initialStyle = {};
                $scope.firstChild = element.find('ng-transclude').children()[0];

            },
            controller: function ($scope, $parse, $timeout) {
                this.showData = function () {
                    $scope.json = JSON.parse($scope.data);
                    $scope.tab = 0;
                    $timeout($parse($scope.onsetjson)($scope));
                };
                this.showJson = function () {
                    $scope.initialStyle.height = ($scope.firstChild.offsetHeight - 33) + 'px';
                    $scope.data = JSON.stringify($scope.json, null, 4);
                    $scope.tab = 1;
                    $scope.downloadUrl = URL.createObjectURL(new Blob([$scope.data], {type: "application/json"}));
                };
            },
            controllerAs: 'tvJsonEditCtrl'
        };
    }

})();

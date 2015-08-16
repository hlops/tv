{
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
        .config(routeProvider)
        .run(tune)
    ;

    // ====== <tvGuide> ======

    function tvGuideJumbotronCtrl() {
        var jc = this;
        this.days = ["Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресение"];
        this.currentDay = 2;
        this.setDay = function (n) {
            jc.currentDay = n;
        }
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
        }
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

        tvGuideChannelsCtrl.loadGroups();

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
        }
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

        tvChannelsGroupCtrl.getModel = function () {
            return tvGuideService.model;
        };

        tvChannelsGroupCtrl.reorder = function (index) {
            var s = tvGuideService.model.groups[index + 1];
            tvGuideService.model.groups[index + 1] = tvGuideService.model.groups[index];
            tvGuideService.model.groups[index] = s;
        };

        tvChannelsGroupCtrl.save = function () {
            tvGuideService.saveGroups();
        };

        tvChannelsGroupCtrl.revert = function () {
            tvGuideService.loadJson();
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
            })
    }

    function tune() {
        //editableOptions.theme = 'bs3'
    }
}
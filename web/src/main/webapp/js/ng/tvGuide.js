{
    angular
        .module('tvGuideApp', ['ngResource', 'ngRoute', 'xeditable'])
        .controller('tvGuideCtrl', tvGuideCtrl)
        .controller('tvGuideJumbotronCtrl', tvGuideJumbotronCtrl)
        .controller('tvGuideChannelsCtrl', tvGuideChannelsCtrl)
        .controller('tvChannelsGroupCtrl', tvChannelsGroupCtrl)
        .factory('tvGuideService', tvGuideService)
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
            return $http.get("tv/json?stop.ge.time=-2m&start.le.time=%2B2h")
                .then(function (response) {
                    tvGuideService.model = response.data;
                });
        };

        tvGuideService.saveGroups = function () {
            $http.put("tv/groups", tvGuideService.model.groups)
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

    function tvGuideChannelsCtrl() {
    }

    function channelsFilter() {
        return function (arr, filterName, filterGroup, filterBinded) {
            var result = [];
            if (arr) {
                for (var i = 0; i < arr.length; i++) {
                    if ((!filterName && !filterGroup && !filterBinded) ||
                        (filterName && arr[i].name.toLowerCase().indexOf(filterName.toLowerCase()) >= 0) ||
                        (filterGroup && arr[i].group.toLowerCase().indexOf(filterGroup.toLowerCase()) >= 0) ||
                        (filterBinded && !arr[i].guideId)) {
                        result.push(arr[i]);
                    }
                }
            }
            return result;
        }
    }

    // ====== </tvGuideChannels> ======

    // ====== </tvChannelsGroupCtrl> ======
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

    // ====== </tvChannelsGroupCtrl> ======

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

    function tune(editableOptions) {
        editableOptions.theme = 'bs3'
    }
}
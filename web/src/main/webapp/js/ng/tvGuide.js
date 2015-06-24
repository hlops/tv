{
    angular
        .module('tvGuideApp', ['ngResource', 'ngRoute', 'xeditable'])
        .controller('tvGuideCtrl', tvGuideCtrl)
        .controller('tvGuideJumbotronCtrl', tvGuideJumbotronCtrl)
        .controller('tvGuideChannelsCtrl', tvGuideChannelsCtrl)
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
        tvGuideService.get(
            function (data) {
                tvGuideCtrl.tv = data;
            }
        );
    }

    function tvGuideService($resource) {
        return $resource("tv/json?group.eq=Эфир&stop.ge.time=-2m&start.le.time=%2B2h");
    }

    function tvGuideTime() {
        return function (t) {
            return t.slice(8, 10) + ":" + t.slice(10, 12);
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
                templateUrl: 'pages/groups.html'
            })
    }

    function tune(editableOptions) {
        editableOptions.theme = 'bs3'
    }
}
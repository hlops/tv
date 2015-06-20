{
    angular
        .module('tvGuideApp', ['ngResource', 'ngRoute'])
        .controller('tvGuideCtrl', tvGuideCtrl)
        .controller('tvGuideJumbotronCtrl', tvGuideJumbotronCtrl)
        .factory('tvGuideService', tvGuideService)
        .filter('tvGuideTime', tvGuideTime)
        .config(routeProvider)
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

    function routeProvider($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'pages/guide.html',
                controller: 'tvGuideCtrl',
                controllerAs: "gc"
            })
            .when('/channels', {
                templateUrl: 'pages/channels.html',
                controller: 'tvGuideCtrl',
                controllerAs: "gc"
            })

    }
}
{
    angular
        .module('tvGuideApp', ['ngResource'])
        .controller('tvGuideCtrl', tvGuideCtrl)
        .controller('tvGuideJumbotronCtrl', tvGuideJumbotronCtrl)
        .factory('tvGuideService', tvGuideService);


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
}
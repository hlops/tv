<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Телепрограмма">

<jsp:attribute name="head">
    <style>
        body {
            padding-top: 70px;
        }
    </style>
    <script src="js/ng/tvGuide.js"></script>
</jsp:attribute>

<jsp:attribute name="body">
    <nav class="navbar navbar-inverse navbar-fixed-top" ng-controller="tvGuideCtrl">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                        aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#">Телепрограмма</a>
            </div>
            <div id="navbar" class="navbar-collapse collapse">
                <ul class="nav navbar-nav">
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
                            Settings<span class="caret"></span></a>
                        <ul class="dropdown-menu" role="menu">
                            <li><a href="#">Channels</a></li>
                        </ul>
                    </li>
                </ul>
                <form class="navbar-form navbar-right">
                    <div class="form-group">
                        <input type="text" placeholder="Email" class="form-control">
                    </div>
                    <div class="form-group">
                        <input type="password" placeholder="Password" class="form-control">
                    </div>
                    <button type="submit" class="btn btn-success">Sign in</button>
                </form>
            </div>
        </div>
    </nav>

    <div class="container">

        <div class="row row-offcanvas row-offcanvas-right">
            <div class="col-xs-12 col-sm-12">
                <p class="pull-right visible-xs">
                    <button type="button" class="btn btn-primary btn-xs" data-toggle="offcanvas">Toggle nav</button>
                </p>
                <div class="jumbotron">
                    <ul class="nav nav-pills">
                        <li role="presentation"><a href="#">Понедельник</a></li>
                        <li role="presentation"><a href="#">Вторник</a></li>
                        <li role="presentation"><a href="#">Среда</a></li>
                        <li role="presentation"><a href="#">Четверг</a></li>
                        <li role="presentation"><a href="#">Пятница</a></li>
                        <li role="presentation" class="active"><a href="#">Суббота</a></li>
                        <li role="presentation"><a href="#">Воскресение</a></li>
                    </ul>

                    <ul class="nav nav-pills">
                        <li role="presentation" class="active"><a href="#">Сейчас</a></li>
                        <li role="presentation"><a href="#">Утро</a></li>
                        <li role="presentation"><a href="#">День</a></li>
                        <li role="presentation"><a href="#">Вечер</a></li>
                    </ul>

                    <ul class="nav nav-pills">
                        <li role="presentation" class="active"><a href="#">Все</a></li>
                        <li role="presentation"><a href="#">Эфир</a></li>
                        <li role="presentation"><a href="#">Спорт</a></li>
                        <li role="presentation"><a href="#">Хобби</a></li>
                        <li role="presentation"><a href="#">Новости</a></li>
                    </ul>

                    <div class="input-group">
                        <input type="text" class="form-control" placeholder="Поиск...">
                        <span class="input-group-btn">
                            <button class="btn btn-default" type="button">
                                <span class="glyphicon glyphicon-remove"></span>
                            </button>
                        </span>
                    </div>
                </div>
                <div id="container">
                    <div class="row">
                        <div class="col-xs-6 col-lg-2">
                            <h2>Heading</h2>

                            <p>Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus
                                commodo,
                                tortor mauris
                                condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada
                                magna
                                mollis
                                euismod. Donec sed odio dui. </p>

                            <p><a class="btn btn-default" href="#" role="button">View details &raquo;</a></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</jsp:attribute>

</t:layout>
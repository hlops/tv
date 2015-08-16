<%@tag description="Basic layout" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@attribute name="title" %>
<%@attribute name="head" fragment="true" %>
<%@attribute name="body" fragment="true" %>

<!DOCTYPE html>
<html lang="ru,en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${title}</title>

    <link href="${pageContext.request.contextPath}/css/readable-bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/xeditable.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

    <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.3.14/angular.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.3.14/angular-resource.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.3.14/angular-route.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.3.14/angular-animate.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery-2.1.3.min.js"></script>
    <jsp:invoke fragment="head"/>
</head>
<body ng-app="tvGuideApp">
<jsp:invoke fragment="body"/>
<script src="${pageContext.request.contextPath}/js/ui.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
</body>
</html>

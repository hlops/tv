<%@tag description="Basic layout" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<%@attribute name="title" %>
<%@attribute name="head" fragment="true" %>
<%@attribute name="body" fragment="true" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>${title}</title>
    <script src="/js/jquery-2.1.3.min.js"></script>
    <script src="/js/ui.js"></script>
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">

    <script src="/js/chosen.jquery.min.js"></script>
    <link href="/css/chosen.min.css" rel="stylesheet">

    <jsp:invoke fragment="head"/>
</head>
<body>
<jsp:invoke fragment="body"/>
</body>
</html>

<!DOCTYPE html>
<%@tag description="Simple Template" pageEncoding="UTF-8" %>

<%@attribute name="title" %>
<%@attribute name="head" fragment="true" %>
<%@attribute name="body" fragment="true" %>

<html>
<head>
    <meta charset="UTF-8"/>
    <title>${title}</title>
    <script src="/js/jquery-2.1.3.min.js"></script>
    <link href="/css/style.css" rel="stylesheet">

    <jsp:invoke fragment="head"/>
</head>
<body>
<jsp:invoke fragment="body"/>
</body>
</html>

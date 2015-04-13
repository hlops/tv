<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Телепрограмма">

<jsp:attribute name="head">
    <script src="js/xmltv.js"></script>
    <script>
        var xmltv;
        $(function () {
            xmltv = new XMLTV("tv/xmltv?group=Познавательный", $("#xmltvcontainer"));
        })
    </script>
    <style>
        body {
            padding: 0;
            margin: 0;
        }

        #xmltvcontainer {
        }

        span, div {
            #outline: 1px solid gray;
        }

        #xmltvcontainer .channel {
            white-space: nowrap;
            height: 25px;
        }

        #xmltvcontainer .channel .name {
            display: inline-block;
            width: 250px;
        }

        #xmltvcontainer .channel .name span.text {
            display: inline-block;
            margin-left: 30px;
        }

        #xmltvcontainer .channel .name img.logo {
            position: absolute;
            vertical-align: middle;
            max-width: 25px;
        }

        #xmltvcontainer .channel div.items {
            display: inline-block;
            white-space: nowrap;
            position: relative;
            overflow: hidden;
            width: calc(100vw - 270px);
            line-height: 25px;
            vertical-align: middle;
        }

        #xmltvcontainer .channel div.items .item {
            display: inline-block;
            border-bottom: 1px dotted gray;
            line-height: normal;
        }

        #xmltvcontainer .channel div.items .item[title=''] {
            border-bottom: none;
        }
    </style>
</jsp:attribute>

<jsp:attribute name="body">
    <div id="xmltvcontainer">

    </div>
</jsp:attribute>

</t:layout>
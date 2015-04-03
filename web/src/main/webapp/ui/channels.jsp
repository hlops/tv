<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Channels">

<jsp:attribute name="head">
    <script>
        $.get("/tv/rest/channels", function (data) {
            var arr = [];
            /*
             buildTable(
             [
             ["Name", "name", function(data) {
             return '<input type="checkbox" onchange="saveEnabled(this)">'
             }]
             ]
             )
             */
            if (data && data.length) {
                arr.push("<table border=1><tr>");
                arr.push("<th>Name</th>");
                arr.push("<th>Enabled</th>");
                arr.push("<th>tvg-name</th>");
                arr.push("<th>aspect</th>");
                arr.push("<th>crop</th>");
                arr.push("<th>group</th>");
                arr.push("</tr>");
                for (var i = 0; i < data.length; i++) {
                    arr.push("<tr>");
                    arr.push("<td>", data[i].name, "</td>");
                    arr.push("<td>", '<input type="checkbox" onchange="saveEnabled(this)">', "</td>");
                    arr.push("<td>", data[i].tvgName, "</td>");
                    arr.push("<td>", data[i].aspect, "</td>");
                    arr.push("<td>", data[i].crop, "</td>");
                    arr.push("<td>", data[i].group, "</td>");
                    arr.push("</tr>");
                }
                arr.push("</table>");
            }
            $("#channelsDiv").html(arr.join(""));
        });
    </script>
</jsp:attribute>

<jsp:attribute name="body">
    Channels:
    <div id="channelsDiv"></div>
</jsp:attribute>

</t:layout>
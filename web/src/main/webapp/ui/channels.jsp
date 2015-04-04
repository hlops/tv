<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Channels">

<jsp:attribute name="head">
    <script>
        $(function () {
            var table = new UITable(
                    "channelsTable",
                    $("#channelsDiv"),
                    [
                        ["Канал", "name"],
                        ["Активен", "enabled", "checkbox"],
                        ["Аспект", "aspect"],
                        ["Размер", "crop"],
                        ["Группа", "group"]
                    ]
            );
            table.getRenderer().on("change", ":checkbox", function (event) {
                var $el = $(event.target);
                if ($el.is(":checkbox")) {
                    $.ajax({
                        url: "/tv/rest/channel",
                        method: "PUT",
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify({
                            id: $el.parents("tr").attr("rowId"),
                            enabled: $el.is(":checked")
                        })
                    })
                }
            });
            $.get("/tv/rest/channels", function (data) {
                table.fill(data);
            });
        });
    </script>
</jsp:attribute>

<jsp:attribute name="body">
    <div id="channelsDiv" class="full"></div>
</jsp:attribute>

</t:layout>
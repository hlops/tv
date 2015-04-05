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
                        ["xmltv Id", "xmltv"],
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
    <style>
        #channelsDiv {
            border-collapse: collapse;
            padding: 10px;
        }

        #channelsDiv .ui-table {
            border-collapse: collapse;
            width: 100%;
        }

        #channelsDiv .ui-table thead {
            padding-right: 15px;
        }

        #channelsDiv .ui-table thead, #channelsDiv .ui-table tbody {
            display: block;
            border-collapse: separate;
        }

        #channelsDiv .ui-table tbody {
            overflow-y: scroll;
            height: calc(100vh - 80px);
        }

        #channelsDiv .ui-table tbody td {
            text-align: center;
        }

        #channelsDiv .ui-table tbody td.td-name {
            text-align: left;
        }

        #channelsDiv .ui-table thead tr.filter .th-enabled input,
        #channelsDiv .ui-table thead tr.filter .th-aspect input,
        #channelsDiv .ui-table thead tr.filter .th-crop input
        {
            display: none;
        }
    </style>
</jsp:attribute>

<jsp:attribute name="body">
    <div id="channelsDiv"></div>
</jsp:attribute>

</t:layout>
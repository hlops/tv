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
                        ["xmltv", "xmltv"],
                        ["Активен", "enabled", "checkbox"],
                        ["Аспект", "aspect"],
                        ["Размер", "crop"],
                        ["Группа", "group"],
                        ["Сдвиг", "shift"]
                    ]
            );
            table.getRenderer().on("change", ":checkbox", function (event) {
                var $el = $(event.target);
                if ($el.is(":checkbox")) {
                    saveChannel({
                        id: $el.parents("tr").attr("rowId"),
                        enabled: $el.is(":checked")
                    });
                }
            });
            var f1 = $.get("/tv/rest/channels", function (data) {
                table.fill(data);
            });
            var f2 = $.get("/tv/rest/xmltv-channels", function (data) {
                var $xmltvSelect = $('#xmltvSelect');
                $xmltvSelect.append(new Option());
                $.each(data, function (val, text) {
                    $xmltvSelect.append(new Option(text, val));
                });
                $xmltvSelect.chosen({
                    placeholder_text_single: "Выбор канала",
                    allow_single_deselect: true
                });
                $xmltvSelect.on("change", function () {
                    saveChannel({
                        id: $xmltvSelect.parents("tr:first").attr("rowId"),
                        xmltv: $xmltvSelect.val()
                    });
                });
                $("#hiddenDiv").hide();
            });
            $.when(f1, f2).then(function (d1, d2) {
                table.getRenderer().find("td.td-xmltv").each(function () {
                    $(this).attr("xmltv", $(this).text());
                    $(this).text(d2[0][$(this).attr("xmltv")]);
                });

                table.getRenderer().on("click", "td", function (event) {
                    $el = $(event.target);
                    if ($el.is("td.td-xmltv")) {
                        showHmltvSelect($el)
                    } else {
                        var $parent = $el.parents("td.td-xmltv");
                        if ($parent.length) {
                            showHmltvSelect($parent)
                        } else {
                            hideHmltvSelect();
                        }
                    }
                });
            });

            var $hiddenDiv = $("#hiddenDiv");
            var $xmltvSelect = $("#xmltvSelect");
            var $xmltvSelectDiv = $('#xmltvSelectDiv');

            function hideHmltvSelect() {
                var $td = $xmltvSelectDiv.parent();
                if ($td.is("td")) {
                    $hiddenDiv.append($xmltvSelectDiv);
                    var $op = $xmltvSelect.find("option:selected");
                    $td.text($op.text());
                    $td.attr("xmltv", $op.val());
                }
            }

            function showHmltvSelect($td) {
                if (!$td.is($xmltvSelectDiv.parent())) {
                    hideHmltvSelect();
                    $td.text("").append($xmltvSelectDiv);
                    $xmltvSelect.val($el.attr("xmltv")).trigger("chosen:updated");
                }
            }

            function saveChannel(data) {
                $.ajax({
                    url: "/tv/rest/channel",
                    method: "PUT",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(data)
                })
            }
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

        #channelsDiv .ui-table input {
            vertical-align: baseline;
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
            cursor: default;
        }

        #channelsDiv .ui-table tbody td.td-xmltv {
            min-width: 300px;
        }

        #channelsDiv .ui-table tbody td {
            cursor: pointer;
        }

        #channelsDiv .ui-table tbody tr:hover {
            background: powderblue;
        }

        #channelsDiv .ui-table thead tr.filter .th-enabled input,
        #channelsDiv .ui-table thead tr.filter .th-aspect input,
        #channelsDiv .ui-table thead tr.filter .th-crop input {
            display: none;
        }

    </style>
</jsp:attribute>

<jsp:attribute name="body">
    <div id="channelsDiv" class="container"></div>
    <div id="hiddenDiv">
        <div id="xmltvSelectDiv">
            <select id="xmltvSelect" class="chosen-select"></select>
        </div>
    </div>
</jsp:attribute>

</t:layout>
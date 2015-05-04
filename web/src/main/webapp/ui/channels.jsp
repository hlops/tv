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
                        ["Сдвиг", "timeShift"]
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
            });
            $.when(f1, f2).then(function (d1, d2) {
                table.getRenderer().find("td.td-timeShift").each(function () {
                    var $this = $(this);
                    $this.attr("timeShift", $this.text());
                });

                $timeShiftSelect.chosen({
                    disable_search_threshold: 20,
                    width: "70px"
                });

                $(".editor").on("change", function (event) {
                    var $editor = $(event.target);
                    var ob = {id: $editor.parents("tr:first").attr("rowId")};
                    ob[$editor.data("attr")] = $editor.val();
                    saveChannel(ob);
                });

                $hiddenDiv.hide();

                table.getRenderer().find("td.td-xmltv").each(function () {
                    $(this).attr("xmltv", $(this).text());
                    $(this).text(d2[0][$(this).attr("xmltv")]);
                });

                table.getRenderer().on("click", "td", function (event) {
                    $el = $(event.target);
                    if ($el.is("td.td-xmltv")) {
                        showSelectEditor($el, $xmltvSelect);
                    } else if ($el.is("td.td-timeShift")) {
                        showSelectEditor($el, $timeShiftSelect);
                    } else {
                        hideSelectEditors($el.parents("td:first"));
                    }
                });
            });

            var $hiddenDiv = $("#hiddenDiv");
            var $xmltvSelect = $("#xmltvSelect").data("attr", "xmltv");
            var $timeShiftSelect = $("#timeShiftSelect").data("attr", "timeShift");

            //$timeShiftSelect.chosen({});

            function hideSelectEditors($activeTd) {
                $(".editor").each(function () {
                    $editor = $(this);
                    $div = $editor.parent();
                    var $td = $div.parent();
                    if ($td.is("td") && !$td.is($activeTd)) {
                        $hiddenDiv.append($div);
                        var $op = $div.find("option:selected");
                        $td.text($op.text());
                        $td.attr($editor.data("attr"), $op.val());
                    }
                });
            }

            function showSelectEditor($td, $editor) {
                var $div = $editor.parent();
                if (!$td.is($div.parent())) {
                    hideSelectEditors($td);
                    $td.text("").append($div);
                    $editor.val($td.attr($editor.data("attr"))).trigger("chosen:updated");
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
    <div class="container">
        <div id="channelsDiv"></div>
        <div>
            <button>Save</button>
        </div>
    </div>
    <div id="hiddenDiv">
        <div>
            <select id="xmltvSelect" class="editor chosen-select"></select>
        </div>
        <div>
            <select id="timeShiftSelect" class="editor chosen-select">
                <option value="-6">-6</option>
                <option value="-5">-5</option>
                <option value="-4">-4</option>
                <option value="-3">-3</option>
                <option value="-2">-2</option>
                <option value="-1">-1</option>
                <option value="0">0</option>
                <option value="1">+1</option>
                <option value="2">+2</option>
                <option value="3">+3</option>
                <option value="4">+4</option>
                <option value="5">+5</option>
                <option value="6">+6</option>
            </select>
        </div>
    </div>
</jsp:attribute>

</t:layout>
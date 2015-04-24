function UITable(id, $renderer, header) {
    {
        var arr = ["<table class='ui-table' id='", id, "'><thead><tr>"];
        for (var i = 0; i < header.length; i++) {
            arr.push("<th class='th-", header[i][1], "'>", header[i][0], "</th>");
        }
        arr.push("<tr class='filter'>");
        for (i = 0; i < header.length; i++) {
            arr.push("<th class='th-", header[i][1], "'><input></th>");
        }
        arr.push("</tr>");
        arr.push("</tr></thead>");
        arr.push("<tbody>");
        arr.push("</tbody></table>");
        $renderer.html(arr.join(""));
        var $th = $renderer.find("thead>tr:first>th");
    }

    this.fill = function (data) {
        if (data && data.length) {
            var arr = [];
            var $th = $renderer.find("thead>tr:first>th");
            for (var i = 0; i < data.length; i++) {
                arr.push("<tr class='row' rowId='", data[i]["id"], "'>");
                $th.each(function (n) {
                    var name = header[n][1];
                    var type = header[n][2];

                    arr.push("<td class='td-", name, "'>");
                    var value = data[i][name];
                    if (type == "checkbox") {
                        arr.push("<input type='checkbox' class='form-control'");
                        if (value) {
                            arr.push("checked='checked'");
                        }
                        arr.push("'>");
                    } else {
                        arr.push(value);
                    }
                    arr.push("</td>");
                });
                arr.push("</tr>");
            }
            $renderer.find("tbody").html(arr.join(""));
        }
        filter();
        $renderer.find(".filter").on("keyup", "input", filter);
    };

    function filter(event) {
        applyFilter();
    }

    function applyFilter() {
        var $filter = $renderer.find("thead>tr.filter");
        var values = [];
        for (var i = 0; i < header.length; i++) {
            var $input = $filter.find("th:eq(" + i + ")>input:visible");
            if ($input.length && $input.val()) {
                values[i] = $input.val().toLowerCase();
            }
        }
        $renderer.find("tbody>tr").each(function () {
            var $tr = $(this);
            var isShown = true;
            for (var i = 0; i < values.length; i++) {
                if (values[i]) {
                    if ($tr.find("td:eq(" + i + ")").text().toLowerCase().indexOf(values[i]) < 0) {
                        isShown = false;
                        break;
                    }
                }
            }
            if (isShown) {
                $tr.show();
            } else {
                $tr.hide();
            }
        });
        alignColumns();
    }

    function alignColumns() {
        var $firstRow = $renderer.find("tbody>tr:visible:first");
        $renderer.find("thead>tr:first>th").each(function (n) {
            var $td = $firstRow.find("td:eq(" + n + ")");
            var $this = $(this);
            if ($this.outerWidth() < $td.outerWidth()) {
                $this.width($td.outerWidth());
            } else {
                $td.width($this.outerWidth());
            }
        })
    }

    this.getRenderer = function () {
        return $renderer;
    }
}


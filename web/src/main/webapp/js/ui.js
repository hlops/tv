function UITable(id, $renderer, header) {
    {
        var arr = ["<table id='", id, "' border=1><thead><tr>"];
        for (var i = 0; i < header.length; i++) {
            arr.push("<th style='", header[i][1], "'>", header[i][0], "</th>");
        }
        arr.push("<tr>");
        for (var i = 0; i < header.length; i++) {
            arr.push("<th><input></th>");
        }
        arr.push("</tr>");
        arr.push("</tr></thead>");
        arr.push("<tbody>");
        arr.push("</tbody></table>");
        $renderer.html(arr.join(""));
        var $th = $("#channelsTable").find("thead>tr:first>th");
        $th.each(function (n) {
            var $this = $(this);
            $this.data("name", header[n][1]);
            $this.data("type", header[n][2]);
        })
    }

    this.fill = function (data) {
        if (data && data.length) {
            var arr = [];
            var $th = $("#channelsTable").find("thead>tr:first>th");
            for (var i = 0; i < data.length; i++) {
                arr.push("<tr>");
                $th.each(function () {
                    arr.push("<td>");
                    var $this = $(this);
                    var name = $this.data("name");
                    var type = $this.data("type");
                    var value = data[i][name];
                    if (type == "checkbox") {
                        arr.push("<input type='checkbox' checked='", value, "'>");
                    } else {
                        arr.push(value);
                    }
                    arr.push("</td>");
                });
                arr.push("</tr>");
            }
            $renderer.find("tbody").html(arr.join(""));
        }
    }
}


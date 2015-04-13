XMLTV = function (url, $container) {

    var channels = {};
    var currentChannel, currentItem;

    var counter = 0;
    var currentTime1 = 20150413130000;
    var currentTime2 = 20150413173000;

    console.log("loading " + url + " ...")
    $.ajax({
            url: url,
            method: "GET",
            cache: true,
            datatype: "xml",
            success: function (data, textStatus, jqXHR) {
                console.log("loaded")
                parseNode(data);
                console.log("done");
                console.log(counter)

                var arr = [];
                for (var cid in channels) {
                    if (channels.hasOwnProperty(cid)) {
                        arr.push(printChannel(channels[cid]));
                    }
                }
                $container.html(arr.join(""));
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log([jqXHR, textStatus, errorThrown]);
            }
        }
    );

    function parseNode(node) {
        counter++;
        if (node.nodeName == 'channel') {
            addChannel(node);
        } else if (node.nodeName == 'display-name' && node.getAttribute("lang") == "ru") {
            currentChannel["name"] = node.childNodes[0].nodeValue;
        } else if (node.nodeName == 'icon') {
            currentChannel["icon"] = node.getAttribute("src");
        } else if (node.nodeName == 'programme') {
            if (parseInt(node.getAttribute("start")) >= (currentTime1) && parseInt(node.getAttribute("start")) <= (currentTime2) ||
                parseInt(node.getAttribute("stop")) >= (currentTime1) && parseInt(node.getAttribute("stop")) <= (currentTime2)) {
                channels[node.getAttribute("channel")].items.push(currentItem = {});
            } else {
                currentItem = null;
            }
        } else if (currentItem != null && (node.nodeName == 'title' || node.nodeName == 'desc') && node.getAttribute("lang") == "ru") {
            currentItem[node.nodeName] = node.childNodes[0].nodeValue;
        }

        if (node.hasChildNodes()) {
            for (var i = 0; i < node.childNodes.length; i++) {
                parseNode(node.childNodes[i]);
            }
        }
    }

    function addChannel(node) {
        channels[node.getAttribute("id")] = currentChannel = {
            id: node.getAttribute("id"),
            items: []
        };
    }

    function printChannel(channel) {
        var arr = [];
        if (channel.items.length) {
            arr.push("<div class='channel'>");
            arr.push("<div class='name'>");
            arr.push("<img class='logo' src='", channel.icon, "'>");
            arr.push("<span class='text'>", channel.name, "</span>");
            arr.push("</div>");
            arr.push("<div class='items'>");
            for (var i = 0; i < channel.items.length; i++) {
                arr.push("<span class='item' title='", channel.items[i].desc, "'>", channel.items[i].title, "</span>");
            }
            arr.push("</div></div>");
        }
        return arr.join("");
    }

};


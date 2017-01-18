
var ws;
var products;
var services;
var str = "";
var start = true;
var jsonData; /* = [{"Op":"Tree"},{"Id":1,"Name":"Morris Ground 1"},{"Id":2,"Name":"Austin Basket"},{"Id":3,"Name":"Austin Soccer"},{"Id":4,"Name":"Morris Sea 1000"},{"Id":5,"Name":"Morris Sea 2099"},{"Id":6,"Name":"Morris Wind"},{"Id":7,"Name":"Austin Polo"},{"Id":8,"Name":"Austin Cricket"},{"Id":9,"Name":"Austin XC"},{"Id":10,"Name":"Austin Base"},{"Products":[{"Products":[{"Products":[{"Products":[{"Id":21,"Name":"21"}],"Id":20,"Name":"20"}],"Id":19,"Name":"19"}],"Id":18,"Name":"18"}],"Id":11,"Name":"Sole Machine"},{"Id":12,"Name":"Sewing Machine"},{"Products":[{"Id":14,"Name":"Rubber"},{"Id":15,"Name":"Aluminium"}],"Id":13,"Name":"Cleat Applier"},{"Id":16,"Name":"Glueing Machine"},{"Id":17,"Name":"Neoprene Cutting Machine"}];
*/
document.addEventListener('DOMContentLoaded', function() {
  ws = new WebSocket('ws://' + window.location.hostname + ":"
    + window.location.port + '/Diversity/server');

  ws.onopen = function () {
    json = {
      "Op" : "gettree",
      "All" : 1
    }

    ws.send(JSON.stringify(json));
  }

  ws.onmessage = function(event) {
    var json = JSON.parse(event.data);

    if (json[0].Op == "Tree") {
      jsonData = JSON.parse(JSON.stringify(json));
      makeTree("prod_list",jsonData);
      $('#prod_list').jstree(true).refresh();
      str = "";
      start = true;
      makeTree("serv_list",jsonData);
      $('#serv_list').jstree(true).refresh();
      console.log(jsonData);
    }

    if (json[0].Op == "Error") {
      if (json[0].hasOwnProperty("Message")) {
        console.log(json[0].Message);
      }
    }
  }
});

/*
* Uses JSTree to make a tree view from an unordered HTML list
*/
function makeTree(div,test) {
  str = "";
  $("#" + div).jstree("destroy");
  document.getElementById(div).innerHTML = str;
  str += "<ul>";
  //console.log(test);
  for (var i = 1; i < test.length; i++) {
    makeList(test[i]);//makeList(jsonData[1]);
  }
  str += "</ul>"
  document.getElementById(div).innerHTML = str;

  $("#" + div).jstree({
  "plugins" : [ "checkbox" ]
  });

  $("#prod_list").on("changed.jstree", function (e, data) {
    products = "";
    var i, j, r = [];
    for (i = 0, j = data.selected.length; i < j; i++) {
        r.push(data.instance.get_node(data.selected[i]).text);
        products += getObjects(jsonData,'Name', data.instance.get_node(data.selected[i]).text)[0].Id + ";" ;
    }
  });

  $("#serv_list").on("changed.jstree", function (e, data) {
    services = "";
    var i, j, r = [];
    for (i = 0, j = data.selected.length; i < j; i++) {
        r.push(data.instance.get_node(data.selected[i]).text);
        services += getObjects(jsonData,'Name', data.instance.get_node(data.selected[i]).text)[0].Id + ";" ;
    }
  });

  $("#prod_list").on('ready.jstree', function() {
    $('#prod_list').jstree('open_all');
  });

  $("#serv_list").on('ready.jstree', function() {
    $('#serv_list').jstree('open_all');
  });
}

function getObjects(obj, key, val) {
    var objects = [];
    for (var i in obj) {
        if (!obj.hasOwnProperty(i)) continue;
        if (typeof obj[i] == 'object') {
            objects = objects.concat(getObjects(obj[i], key, val));
        } else if (i == key && obj[key] == val) {
            objects.push(obj);
        }
    }
    return objects;
}

/*
* Makes a HTML unordered list from a json array (uses recursion, requires a var start = true)
*/
function makeList(array) {
  if (typeof array == 'undefined') {
    return;
  }

  if (!array.hasOwnProperty('Products') /*&& !start*/) {
    str += "<li>" + array.Name + "</li>";
    return null;
  }

  if (array.hasOwnProperty('Products')/* && start */) {
    start = false;
    if (array.hasOwnProperty('Name')) {
      str += "<li>"+ array.Name;
      str += "<ul>";
    }

    for (var i = 0; i < array.Products.length; i++) {
      makeList(array.Products[i]);
    }

    if (array.hasOwnProperty('Name')) {
      str += "</ul>";
      str += "</li>";
    }
  }
}

function submit() {
  var json = {
    "Op" : "Prediction",
    "Products" : products,
    "Services" : services
  }

  ws.send(JSON.stringify(json));
  window.location.href = "prediction.html"
}

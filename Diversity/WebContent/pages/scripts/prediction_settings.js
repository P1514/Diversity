var testProducts = [
   {
      "Op":"Tree"
   },
   {
      "PSS":"PSS D522-1",
      "Products":[
         {
            "Products":[
               {
                  "Name":"Red Brush"
               },
               {
                  "Name":"Blue Brush"
               },
               {
                  "Name":"Super-Glue +9000"
               },
               {
                  "Name":"Cheap-Glue 100"
               }
            ],
            "Name":"Paiting Machine"
         },
         {
            "Products":[
               {
                  "Name":"Red Brush"
               },
               {
                  "Name":"Blue Brush"
               },
               {
                  "Name":"Super-Glue +9000"
               },
               {
                  "Name":"Cheap-Glue 100"
               }
            ],
            "Name":"Glueing Machine"
         }
      ]
   }
];

var products;
var services;
var str = "";
var start = true;
$(document).ready(function () {
  makeTree("prod_list",testProducts);
  $('#prod_list').jstree(true).refresh();
  str = "";
  start = true;
  makeTree("serv_list",testProducts);
  $('#serv_list').jstree(true).refresh();
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
  for (var i = 0; i < test.length; i++) {
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
        products += data.instance.get_node(data.selected[i]).text + ";" ;
    }
    console.log(products);

  });

  $("#serv_list").on("changed.jstree", function (e, data) {
    services = "";
    var i, j, r = [];
    for (i = 0, j = data.selected.length; i < j; i++) {
        r.push(data.instance.get_node(data.selected[i]).text);
        services += data.instance.get_node(data.selected[i]).text + ";" ;
    }
    console.log(services);

  });

  $("#prod_list").on('ready.jstree', function() {
    $('#prod_list').jstree('open_all');
  });

  $("#serv_list").on('ready.jstree', function() {
    $('#serv_list').jstree('open_all');
  });

}



/*
* Makes a HTML unordered list from a json array (uses recursion, requires a var start = true)
*/
function makeList(array) {
  if (typeof array == 'undefined') {
    return;
  }

  if (!array.hasOwnProperty('Products') && !start) {
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

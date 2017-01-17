var str = "";
/*
* Uses JSTree to make a tree view from an unordered HTML list
*/
function makeTree() {
  str = "";
  $('#prod_list').jstree("destroy");
  document.getElementById('prod_list').innerHTML = str;
  str += "<ul>";
  var test = jsonData;
  //console.log(test);
  for (var i = 0; i < test.length; i++) {
    if (test[i].PSS == document.getElementById("pss").value) {
      makeList(test[i]);//makeList(jsonData[1]);
      break;
    }
  }
  str += "</ul>"
  document.getElementById('prod_list').innerHTML = str;

  $('#prod_list').jstree({
  "plugins" : [ "checkbox" ]
  });

  $('#prod_list').on("changed.jstree", function (e, data) {
    final_products = "";
    var i, j, r = [];
    for (i = 0, j = data.selected.length; i < j; i++) {
        r.push(data.instance.get_node(data.selected[i]).text);
        final_products += data.instance.get_node(data.selected[i]).text + ";" ;
    }
    //console.log(final_products);
  });

  $('#prod_list').jstree(true).refresh();
}

function showTree() {
  var checkbox = document.getElementById('final');
  if (!checkbox.checked) {
    document.getElementById('prod_list').style.display = 'none';
    document.getElementById('tree_div').style.display = 'none';
    $('#prod_list').jstree(true).deselect_all();
    final_products = "";
  } else {
    document.getElementById('tree_div').style.display = 'block';
    document.getElementById('prod_list').style.display = 'block';
  }
}

var start = true;

/*
* Makes a HTML unordered list from a json array (uses recursion)
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

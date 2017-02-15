var json = "";
var edit = false;
var i = 0;
var id=0;
var jsonData;
var final_products = "";

var datefield=document.createElement("input")
datefield.setAttribute("type", "date")
if (datefield.type!="date"){ //if browser doesn't support input type="date", load files for jQuery UI Date Picker
    document.write('<link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css" />\n')
    document.write('<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js"><\/script>\n')
    document.write('<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"><\/script>\n')
}

if (datefield.type!="date"){ //if browser doesn't support input type="date", initialize date picker widget:
    jQuery(function($){ //on document.ready
        $('#date_input').datepicker();
    })
}


function getCookie(name) { //not being used
  var value = "; " + document.cookie;
  var parts = value.split("; " + name + "=");
  if (parts.length == 2)
    return parts.pop().split(";").shift();
}

$(document).ready(function () {
  $('#ex1').slider({
    formatter: function(value) {
      return value;
    }
  });
});
/*
* Checks if the URL contains a pss=XXX tag; XXX is the desired PSS name and can contain spaces
*/
function getPss() {
  var url = window.location.href.toString();
  var pss = "";
  if (url.indexOf("pss=") != -1) {
    pss = url.split("pss=")[1].split("&")[0];
  }

  return pss.replace(/%20/g," ");
}

var ws;
document.addEventListener('DOMContentLoaded', function() {
  document.getElementById("page_title").innerHTML = "<h1>Create Opinion Model</h1>"
  ws = new WebSocket('ws://' + window.location.hostname + ":"
    + window.location.port + '/Diversity/server');
	$('#tree_div').hide();
  ws.onopen = function() {
    json = {
      "Op" : "getpss",
      'Key' : sessionStorage.userKey
    }

    ws.send(JSON.stringify(json));
  }

  ws.onmessage = function(event) {
    //console.log(event.data);
    var json = JSON.parse(event.data);
    if(json[0].Op=="Model"){
      var json2 = JSON.parse(event.data);}
    //console.log(json);
    if (json[0].Op == "Error") {
      if(json[0].hasOwnProperty('id')){
		  var id = json[0].id;
		  var code = json[0].Message +  '<br> Do you want to create another model? <br><br><button class="btn btn-default" id="yes" onclick="location.href =\'models.html\'">Yes</button> <button class="btn btn-default" id="no" onclick="sessionStorage.Id=\'model=\'+id;location.href =\'index.html\';">No</button>';
		  $('#alert').html(code);
		  $('#overlay').show();
		  $('#overlay-back').show();
		 /* $('alert').text()
        alert(json[0].Message);
        if (confirm("Do you want to create another model?")) {
          location.href = "models.html"
        } else {
          sessionStorage.Id="model="+json[0].id;
          location.href ='index.html';
        }*/
      } else {
		  var code = json[0].Message + '<br><br><button class="btn btn-default" id="ok" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide()">OK</button>';
		  $('#alert').html(code);
		  $('#overlay').show();
		  $('#overlay-back').show();
	 }
    return;
    }
    if (json[0].Op == "pss") {
      jsonData = JSON.parse(JSON.stringify(json));
      populatePSS();
      if (getPss() != "") {
        sessionStorage.internal = false;
        var jsonData2 = {
          "Op" : "gettree",
          "Pss" : getPss()
        }
      } else {
        var jsonData2 = {
          "Op" : "gettree",
          'Key' : sessionStorage.userKey
        }
      }

      ws.send(JSON.stringify(jsonData2));

    }

    if (json[0].Op == "Tree") {
      jsonData = JSON.parse(JSON.stringify(json))
      //console.log(jsonData);
      makeTree();
      if (sessionStorage.id != null) {
        var json2 = {
          "Op" : "get_model",
          "Id" : sessionStorage.id,
          'Key' : sessionStorage.userKey
        }
        //console.log("YOYOYOYOLO -> " +JSON.stringify(json2));
        ws.send(JSON.stringify(json2));
        return;
        }
    }

    if (sessionStorage.internal == "false") {
      var box = document.getElementById('pss');
      var found = false;

      for (var i = 0; i < box.length; i++) {
        if (box[i].text == getPss()) {
          found = true;
          break;
        }
      }

      if (found) {
        document.getElementById('pss').value = getPss();
        document.getElementById('pss').text = getPss();
        document.getElementById('pss').disabled = true;
      } else {
		  var code = json[0].Message + '<br><br><button class="btn btn-default" id="ok" onclick="window.history.back();">OK</button>';
		  $('#alert').html(code);
		  $('#overlay').show();
        //alert("Selected PSS does not exist.");
      }
    }

    if(json2 != null && json2[0].Op=="Model"){
      //console.log(json2[0]);
      document.getElementById("page_title").innerHTML = "<h1>Edit Opinion Model</h1>"
      document.getElementById("model_name").value=json2[0].Name;
      document.getElementById("model_name").readOnly = true;
      document.getElementById("frequency").value=json2[0].Update;
      document.getElementById('pss').value=json2[0].PSS;
      document.getElementById('pss').text=json2[0].PSS;
      document.getElementById("pss").disabled = true;

      makeTree();
	  if (json2[0].hasOwnProperty('Final_products')) {
		  var prods = json2[0].Final_products.split(";");
			  if(prods.length > 0) {
			  $('#final').click();
		  }
		  $("#final_input").bind('ready.jstree', function(event, data) {
			var $tree = $(this);
			$($tree.jstree().get_json($tree, {
			  flat: true
			}))
			.each(function(index, value) {
			  var node = $("#final_input").jstree().get_node(this.id);
				$("#final_input").jstree().disable_node(node);
			  if (prods.indexOf(node.text) != -1) {
				$("#final_input").jstree().select_node(node);
			  }
			  });
		  });
	  } else {
		$('#final').hide();
		$('#check_label').hide();
		$('#tree_div').hide();
	  }

      //document.getElementById("gender").value=json2[0].Gender;

      document.getElementById("final").checked=json2[0].Final_products;
      //console.log(json2[0].Archive);
      //console.log(json2[0].Final_products);
      //document.getElementById("Archive").checked=json2[0].Archive;
      //var ager=json2[0].Age.split(",").map(Number).filter(Boolean);// Converts string from DB to array of ints
      //$('#age').slider('setValue', ager);
      var uris = json2[0].URI.split(";");
      for(i=0; i<uris.length; i++){
        var source = uris[i].split(",")[0];
        var user = uris[i].split(",")[1];
        addline2(source,user);
        //console.log("HELLO "+uris[i]+"\r\n");
        $(':checkbox[value="'+uris[i]+'"]').prop("checked","true");
        $(':checkbox[value="'+uris[i]+'"]').prop("disabled","true");
      }

    //document.getElementById("fcheckbox").style.display = "block";
    //document.getElementById("Archive").disabled = false;
    document.getElementById("submit").value = "Update";
    edit = true;
  }
  if(json2 != null && json2[0].Op=="Model" && sessionStorage.getItem('view') == 'true'){
    document.getElementById("page_title").innerHTML = "<h1>View Opinion Model</h1>"
    document.getElementById("model_name").value=json2[0].Name;
    document.getElementById("model_name").readOnly = true;
    document.getElementById("frequency").value=json2[0].Update;
    document.getElementById("frequency").readOnly = true;
    document.getElementById('pss').value=json2[0].PSS;
    document.getElementById('pss').text=json2[0].PSS;
    document.getElementById("pss").disabled = true;
    document.getElementById("final").checked=json2[0].Final_products;
    document.getElementById("final").disabled = true;
    $('#ex1').slider('disable')
    var uris = json2[0].URI.split(";");
    for(i=0; i<uris.length; i++){
      //console.log("HELLO "+uris[i]+"\r\n");
      $(':checkbox[value="'+uris[i]+'"]').prop("checked","true");
      $(':checkbox[value="'+uris[i]+'"]').prop("disabled","true");
    }
    $("#submit").hide();
    document.getElementById("start_date").disabled = true;
    document.getElementById("new_name").disabled = true;
    document.getElementById("new_URI").disabled = true;

    makeTree();
	if (json2[0].hasOwnProperty('Final_products')) {
		var prods = json2[0].Final_products.split(";");
		$("#final_input").bind('ready.jstree', function(event, data) {
		  var $tree = $(this);
		  $($tree.jstree().get_json($tree, {
			  flat: true
			}))
			.each(function(index, value) {
			  var node = $("#final_input").jstree().get_node(this.id);
			  $("#final_input").jstree().disable_node(node);
			  if (prods.indexOf(node.text) != -1) {
				$("#final_input").jstree().select_node(node);
			  }
			});
		});
	}
  }
  }

}, false);


function populatePSS() {
  var products = JSON.parse(JSON.stringify(jsonData));
  for (var i = 1; i < products.length; i++) {
  //console.log(products[i].Pss);
  $('#pss').append('<option value="'+products[i].Pss+'">'+products[i].Pss+'</option>');
  }

}

function escapeHtml(html)//stops the user from injecting html in forms
{
    var text = document.createTextNode(html);
    var div = document.createElement('div');
    div.appendChild(text);
    return div.innerHTML;
}

function addline() {
  var name = escapeHtml(document.getElementById("new_name").value);
  var value = escapeHtml(document.getElementById("new_URI").value);
  //console.log("line added");
  if (name == "" || value == "")
    return;
  $('#table_div2').append(
      '<div class="checkbox"><label><input type="checkbox" value="'+name+","+value+'"">'
          + name + " / " + value +  '</label></div>');
  document.getElementById("new_name").value = "";
  document.getElementById("new_URI").value = "";
}

function addline2(name, value) {
  //console.log("line added");
  if (name == "" || value == "")
    return;
  $('#table_div2').append(
      '<div class="checkbox"><label name="user"><input type="checkbox" value="'+name+","+value+'"">'
          + name + " / " + value +  '</label></div>');
  document.getElementById("new_name").value = "";
  document.getElementById("new_URI").value = "";
}

function showdate() {
  var checkbox = document.getElementById("start_date");
  var data = new Date(document.getElementById("date_input").value);
  //console.log(data.toString());
  //console.log(data.getDate());
  //console.log(data.getMonth()+1);
  //console.log(data.getFullYear());
  // if(data.toString()=="Invalid Date")
      //      console.log("NaN");
//console.log("data:" + transformdate(data));
    if(!checkbox.checked)
      document.getElementById("date_input").style.display = 'none';//hide
    else
      document.getElementById("date_input").style.display = 'block';//show as block
}

function transformdate(date){
  var month = date.getMonth()+1;
  return date.getDate() + ";" + month + ";" + date.getFullYear();
}

$

var str = "";
/*
* Uses JSTree to make a tree view from an unordered HTML list
*/
function makeTree() {
  str = "";
  $('#final_input').jstree("destroy");
  document.getElementById('final_input').innerHTML = str;
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
  document.getElementById('final_input').innerHTML = str;

  $('#final_input').jstree({
  "plugins" : [ "checkbox" ]
  });

  $('#final_input').on("changed.jstree", function (e, data) {
    final_products = "";
    var i, j, r = [];
    for (i = 0, j = data.selected.length; i < j; i++) {
        r.push(data.instance.get_node(data.selected[i]).text);
        final_products += data.instance.get_node(data.selected[i]).text + ";" ;
    }
    //console.log(final_products);
  });

  $('#final_input').jstree(true).refresh();
}

function showTree() {
  var checkbox = document.getElementById('final');
  if (!checkbox.checked) {
    document.getElementById('final_input').style.display = 'none';
    document.getElementById('tree_div').style.display = 'none';
    $('#final_input').jstree(true).deselect_all();
    final_products = "";
  } else {
    document.getElementById('tree_div').style.display = 'block';
    document.getElementById('final_input').style.display = 'block';
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


function send_config() {
  var configs = ""; //to make sure it has the checkbox active
  $('#table_div2').find('input[type=checkbox]').each(function() {
    if (this.checked == true) {
      configs += this.value + ";";
    }
  });
  var erro = false;

  var date = new Date(document.getElementById("date_input").value);
      //console.log("TESTE***********"+date);
      if(date.toString() == "Invalid Date" && document.getElementById('start_date').checked){//checks for invalid date
        var code = 'Invalid date. <br><br><button class="btn btn-default" id="ok" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide();">OK</button>';
		  $('#alert').html(code);
		  $('#overlay').show();
		  $('#overlay-back').show();
    return;
    }
  var jsonData = {
    "Op" : document.getElementById("submit").value.toLowerCase()+"_model",//create or update
    "URI" : configs != "" ? configs : erro = true,
    "Update" : document.getElementById('frequency').value != "" ? document
        .getElementById('frequency').value
        : erro = true,
    "PSS" : document.getElementById('pss').value != "" ? document
        .getElementById('pss').value : erro = true,
    /*"Age" : document.getElementById('age').value != "" ? document
        .getElementById('age').value : erro = true,
    "Gender" : document.getElementById('gender').value != "" ? document
        .getElementById('gender').value : erro = true,*/
    "Final_Products" : final_products,//document.getElementById('final').checked,
    "Products" : final_products,
    "Archive" : false,
    "Name" : document.getElementById('model_name').value != "" ? document.getElementById('model_name').value : erro = true,
    "User" : 1,//TODO find this field
    "Id":sessionStorage.id,
    "Start_date": document.getElementById('start_date').checked ? document.getElementById('date_input').value :undefined,
    'Key' : sessionStorage.userKey

  };
  if (erro == true) {
      var code = 'All fields must be filled. <br><br><button class="btn btn-default" id="ok" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide();">OK</button>';
		  $('#alert').html(code);
		  $('#overlay').show();
		  $('#overlay-back').show();
    //alert("All Fields must be filled");
    return;
  }

  ws.send(JSON.stringify(jsonData));

}
function checknumber(field) {
  field.value = field.value.replace(/[^0-9]/g, '');
  if (field.value > 99) {
    field.value = '99';
  } else if (field.value < 0) {
    field.value = '0';
  }
}

var minSliderValue = $("#ex1").data("slider-min");
var maxSliderValue = $("#ex1").data("slider-max");

$('#ex1').slider({
    value : 1,
  formatter: function(value) {
    return value;
  }
});

// change number box when sliding
$('#ex1').on('slideStop', function(slider){
  $("#frequency").val(slider.value);
});

$('#ex1').on('slide', function(slider){
  $("#frequency").val(slider.value);
});

// change slider after typing into number box
$("#frequency").bind("keyup", function() {
    var val = Math.abs(parseInt(this.value, 10) || minSliderValue);
    this.value = val > maxSliderValue ? maxSliderValue : val;
    $('#ex1').slider('setValue', val);
});

// change slider after using the arrows inside the number box
$("#frequency").on("change", function() {
    var val = Math.abs(parseInt(this.value, 10) || minSliderValue);
    this.value = val > maxSliderValue ? maxSliderValue : val;
    $('#ex1').slider('setValue', val);
});

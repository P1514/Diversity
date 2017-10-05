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

$(window).on('load', function() {
  $('#ex1').slider({
    formatter: function(value) {
      return value;
    }
  });
});

document.addEventListener('DOMContentLoaded', function() {
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

function goToByScroll(id){
      // Remove "link" from the ID
    id = id.replace("link", "");
      // Scroll
    $('html,body').animate({
        scrollTop: $("#"+id).offset().top - 100},
        'slow');
}

var ws;
document.addEventListener('DOMContentLoaded', function() {
  document.getElementById("page_title").innerHTML = "<h1>Create Opinion Model</h1>"
  if (window.location.href.indexOf('https://') != -1) {
		ws = new WebSocket('wss://' + window.location.hostname + ":"
				+ window.location.port + '/Diversity/server');
	} else {
		ws = new WebSocket('ws://' + window.location.hostname + ":"
				+ window.location.port + '/Diversity/server');
	}
	$('#tree_div').hide();

  ws.onopen = function() {

    json = {
      "Op" : "getpss",
      'Key' : getCookie("JSESSIONID")
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
          'Key' : getCookie("JSESSIONID")
        }
      }

      ws.send(JSON.stringify(jsonData2));

    }

    if (json[0].Op == "Tree") {
      jsonData = JSON.parse(JSON.stringify(json));
      makeTree();
      $('#final_input').jstree(true).refresh();
      if (sessionStorage.id != null) {
        var json2 = {
          "Op" : "get_model",
          "Id" : sessionStorage.id,
          'Key' : getCookie("JSESSIONID")
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

    document.getElementById("mediawikibox").checked = json2[0].hasOwnProperty('mediawiki') ? json2[0].mediawiki : false;
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

  if (localStorage.tutorial == undefined) {
    localStorage.tutorial += "";
    request_tutorial();
  }

  }


}, false);

function request_tutorial() {
  if (localStorage.tutorial != undefined) {
    if (($('#page_title').text().indexOf('Create') != -1 && localStorage.tutorial.indexOf("create=done") == -1)
              || ($('#page_title').text().indexOf('View') != -1 && localStorage.tutorial.indexOf("view=done") == -1)
              || ($('#page_title').text().indexOf('Edit') != -1 && localStorage.tutorial.indexOf("edit=done") == -1)) {
      $('#alert').html("Would you like to see a tutorial for this page?" + '<br><br><button class="btn btn-default" id="yes" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide();start_tutorial();">Yes</button><button class="btn btn-default" id="no" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide();">No</button>');
      $('#overlay').show();
      $('#overlay-back').show();
    }
  }
}

function start_tutorial() {
  if ($('#page_title').text().indexOf('Create') != -1) {
    define_tutorial();
    if (localStorage.tutorial.indexOf("create=done") == -1) {
      localStorage.tutorial += "create=done;";
    }
  } else if ($('#page_title').text().indexOf('Edit') != -1) {
    edit_tutorial();
    if (localStorage.tutorial.indexOf("edit=done") == -1) {
      localStorage.tutorial += "edit=done;";
    }
  } else if ($('#page_title').text().indexOf('View') != -1) {
    view_tutorial();
    if (localStorage.tutorial.indexOf("view=done") == -1) {
      localStorage.tutorial += "view=done;";
    }
  }
  $('#tutorial_box').toggle();

  goToByScroll('tutorial_box');

}

function define_tutorial() {
  pss_tutorial();
}

function edit_tutorial() {
  var pos=$('#define').offset();
  var h=$('#define').height() + 10;
  var w=$('#define').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h });
  $('#tutorial').html('When editing a model, the Define section is blocked, which means that the model name, PSS and final products cannot be changed.<br><br><center><button class="btn btn-default" id="next" style="margin-left:5px;" onclick="sources_tutorial();">Next</button></center>');

  goToByScroll('tutorial_box');

}

function view_tutorial() {
  var pos=$('#define').offset();
  var h=$('#define').height() + 10;
  var w=$('#define').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h });
  $('#tutorial').html('When viewing a model, you have access to all the settings used to create it, but you can\'t make any changes to those settings.<br><br><center><button class="btn btn-default" id="next" style="margin-left:5px;" onclick="end_tutorial();">Next</button></center>');

  goToByScroll('tutorial_box');

}

function pss_tutorial() {
  var pos=$('#pss').offset();
  var h=$('#pss').height() + 10;
  var w=$('#pss').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h });
  $('#tutorial').html('This dropdown lets you select the PSS for the new model.<br><br><center><button class="btn btn-default" id="next" style="margin-left:5px;" onclick="name_tutorial();">Next</button></center>');

  goToByScroll('tutorial_box');

}

function name_tutorial() {
  var pos=$('#model_name').offset();
  var h=$('#model_name').height() + 10;
  var w=$('#model_name').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h });
  $('#tutorial').html('In this text box you can define a name for the new model.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="pss_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="final_product_tutorial();">Next</button></center>');

  goToByScroll('tutorial_box');

}

function final_product_tutorial() {
  var pos=$('#final').offset();
  var h=$('#final').height() + 10;
  var w=$('#final').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h});
  $('#tutorial').html('You can toggle this checkbox to specify if you want to include final products in your model. If toggled, you can select one or more final products to be included.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="name_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="sources_tutorial();">Next</button></center>');

  goToByScroll('tutorial_box');

}

function sources_tutorial() {
  source_tutorial();
}

function source_tutorial() {
  var pos=$('#new_name').offset();
  var h=$('#new_name').height() + 10;
  var w=$('#new_name').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h});
  if ($('#page_title').text().indexOf('Edit') != -1) {
    $('#tutorial').html('In this section you can select the user accounts to be used in the model. In the dropdown box you can define the source of the account. If you know the user name and social network, you can select Facebook or Twitter. Otherwise, you can select the URL option and the system will extract the user and social network from the provided link.<br><br><center><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="account_tutorial();">Next</button></center>');
  } else {
    $('#tutorial').html('In this section you can select the user accounts to be used in the model. In the dropdown box you can define the source of the account. If you know the user name and social network, you can select Facebook or Twitter. Otherwise, you can select the URL option and the system will extract the user and social network from the provided link.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="final_product_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="account_tutorial();">Next</button></center>');
  }


  goToByScroll('tutorial_box');

}

function account_tutorial() {
  var pos=$('#new_URI').offset();
  var h=$('#new_URI').height() + 10;
  var w=$('#new_URI').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h});
  $('#tutorial').html('Here you can insert the name of the user you intend to add. If you selected the URL option, simply type the URL of the user\'s Facebook or Twitter page. After you\'ve filled both fields, press the "+" button to add it to the list. You can then add more users or move on to the next section.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="source_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="media_wiki_tutorial();">Next</button></center>');

  goToByScroll('tutorial_box');

}

function media_wiki_tutorial() {
  var pos=$('#mediawikibox').offset();
  var h=$('#mediawikibox').height() + 10;
  var w=$('#mediawikibox').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h});
  $('#tutorial').html('The Media Wiki box lets you choose whether to use data from the Media Wiki or not.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="account_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="frequency_tutorial();">Next</button></center>');

  goToByScroll('tutorial_box');

}

function frequency_tutorial() {
  var pos=$('#slider').offset();
  var h=$('#slider').height() + 10;
  var w=$('#slider').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h});
  $('#tutorial').html('This slider lets you define the update frequency of the new model. In other words, it defines the number of days (ranging from 1 to 30) that occur until the model displays new data. You can set this value by adjusting the slider or by typing into the text box.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="media_wiki_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="start_date_tutorial();">Next</button></center>');

  goToByScroll('tutorial_box');

}

function start_date_tutorial() {
  var pos=$('#date').offset();
  var h=$('#date').height() + 10;
  var w=$('#date').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h});
  $('#tutorial').html('This checkbox lets you choose the start date of the new model. This date determines the point from which the model will start gathering data. If left unchecked, the data monitoring will begin immediately.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="frequency_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="end_tutorial();">Next</button></center>');

  goToByScroll('tutorial_box');

}

function end_tutorial() {
  var pos=$('#date').offset();
  var h=$('#date').height();
  var w=$('#date').width();

  $('#tutorial').html('You\'ve reached the end of the tutorial. You can access it at any time by clicking the <i class="fa fa-question-circle" aria-hidden="true"></i> button at the top right corner of the page.<br><br><center><button class="btn btn-default" style="margin-left:5px;" id="end" onclick="$(\'#tutorial_box\').toggle();">Finish</button></center>');

  goToByScroll('tutorial_box');

}


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

  if (name == "" || value == "")
    return;

  if (name == "URL") {

    if (value.toLowerCase().indexOf("facebook") != -1 || value.toLowerCase().indexOf("fb.com") != -1) {
      name = "Facebook";
      var tmp = value.split("/");
      value = tmp[tmp.length - 1];
    }

    if (value.toLowerCase().indexOf("twitter") != -1) {
      name = "Twitter";
      var tmp = value.split("/");
      value = tmp[tmp.length - 1];
    }

    if (value.toLowerCase().indexOf("amazon") != -1) {
      name = "Amazon"
      var tmp = value.split("/dp/");
      value = tmp[tmp.length - 1];
    }
  }

  $('#table_div2').append(
      '<div class="checkbox"><label><input type="checkbox" value="'+name+","+value+'"" checked>'
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

  $('#final_input').jstree('destroy');
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
    "URI" : configs != "" ? configs : document.getElementById('mediawikibox').checked ? undefined : erro = true,
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
    "User" : localStorage.user,
    "Id":sessionStorage.id,
    "Start_date": document.getElementById('start_date').checked ? document.getElementById('date_input').value :undefined,
    'Key' : getCookie("JSESSIONID"),
    'mediawiki' : document.getElementById('mediawikibox').checked ? true : undefined,
    'design_project' : localStorage.dp

  };

  if (erro == true) {
      var code = 'All fields must be filled. <br><br><button class="btn btn-default" id="ok" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide();">OK</button>';
		  $('#alert').html(code);
		  $('#overlay').show();
		  $('#overlay-back').show();
    //alert("All Fields must be filled");
    return;
  }
  console.log(jsonData);
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

function get_ip_address() {

    if ($_SERVER["REMOTE_ADDR"] != "::1") {
        // remote operation
        return $_SERVER["REMOTE_ADDR"];
    } else {
        // local operation
        $json_ip = file_get_contents("https://jsonip.com/?callback=");
        $arr_ip = json_decode($json_ip, true);
        return $arr_ip["ip"];
    }
}

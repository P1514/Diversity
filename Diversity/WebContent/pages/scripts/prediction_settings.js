var draw = false;
var ws;
var products;
var services;
var str = "";
var start = true;
var chartData;
var jsonData; /* = [{"Op":"Tree"},{"Id":1,"Name":"Morris Ground 1"},{"Id":2,"Name":"Austin Basket"},{"Id":3,"Name":"Austin Soccer"},{"Id":4,"Name":"Morris Sea 1000"},{"Id":5,"Name":"Morris Sea 2099"},{"Id":6,"Name":"Morris Wind"},{"Id":7,"Name":"Austin Polo"},{"Id":8,"Name":"Austin Cricket"},{"Id":9,"Name":"Austin XC"},{"Id":10,"Name":"Austin Base"},{"Products":[{"Products":[{"Products":[{"Products":[{"Id":21,"Name":"21"}],"Id":20,"Name":"20"}],"Id":19,"Name":"19"}],"Id":18,"Name":"18"}],"Id":11,"Name":"Sole Machine"},{"Id":12,"Name":"Sewing Machine"},{"Products":[{"Id":14,"Name":"Rubber"},{"Id":15,"Name":"Aluminium"}],"Id":13,"Name":"Cleat Applier"},{"Id":16,"Name":"Glueing Machine"},{"Id":17,"Name":"Neoprene Cutting Machine"}];
*/
var count; // for timespan
var snapshots;
var snap = false;
var snap_name;
var snap_user;
var snap_date;
var snapProds;
var snapServs;
function getCookie(name) { //not being used
	  var value = "; " + document.cookie;
	  var parts = value.split("; " + name + "=");
	  if (parts.length == 1){
		  document.cookie="JSESSIONID=10";
	  }
	  value = "; " + document.cookie;
	  parts = value.split("; " + name + "=");
	  if (parts.length == 2)
	    return parts.pop().split(";").shift();
	}
	google.charts.load('current', {packages: ['corechart', 'line']});
	google.charts.setOnLoadCallback(drawChart);
document.addEventListener('DOMContentLoaded', function() {

  $('#overlay-back').hide();
  $('#overlay').hide();

  if (window.location.href.indexOf('https://') != -1) {
		ws = new WebSocket('wss://' + window.location.hostname + ":"
				+ window.location.port + '/Diversity/server');
	} else {
		ws = new WebSocket('ws://' + window.location.hostname + ":"
				+ window.location.port + '/Diversity/server');
	}


  //Request products and services tree
  ws.onopen = function () {
		if (window.location.href.indexOf('snapshot=') != -1) {
			var snapName = window.location.href.split("snapshot=")[1].split("&")[0].replace('%20',' ').replace('%3A',':');
			snap_name = snapName;
			//snap = true;
			json = {
				'Op' : 'getrestrictions',
				'Role' : 'DEVELOPER',
				'Key' : getCookie('JSESSIONID'),
			}
			ws.send(JSON.stringify(json));
		} else {
			json = {
	      "Op" : "gettree",
	      "All" : 1,
	      'Key' : getCookie("JSESSIONID")
	    }

	    ws.send(JSON.stringify(json));
		}

  }

  ws.onmessage = function(event) {
    var json = JSON.parse(event.data.replace(/\\/g,''));

    //If the message Op is 'Tree', build the products and services list as an interactive tree
    if (json[0].Op == "Tree") {
      jsonData = JSON.parse(JSON.stringify(json));
      makeTree("prod_list",jsonData);
      $('#prod_list').jstree(true).refresh();
      str = "";
      start = true;
      makeTree("serv_list",jsonData);
      $('#serv_list').jstree(true).refresh();

			if (localStorage.tutorial != undefined && localStorage.tutorial.indexOf("prediction=done") == -1) { // if the user never opened this page, start the tutorial

				request_tutorial();
			}
			if (localStorage.tutorial == undefined) {
				localStorage.tutorial += "";
				request_tutorial();
			}


    }

		if (json[0].Op == "Rights") {
			requestSnapshot(snap_name);
		}
    //If the message Op is 'Prediction', draw the predicted global sentiment chart
    if (json[0].Op == "Prediction") {
      draw = true;
      //console.log(json);
      chartData = JSON.parse(JSON.stringify(json));
			if (snap) {
				snapProds = json[1][json[1].length - 1].Products.split(',');
				snapServs = json[1][json[1].length - 1].Services.split(',');
				for (var i = 0; i < chartData.length; i++) {
					if (chartData[i].hasOwnProperty('User')) {
						snap_user = chartData[i].User;
					} else if (chartData[2].hasOwnProperty('Date')) {
						var d = chartData[2].Date.split(" ");
						var dateString = d[1] + " " + d[2] + ", " + d[5];
						snap_date = dateString;
					} else if (chartData[i].hasOwnProperty('PSS')) {
						snap_pss = chartData[i].PSS;
					}
				}
				$('#page_title').html('Snapshot: ' + snap_name.replace(/\+/g, ' '));
				$('#snap_label').html('<p style="margin-left:50px">Created by ' + snap_user + ' on ' + snap_date + '</p>');
				$('#tip').hide();
				$('#prod_list').hide();
				$('#serv_list').hide();
				$('#lists').hide();
				$('#submit').hide();
				$('#radio_label1').hide();
				$('#radio_label2').hide();

					//var prods = json[1][json[1].length - 1].Products.split(',');
					var prodsHTML = '';
					for (var i = 0; i < snapProds.length; i++) {
						prodsHTML += snapProds[i] + ', ';
					}

					//var servs = json[1][json[1].length - 1].Services.split(',');
					var servsHTML = '';
					for (var i = 0; i < snapServs.length; i++) {
						servsHTML += snapServs[i] + ', ';
					}
					$('#snap_label').html('<p style="margin-left:50px">Created by ' + snap_user + ' on ' + snap_date + '</p><br><p style="margin-left:50px"><b>Products:</b> ' + prodsHTML.substring(0, prodsHTML.length - 4) + '</p><br><p style="margin-left:50px"><b>Services:</b> ' + servsHTML.substring(0, servsHTML.length - 4)  + '</p>');
			}
      drawChart();
  	  $('#overlay').fadeOut(2000);
  	  $('#overlay-back').fadeOut(2000);
    }
		//If the message contains the string 'Snapshots', build a dropdown with all the saved snapshots and display it
		if (json[0] == "Snapshots") {
				snapshots = json[1];
				displaySnapshots();
		}
		if (snap) {

		} else {
			$('#snap_label').empty();

		}
    //If the message Op is 'Error', it contains a message from the server, which is displayed in an overlay box
    if (json[0].Op == "Error") {
      if (json[0].hasOwnProperty("Message")) {
        $('#overlay-back').show();
        $('#overlay').show();
        $('#error').html(json[0].Message + '<br>' + '<input id="submit" class="btn btn-default" onclick="$(\'#overlay-back\').hide();$(\'#overlay\').hide();" style="margin-top:20px" type="submit" value="OK" />');
      }
    }

		if (json[0].Op == "DBLoading") {
			$('#dbload').show();
		}
    //If the message contains the string 'Snapshots', build a dropdown with all the saved snapshots and display it
    if (json[0] == "Snapshots") {
	      snapshots = json[1];
	      displaySnapshots();
    }

  }
});

function request_tutorial() {
  $('#error').html("Would you like to see a tutorial for this page?" + '<br><br><button class="btn btn-default" id="yes" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide();start_tutorial();">Yes</button><button class="btn btn-default" id="no" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide();">No</button>');
  $('#overlay').show();
  $('#overlay-back').show();
}

function start_tutorial() {
  options_tutorial();
  $('#tutorial_box').toggle();
}

function options_tutorial() {
  var pos=$('#back').offset();
  var h=$('#back').height() + 10;
  var w=$('#back').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h });
  $('#tutorial').html('At the top of the prediction page, you can load a previously saved snapshot.<br><br><center><button class="btn btn-default" id="next" style="margin-left:5px;" onclick="generate_tutorial();">Next</button></center>');
}

function generate_tutorial() {
  var pos=$('#submit').offset();
  var h=$('#submit').height() + 10;
  var w=$('#submit').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h});
  $('#tutorial').html('To generate a prediction, you can start by selecting a combination of products and services from the lists above. When you\'re finished, click the \'Generate Prediction\' button to view the results. When the results are displayed, you can also click the \'Save Snapshot\' button that appears in order to be able to access the data later.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="options_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="end_tutorial();">Next</button></center>');
}

function end_tutorial() {
  var pos=$('#submit').offset();
  var h=$('#submit').height() + 10;
  var w=$('#submit').width();

  $('#tutorial').html('You\'ve reached the end of the tutorial. You can access it at any time by clicking the <i class="fa fa-question-circle" aria-hidden="true"></i> button at the top right corner of the page.<br><br><center><button class="btn btn-default" style="margin-left:5px;" id="end" onclick="$(\'#tutorial_box\').toggle();">Finish</button></center>');

  if (localStorage.tutorial.indexOf("prediction=done") == -1) {
    localStorage.tutorial += "prediction=done;";
  }
}

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
		if (div.indexOf("prod") != -1) {
	    if (test[i].Type == "Product") {
				makeList(test[i]);
			}
		}

		if (div.indexOf("serv") != -1) {
	    if (test[i].Type == "Service") {
				makeList(test[i]);
			}
		}
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

/*
* Finds and returns objects with a specific property inside an array.
* Input: obj - array to be searched;
*        key - name of the property that we're looking for;
*        val - value of the property that we're looking for.
* Output: an array with all objects that meet the requirements.
*/
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
* Makes an HTML unordered list from a json array (uses recursion, requires a var start = true)
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

/*
* Sends a JSON message requesting a prediction for the given products and services.
*/
function submit() {
  var json = {
    "Op" : "prediction",
    "Products" : products != "" ? products : undefined,
    "Services" : services != "" ? services : undefined,
		"type" : document.getElementById('radio_lifecycle').checked ? "lifecycle" : undefined,
    'Key' : getCookie("JSESSIONID")
  }
  snap = false;
  $('#page_title').html('Create Prediction');
  ws.send(JSON.stringify(json));
  $('#error').html('<i class="fa fa-spinner fa-3x fa-spin" aria-hidden="true"></i><br>Loading, please wait...');
  $('#overlay').show();
  $('#overlay-back').show();
}

/*
* Displays an overlay window to save a new snapshot.
*/
function save() {
  var code = '<center><b>Save snapshot</b></center><br><label for="snap_name">Name: </label><input id="snap_name" type="text" style="margin-left:15px;" placeholder="Snapshot name..."><br><br><button class="btn btn-default" id="save" onclick="send($(\'#snap_name\').val());$(\'#overlay\').hide();$(\'#overlay-back\').hide()">Save</button> <button class="btn btn-default" id="cancel" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide()">Cancel</button>';
  $('#error').html(code);
  $('#overlay').show();
  $('#overlay-back').show();
}

/*
* Sends a message requesting a list of snapshots.
*/
function load() {
  var json = {
    "Op" : "load_snapshot",
    "Type" : "Prediction",
    'Key' : getCookie("JSESSIONID")
  }

  ws.send(JSON.stringify(json));
}

/*
* Sends a message with all the data required to save a snapshot.
*/
function send(val) {
  var json = {
    "Op" : "Snapshot",
    "type" : "Prediction",
    "name" : val,
    "creation_date" : new Date(),
    "timespan" : count,
    "user" : "test",
    "Products" : products != "" ? products : undefined,
    "Services" : services != "" ? services : undefined,
    'Key' : getCookie("JSESSIONID")
  }
  ws.send(JSON.stringify(json));
}

/*
* Builds a dropdown list of availiable snapshots and displays them in an overlay window to be loaded.
*/
function displaySnapshots() {
  var code = '<center><b>Load snapshot</b></center><br><label for="snap_name">Select a snapshot: </label><select id="select_snap" style="margin-left:15px;"></select><br><br><button class="btn btn-default" id="sel_btn" onclick="requestSnapshot($(\'#select_snap\').find(\':selected\').text());$(\'#overlay\').hide();$(\'#overlay-back\').hide()">Load</button> <button class="btn btn-default" id="cancel" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide()">Cancel</button>';
  $('#error').html(code);
  for (var i=0; i < snapshots.length; i++) {
    $('#select_snap').append($('<option>', {
      value: snapshots[i].Name,
      text: snapshots[i].Name
    }));
  }
  $('#overlay').show();
  $('#overlay-back').show();
}

/*
* Sends a message requesting a specific snapshot to be loaded.
*/
function requestSnapshot(val) {
  var json = {
    "Op" : "load_snapshot",
    "Name" : val,
    'Key' : getCookie("JSESSIONID")
  }
  snap = true;
  snap_name = val;

  ws.send(JSON.stringify(json));
}

/*
* Uses Google Charts API to draw the predicted global sentiment chart.
*/
function drawChart() {
  if (draw) {
    $("#wrapper").show();
    var data = new google.visualization.DataTable();
    data.addColumn('string', 'Month');
    data.addColumn('number', 'Global Sentiment');
    data.addColumn({id:'min', type:'number', role:'interval'}); // value - variance
    data.addColumn({id:'max', type:'number', role:'interval'}); // value + variance

    var sum = 0;
    count = 0;

    for (i = 0; i < chartData[1].length; i++) {
      var month = "";
      var value = -1;
      var variance = -1;

      if (chartData[1][i].hasOwnProperty('Month')) {
        month = chartData[1][i].Month;
      }

      if (chartData[1][i].hasOwnProperty('Value')) {
        value = chartData[1][i].Value;
      }

      if (chartData[1][i].hasOwnProperty('Variance')) {
        variance = chartData[1][i].Variance;
      }

      if (month != "" && (value != -1 && variance != -1)) {
        sum += value;
        count += 1;

        data.addRow([month, value, value - variance, value + variance]);
      }
    }

    var options = {
      title : 'Predicted global sentiment over time',
      hAxis: {
        title: 'Time'
      },
      series: {
        0: {
          color: '#604460',
          lineWidth: 3
        }
      },
      intervals: {
        'style':'area',
      },
      vAxis: {
        title: 'Global Sentiment',
        minValue: 0,
        maxValue: 100
      },
      legend: {
        position: 'bottom'
      },
      backgroundColor: {
        fill:'transparent'
      },
      curveType: 'function'
    };

    var chart = new google.visualization.LineChart(document.getElementById('graph'));
    chart.draw(data,options);

    var data = google.visualization.arrayToDataTable([['Indicator', 'Value'], ['Average Global Sentiment', count != 0 ? Math.round(sum/count) : 0]]);

    var options = {
      title: 'Predicted average global sentiment',
      legend: 'none',
      pieSliceText: 'value',
      pieSliceTextStyle: {
        fontSize: 20,
      },
      colors:['#604460'],
      backgroundColor: {
        fill:'transparent'
      },
      tooltip: { trigger: 'none' },
    };

    var chart = new google.visualization.PieChart(document.getElementById('pie'));
    chart.draw(data, options);

  } else {
    return;
  }
}

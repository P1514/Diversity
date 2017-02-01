var shade = 0;
var filteredByProduct = false;
var json;
var jsonData;
var sentimentdata;
var ws;
var top_middle;
var top_left;
var top_right;
var bottom_left;
var bottom_right;
var bottom_middle;
var windowwidth = $(window).width();
var needlecolor = '#604460';
var animationend = false;
var extra = false;
var snap = false;
var name = "";

function setExtra() {
	extra = !extra;
}

function connect() {
	var css = /* Needle */"#globalgauge path:nth-child(2){ fill:" + needlecolor
			+ " ; stroke-width:0; } #globalgauge circle:nth-child(1){ fill:" + needlecolor
			+ " ; stroke-width:0; }", head = document.head
			|| document.getElementsByTagName('head')[0], style = document
			.createElement('style');

	style.type = 'text/css';
	if (style.styleSheet) {
		style.styleSheet.cssText = css;
	} else {
		style.appendChild(document.createTextNode(css));
	}
	head.appendChild(style);
	top_left = new google.visualization.PieChart(document
			.getElementById('opinionpie'));
	top_middle = new google.visualization.ColumnChart(document
			.getElementById('polaritybar'));
	top_right = new google.visualization.Gauge(document
			.getElementById('globalgauge'));
	bottom_left = new google.visualization.PieChart(document
			.getElementById('reachpie'));
	bottom_middle = new google.visualization.LineChart(document
			.getElementById('reachline'));
	bottom_right = new google.visualization.LineChart(document
			.getElementById('globalline'));
	google.visualization.events.addListener(bottom_right, 'select', function() {
		var selection = bottom_right.getSelection()[0];
		if (selection != undefined && (selection.hasOwnProperty('row') && selection.row != null)) {
			var row = selection.row ;
			var col = selection.column;
			var month = sentimentdata.getValue(row, 0);
      var product = sentimentdata.getColumnLabel(selection.column);

      if (product != "Global" && filteredByProduct) {
        json = {
          "Op" : "getposts",
          "Id" : sessionStorage.id,
          "Param" : "Month",
          "Values" : month,
          "Product" : product,
        }
      } else {
        json = {
          "Op" : "getposts",
          "Id" : sessionStorage.id,
          "Param" : "Month",
          "Values" : month,
        }
      }

			ws.send(JSON.stringify(json));

		} else {
			json = {
				"Op" : "getposts",
				"Id" : sessionStorage.id
			}

			ws.send(JSON.stringify(json));
		}
	})

	document.getElementById("Cookie").innerHTML = "Model: "
			+ window.sessionStorage.model + "; PSS: "
			+ window.sessionStorage.pss;

	ws = new WebSocket('ws://' + window.location.hostname + ":"
			+ window.location.port + '/Diversity/server');

	ws.onopen = function() {

		json = {
			"Op" : "getconfig",
			"Id" : sessionStorage.id,
		}

		ws.send(JSON.stringify(json));
	};

	ws.onmessage = function(event) {
		json = JSON.parse(event.data);

		if (snap) {
			$('#genderfilt').hide();
			$('#agefilt').hide();
			$('#locationfilt').hide();
			$('#finalfilt').hide();
		}
		
		if (json[0].Op == "Error") {
			$('#loading').html(json[0].Message + '<br><br><button class="btn btn-default" id="ok" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide()">OK</button>');
			$('#overlay').show();
			$('#overlay-back').show();
			return;
		}

		if (json[0].Op == "Configs") {
			var jsonData1 = JSON.parse(JSON.stringify(json));

			for (var i = 1; i < jsonData1.length;) {
				switch (jsonData1[i].Param) {
				case "Age":
					var x = document.getElementById("agefilt");
					break;
				case "Gender":
					var x = document.getElementById("genderfilt");
					break;
				case "Location":
					var x = document.getElementById("locationfilt");
					break;
				case "Product":
					var x = document.getElementById("finalfilt");
				}
				var tsize = jsonData1[i].Size;
				// console.log(tsize);
				i++;
				for (var ii = 0; ii < tsize; ii++, i++) {
					var option = document.createElement('option');
					if (jsonData1[i].hasOwnProperty("Min")) {
						option.text = jsonData1[i].Min + "-" + jsonData1[i].Max;
					} else {
						if (!(jsonData1[i].hasOwnProperty("Gender"))
								&& !(jsonData1[i].hasOwnProperty("Product"))) {
							option.text = jsonData1[i].Location;
						} else if (!(jsonData1[i].hasOwnProperty("Gender"))
								&& !(jsonData1[i].hasOwnProperty("Location"))) {
							option.text = jsonData1[i].Product;
						} else if (!(jsonData1[i].hasOwnProperty("Product"))
								&& !(jsonData1[i].hasOwnProperty("Location"))) {
							option.text = jsonData1[i].Gender;
						}
						/*
						 * option.text = (!(jsonData1[i]
						 * .hasOwnProperty("Gender"))) ? jsonData1[i].Location :
						 * jsonData1[i].Gender;
						 */
					}
					x.add(option);
				}
			}

			json = {
				"Op" : "opinion_extraction",
				"Id" : window.sessionStorage.id
			}

			ws.send(JSON.stringify(json));
			return;

		}
		if (json[0].Op == "OE_Redone") {
			jsonData = JSON.parse(JSON.stringify(json));
			if ( json[1].hasOwnProperty("Error")) {
				if (json[1].Error == "No_data" ) {
						$('#loading').html('No data to display.<br><br><button class="btn btn-default" id="ok" onclick="location.href = \'index.html\'">OK</button>');
						$('#overlay').show();
						$('#overlay-back').show();
				}
			} else {
				console.log("redone");
				drawChart();
			}
      var json = {
            "Op" : "getposts",
            "Id" : sessionStorage.id,
      }

			ws.send(JSON.stringify(json));
			return;
		}

		if (json[0].Op == "table") {
			// populate table
			var tr;
			$('#posts tbody').empty();
			for (var i = 1; i < json.length; i++) {
				tr = $('<tr/>');
				tr.append("<td>" + json[i].Name + "</td>");
				tr.append("<td>" + json[i].Message + "</td>");
				tr.append("<td>" + json[i].Comments + "</td>");
				tr.append("<td>" + json[i].Date + "</td>");
				tr.append("<td>" + json[i].Polarity + "</td>");
				tr.append("<td>" + json[i].Reach + "</td>");
				tr.append("<td>" + json[i].Influence + "</td>");
				tr.append("<td>" + json[i].Location + "</td>");
				tr.append("<td>" + json[i].Gender + "</td>");
				tr.append("<td>" + json[i].Age + "</td>");
				tr.append("<td><input type=\"hidden\" name=\"id\" value=\""
						+ json[i].Id + "\">");
				$('#posts tbody').append(tr);
			}
			$('.table > tbody > tr').click(function(e) {
				clicker($(this).find('input[name="id"]').val());
			});

			return;
		}
		if (json[0].Op == "graph") {
			jsonData = JSON.parse(JSON.stringify(json));
			drawChart();
			return;
		}
		if (json[0].Op == "comments") {
			clicker();
			return;
		}
	};
}

google.charts.load('current', {
	packages : [ 'corechart', 'bar', 'gauge' ]
});
$(document).ready(function () {
	google.charts.setOnLoadCallback(connect);
});

function save() {
  var code = '<center><b>Save snapshot</b></center><br><label for="snap_name">Name: </label><input id="snap_name" type="text" placeholder="Snapshot name..."><br><br><button class="btn btn-default" id="save" onclick="send($(\'#snap_name\').val());$(\'#overlay\').hide();$(\'#overlay-back\').hide()">Save</button> <button class="btn btn-default" id="cancel" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide()">Cancel</button>';
  $('#loading').html(code);
  $('#overlay').show();
  $('#overlay-back').show();
}

function load() {
  // send request for snapshot list
  var json = {
    "Op" : "load_snapshot",
		"Type" : "Extraction"
  }

  ws.send(JSON.stringify(json));
}

function send(val) {
  var json = {
    "Op" : "Snapshot",
    "type" : "Extraction",
    "name" : val,
    "creation_date" : new Date(),
    "timespan" : 12,
    "user" : "test"
  }
  ws.send(JSON.stringify(json));
}

//need to receive json with snapshot names
function displaySnapshots() {
  var code = '<center><b>Load snapshot</b></center><br><label for="snap_name">Select a snapshot: </label><select id="select_snap"></select><br><br><button class="btn btn-default" id="sel_btn" onclick="requestSnapshot($(\'#select_snap\').find(":selected").text());$(\'#overlay\').hide();$(\'#overlay-back\').hide()">Save</button> <button class="btn btn-default" id="cancel" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide()">Cancel</button>';
  $('#loading').html(code);
  $('#overlay').show();
  $('#overlay-back').show();
}

function requestSnapshot(val) {
	name = val;
  var json = {
    "Op" : "load_snapshot",
    "Name" : val,
		"Type" : "All"
  }
	snap = true;
  ws.send(JSON.stringify(json));
}

// Detect table click

function clicker(hidden) {
	var thediv = document.getElementById('displaybox');
	var embedCode = '<iframe width="75%" height="45%" src="comments.html?id='
			+ hidden + ' frameborder="0" allowfullscreen="no"></iframe>';
	if (thediv.style.display == "none") {
		thediv.style.display = "";
		thediv.innerHTML = "<table width='100%' height='100%'><tr><td align='center' valign='bottom' width='80%' height='80%'>"
				+ "<param name='bgcolor' value='#000000'>"
				+ embedCode
				+ "</tr><tr align='center' valign='top' width='10%' height='10%'><td><a href='#' align='center' onclick='return clicker();'>CLOSE WINDOW</a></td></tr></table>";
		// thediv.innerHTML = ""++ "<button onclick='clicker()' id='closepage'
		// class='btn btn-default'>Close Page</button>";
	} else {
		thediv.style.display = "none";
		thediv.innerHTML = '';
	}
	return false;
}

function drawChart() {
	// Top Left
	var data = new google.visualization.DataTable();
	var i = 1;
	data.addColumn('string', 'Param');
	if (jsonData[i].Graph == "Top_Left") {
		for (filt = 1; i < jsonData.length
				&& jsonData[i].hasOwnProperty("Filter")
				&& jsonData[i].Graph == "Top_Left"; filt++) {
			data.addColumn('number', jsonData[i].Filter);
			i++;
			for (ii = 0; jsonData[i].Graph == "Top_Left"
					&& !jsonData[i].hasOwnProperty("Filter"); ii++, i++) {
				data.addRow();
				data.setCell(ii, 0, "Value");
				data.setCell(ii, filt, jsonData[i].Value);
			}
		}

		var colors = new Array();
		for (var color = 1; color < filt; color++) {
			colors.push(chartcolor(data.getColumnLabel(color)));
		}

		var options = {
			title : 'Total Opinions',
			titlePosition : 'center',
			pieSliceTextStyle : {
				fontSize : 18
			},
			pieSliceText : 'value',
			legend : {
				position : 'none'
			},
			enableInteractivity : false,
			colors : colors,
			animation : {
				duration : 1000,
				easing : 'out',
			},
			backgroundColor: {
				fill:'transparent'
			},
		};

		top_left.draw(data, options);
	}

	// Top Middle
	if (jsonData[i].Graph == "Top_Middle") {
		var data = new google.visualization.DataTable();
		data.addColumn('string', 'Polarity')
		// console.log(jsonData);
		for (filt = 1; i < jsonData.length && jsonData[i].Graph == "Top_Middle"; filt++) {
			data.addColumn('number', jsonData[i].Filter);
			i++;
			for (ii = 0; i < jsonData.length
					&& jsonData[i].Graph == "Top_Middle"
					&& !jsonData[i].hasOwnProperty('Filter'); ii++, i++) {
				if (filt == 1)
					data.addRow();
				data.setCell(ii, 0, jsonData[i].Param);
				data.setCell(ii, filt, jsonData[i].Value);
			}
		}
		colors = new Array();
		for (var color = 1; color < filt; color++) {
			colors.push(chartcolor(data.getColumnLabel(color)));
		}
		var options = {
			title : 'Polarity',
			colors : [ '#9575cd', '#33ac71' ],
			vAxis : {
				title : 'no. posts',
				gridlines : {
					count : 6,
				}
			},
			backgroundColor: {
				fill:'transparent'
			},
			hAxis : {

				baselineColor : 'transparent',
				gridlines : {
					color : 'transparent'
				}
			},
			legend : {
				position : 'bottom'
			},
			colors : colors,
			animation : {
				duration : 1000,
				easing : 'out',
			},
		};

		top_middle.draw(data, options);
	}
	// Top Right
	if (jsonData[i].Graph == "Top_Right") {
		var data = new google.visualization.DataTable();
		data.addColumn('string', 'Polarity')
		data.addRow();
		data.setCell(0, 0, "Sentiment");
		for (ii = 0; i < jsonData.length && jsonData[i].Graph == "Top_Right"; ii++, i++) {
			data.addColumn('number', 'Global')
			data.setCell(0, 1, jsonData[i].Value);
		}

		var options = {
			max : 100,
			min : 0,
			redColor : '#DD6100',
			yellowColor : '#FFC50A',
			redFrom : 0,
			redTo : 25,
			yellowFrom : 25,
			yellowTo : 50,
			minorTicks : 10,
			animation : {
				duration : 500,
			},
		};

	}
	top_right.draw(data, options);

	// Bottom Left
	if (jsonData[i].Graph == "Bottom_Left") {
		var data = new google.visualization.DataTable();
		data.addColumn('string', 'Gender');
		data.addColumn('number', 'Global');
		for (ii = 0; i < jsonData.length && jsonData[i].Graph == "Bottom_Left"; ii++, i++) {
			data.addRow();
			data.setCell(ii, 0, jsonData[i].Param);
			data.setCell(ii, 1, jsonData[i].Value)
		}
		filt = 2;
		colors = new Array();
		for (var color = 1; color < filt; color++) {
			colors.push(chartcolor(data.getColumnLabel(color)));
		}

		var options = {
			title : 'Average Reach',
			pieSliceTextStyle : {
				fontSize : 18
			},
			pieSliceText : 'value',
			legend : {
				position : 'none'
			},
			enableInteractivity : false,
			colors : colors,
			animation : {
				duration : 1000,
				easing : 'out',
			},
			backgroundColor: {
				fill:'transparent'
			},
		};

		bottom_left.draw(data, options);

	}
	// Bottom Middle
	if (jsonData[i].Graph == "Bottom_Middle") {
		var data = new google.visualization.DataTable();
		data.addColumn('string', 'Month');

		for (filt = 1; i < jsonData.length
				&& jsonData[i].Graph == "Bottom_Middle"; filt++) {
			data.addColumn('number', jsonData[i].Filter);
			i++;

			for (ii = 0; i < jsonData.length
					&& jsonData[i].Graph == 'Bottom_Middle'
					&& !jsonData[i].hasOwnProperty('Filter'); ii++, i++) {
				if (filt == 1) {
						data.addRow();
				}
				if (jsonData[i].Value != 0) {
					data.setCell(ii, 0, jsonData[i].Month);
					data.setCell(ii, filt, jsonData[i].Value)
				} else {
					data.setCell(ii, 0, jsonData[i].Month);
				}
			}
		}
		colors = new Array();
		for (var color = 1; color < filt; color++) {
			colors.push(chartcolor(data.getColumnLabel(color)));
		}


    function midSelectHandler() {
      var selectedItem = bottom_middle.getSelection()[0] != undefined ? bottom_middle.getSelection()[0] : false ;
      if (selectedItem) {
        if ( (selectedItem.row != null && bottom_right.getSelection()[0] == undefined) || (selectedItem.row != null && selectedItem.row != bottom_right.getSelection()[0].row)) {
          bottom_right.setSelection([{column:selectedItem.column, row:selectedItem.row}]);
          google.visualization.events.trigger(bottom_right, 'select');
        }
      } else {
				bottom_right.setSelection([]);
			}
    }
/*
		function midSelectHandler() {
			var selectedItem = bottom_middle.getSelection()[0];
			if (selectedItem) {
				if (bottom_right.getSelection()[0] == undefined || selectedItem.row != bottom_right.getSelection()[0].row) {
					bottom_right.setSelection([{column:selectedItem.column, row:selectedItem.row}]);
					google.visualization.events.trigger(bottom_right, 'select');
				}
			}
		}
*/

		var options = {
			hAxis : {
				showTextEvery : 1,
				textStyle : {
					fontSize : 8
				}
			},
			vAxis : {
				title : 'Reach',
				viewWindow : {
					max : 0,
					min : 2
				}
			},
			legend : {
				position : 'bottom'
			},
			colors : colors,
			animation : {
				duration : 1000,
				easing : 'out',
			},
			backgroundColor: {
				fill:'transparent'
			},
		};

    google.visualization.events.addListener(bottom_middle, 'select', midSelectHandler);


		//google.visualization.events.addListener(bottom_middle, 'select', midSelectHandler);

		bottom_middle.draw(data, options);
	}
	// Bottom Right
	if (jsonData[i].Graph == "Bottom_Right") {

		var date = new Date();
    var locale = "en-us";
    var month = date.toLocaleString(locale, { month: "short" }).toUpperCase;

		var first = -1;
		var prev = -1;
		var ext = false;
		var trigger = false;
		var series = [];
		var randomYear = 2000;

		var monthNames = ["JAN", "FEB", "MAR", "APR", "MAY", "JUN",
		  "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"];

		var columns = [];

		sentimentdata = new google.visualization.DataTable();
		sentimentdata.addColumn('string', 'Month');
		columns.push('Month');
		for (filt = 1; i < jsonData.length && (jsonData[i].Graph == "Bottom_Right" || jsonData[i].Graph == "Bottom_Right_Ex"); filt++) {
			var name = jsonData[i].Filter;
			if (jsonData[i].Graph == "Bottom_Right") {
				if (columns.indexOf(name) == -1 ) {
					sentimentdata.addColumn('number', name, name);
					columns.push(name);
				}
			}
			i++;
			for (ii = 0; i < jsonData.length && (jsonData[i].Graph == 'Bottom_Right')
					&& !jsonData[i].hasOwnProperty('Filter'); ii++, i++) {
				if (filt == 1)
					sentimentdata.addRow();
				if (jsonData[i].Value != -1) {
					if (jsonData[i].Graph == 'Bottom_Right') {
						sentimentdata.setCell(ii, 0, jsonData[i].Month);
						sentimentdata.setCell(ii, filt, jsonData[i].Value);
					}
				} else {
					sentimentdata.setCell(ii, 0, jsonData[i].Month);
				}
			}

			for (var iii = 11; i < jsonData.length && (jsonData[i].Graph == 'Bottom_Right_Ex') && !jsonData[i].hasOwnProperty('Filter');iii++,ii++,i++) {
				if (jsonData[i].Graph == 'Bottom_Right_Ex') {
					if (columns.indexOf('Extrapolation for ' + name) == -1) {
						sentimentdata.addColumn('number', 'Extrapolation for ' + name, 'Extrapolation for ' + name);
						columns.push('Extrapolation for ' + name);
						series.push(sentimentdata.getNumberOfColumns()-2);
					}
					sentimentdata.addRow();
					sentimentdata.setCell(iii, 0, jsonData[i].Month);
					sentimentdata.setCell(iii, filt, jsonData[i].Value);
				}
			}
		}


		colors = new Array();
		for (var color = 1; color < filt; color++) {
			colors.push(chartcolor(sentimentdata.getColumnLabel(color)));
		}


    function rightSelectHandler() {
      var selectedItem = bottom_right.getSelection()[0] != undefined ? bottom_right.getSelection()[0] : false ;
      if (selectedItem) {
        if ( (selectedItem.row != null && bottom_middle.getSelection()[0] == undefined) || (selectedItem.row != null && selectedItem.row != bottom_middle.getSelection()[0].row)) {
					bottom_middle.setSelection([{column:selectedItem.column, row:selectedItem.row}]);
          google.visualization.events.trigger(bottom_middle, 'select');
      	}
  		} else {
				bottom_middle.setSelection([]);
			}
		}

/*
		function rightSelectHandler() {
			var selectedItem = bottom_right.getSelection()[0];
			if (selectedItem) {
				if (bottom_middle.getSelection()[0] == undefined || selectedItem.row != bottom_middle.getSelection()[0].row) {
					bottom_middle.setSelection([{column:selectedItem.column, row:selectedItem.row}]);
					google.visualization.events.trigger(bottom_middle, 'select');
				}
			}
		}
*/

		var options = {
			hAxis : {
				format: 'MMM',
				showTextEvery : 1,
				textStyle : {
					fontSize : 8
				},
				viewWindowMode: 'pretty',
			},
			vAxis : {
				title : 'Sentiment',
				viewWindow : {
					max : 0,
					min : 100
				}
			},
			legend : {
				position : 'bottom'
			},
			colors : colors,
			animation : {
				duration : 1000,
				easing : 'out',
			},
			backgroundColor: {
				fill:'transparent'
			},
			series: {},
		};

		for (var v = 0; v < series.length; v++) {
			options["series"][series[v]] = { lineDashStyle: [4, 4] }
		}

		console.log(options);
    google.visualization.events.addListener(bottom_right, 'select', rightSelectHandler);

		//google.visualization.events.addListener(bottom_right, 'select', rightSelectHandler);
		bottom_right.draw(sentimentdata, options);
	}
	 $('#overlay').fadeOut(2000);
	 $('#overlay-back').fadeOut(2000);
}

// create trigger to resizeEnd event
$(window).resize(function() {
	if (this.resizeTO)
		clearTimeout(this.resizeTO);
	this.resizeTO = setTimeout(function() {
		$(this).trigger('resizeEnd');
	}, 500);
});

// redraw graph when window resize is completed
$(window).on('resizeEnd', function() {
	if (windowwidth < $(window).width()) {
		windowwidth = $(window).width(); // Workaround because of gauge
											// colors, Check why when on resize
											// to smaller error occurs and
											// graphs dont resize at all
		drawChart();
	}
});

function getColor() {
	switch (shade) {
	case 0:
		shade++;
		return "#FFC00C";
	case 1:
		shade++;
		return "#DD9808";
	case 2:
		shade++;
		return "#C06504";
	case 3:
		shade = 0;
		return "#843E02"
	}
}

function chartcolor(data) {
	switch (data) {
	case "Global":
	case "Extrapolation for Global":
		return "#604460";
	case "Male":
	case "Extrapolation for Male":
		return "#00617F";
	case "Female":
	case "Extrapolation for Female":
		return "#0093C8";
	case "Asia":
	case "Extrapolation for Asia":
		return "#FF3C14";
	case "Europe":
	case "Extrapolation for Europe":
		return "#A60202";
	case "0-30":
	case "Extrapolation for 0-30":
		return "#BFD730";
	case "31-60":
	case "Extrapolation for 31-60":
		return "#8CA122";
	case "61-90":
	case "Extrapolation for 61-90":
		return "#5C6E0E";
	default:
		return getColor();
	}
}

function changeRequest() {
	var gender = document.getElementById("genderfilt").value;
	var location = document.getElementById("locationfilt").value;
	var age = document.getElementById("agefilt").value;
	var products = document.getElementById("finalfilt").value;
	var globalradio = document.getElementById('Global').checked;
	var genderradio = document.getElementById('Gender').checked;
	var locationradio = document.getElementById('Location').checked;
	var ageradio = document.getElementById('Age_radio').checked;
	var finalradio = document.getElementById('Final').checked;
	var extrapolate = document.getElementById('extrapolate').checked;
	var json;

	if (snap) {
		json = {
			"Op" : "load_snapshot",
			"Type" : "",
			"Name" : name
		};

		if (globalradio == true) {
			needlecolor = '#604460';
			json.Type = "All";
		}

		if (genderradio == true) {
			needlecolor = '#00617F';
			json.Type = "Gender";
		}

		if (locationradio == true) {
			needlecolor = '#A60202';
			json.Type = "Location";
		}

		if (ageradio == true) {
			needlecolor = "#5C6E0E";
			json.Type = "Age";
		}

		if (finalradio == true) {
			needlecolor = '#FFC00C';
			json.Type = "Product";
		}

	} else {
		json = {
			"Op" : "oe_refresh",// OE_Filter
			"Param" : "",
			"Values" : "",
			"Filter" : "",
			"Id" : sessionStorage.id,
			"Extrapolate" : extrapolate ? 1 : undefined,
		};

		if (globalradio == true)
			needlecolor = '#604460';

		if (gender == "All" && location == "All" && age == "All"
				&& products == "All" && globalradio) {
			json.Param += "Global";
			json.Values += "Global";
		} else {
			if (ageradio == false) {

				if (age == "All") {
					json.Param += "Age,";
					json.Values += "All,";

				} else {
					json.Param += "Age,";
					var select = document.getElementById('agefilt');
					json.Values += age + ",";
				}
			} else {
				needlecolor = "#5C6E0E";
				json.Filter = "Age";
			}
			if (genderradio == false) {

				if (gender == "All") {
					json.Param += "Gender,";
					json.Values += "All,";

				} else {
					json.Param += "Gender,";
					var select = document.getElementById('genderfilt');
					json.Values += gender + ",";
				}
			} else {
				needlecolor = '#00617F';
				json.Filter = "Gender";
			}
			if (locationradio == false) {
				if (location == "All") {
					json.Param += "Location,";
					json.Values += "All,";
				} else {
					json.Param += "Location,";
					var select = document.getElementById('locationfilt');
					json.Values += location + ",";
				}
			} else {
				needlecolor = '#A60202';
				json.Filter = "Location";
			}
			if (finalradio == false) {
				filteredByProduct = false;
				if (products == "All") {
					json.Param += "Product,";
					json.Values += "All,";
				} else {
					json.Param += "Product,";
					var select = document.getElementById('finalfilt');
					json.Values += products + ",";

				}
			} else {
				needlecolor = '#FFC00C';
				json.Filter = "Product";
				filteredByProduct = true;
			}
		}
	}

	/*
	 * if (ageradio == "false") { if (age == "All") {
	 *
	 * json.Param += "Age,"; json.Values += "All,"; } else { json.Param +=
	 * "Age,"; var select = document .getElementById('agefilt');
	 * console.log(age); json.Values += age + ","; } } else {
	 * needlecolor="#5C6E0E" json.Filter="Age"; }
	 *  }
	 */

	var css = /* Needle */"#globalgauge path:nth-child(2){ fill:" + needlecolor
			+ " ; stroke-width:0; } #globalgauge circle:nth-child(1){ fill:" + needlecolor
			+ " ; stroke-width:0; }", head = document.head
			|| document.getElementsByTagName('head')[0], style = document
			.createElement('style');

	style.type = 'text/css';
	if (style.styleSheet) {
		style.styleSheet.cssText = css;
	} else {
		style.appendChild(document.createTextNode(css));
	}
	head.appendChild(style);
	$('#loading').html('<i class="fa fa-ellipsis-h fa-5x" aria-hidden="true"></i><br>Loading, please wait...');
	$('#overlay').show();
	$('#overlay-back').show();
	ws.send(JSON.stringify(json));
}
function fixbuttons(data) {

	if (data == "Global") {
		document.getElementById("genderfilt").disabled = false;
		document.getElementById("locationfilt").disabled = false;
		document.getElementById('agefilt').disabled = false;
		document.getElementById('finalfilt').disabled = false;
	}
	if (data == "Gender") {
		document.getElementById('genderfilt').selectedIndex = 0;
		document.getElementById("genderfilt").disabled = true;
		document.getElementById("locationfilt").disabled = false;
		document.getElementById('agefilt').disabled = false;
		document.getElementById('finalfilt').disabled = false;
	}
	if (data == "Location") {
		document.getElementById('locationfilt').selectedIndex = 0;
		document.getElementById("genderfilt").disabled = false;
		document.getElementById("locationfilt").disabled = true;
		document.getElementById('agefilt').disabled = false;
		document.getElementById('finalfilt').disabled = false;
	}
	if (data == "Age") {
		document.getElementById('agefilt').selectedIndex = 0;
		document.getElementById('genderfilt').disabled = false;
		document.getElementById('locationfilt').disabled = false;
		document.getElementById('agefilt').disabled = true;
		document.getElementById('finalfilt').disabled = false;
	}
	if (data == "Final") {
		document.getElementById('finalfilt').selectedIndex = 0;
		document.getElementById('genderfilt').disabled = false;
		document.getElementById('locationfilt').disabled = false;
		document.getElementById('agefilt').disabled = false;
		document.getElementById('finalfilt').disabled = true;
	}
}

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
		var selection = bottom_right.getSelection();
		if (selection != "") {
			var row = selection[0].row;
			var col = selection[0].column;
			var month = sentimentdata.getValue(row, 0);
      var product = sentimentdata.getColumnLabel(selection[0].column);

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

		if (json[0].Op == "Error") {
			alert(json[0].Message);
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
			drawChart();
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
google.charts.setOnLoadCallback(connect);
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
	data.addColumn('string', 'Param')
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
				if (filt == 1)
					data.addRow();
				data.setCell(ii, 0, jsonData[i].Month);
				data.setCell(ii, filt, jsonData[i].Value)
			}
		}

		colors = new Array();
		for (var color = 1; color < filt; color++) {
			colors.push(chartcolor(data.getColumnLabel(color)));
		}


    function midSelectHandler() {
      var selectedItem = bottom_middle.getSelection()[0];
      if (selectedItem) {
        if (bottom_right.getSelection()[0] == undefined || selectedItem.row != bottom_right.getSelection()[0].row) {
          bottom_right.setSelection([{column:selectedItem.column, row:selectedItem.row}]);
          google.visualization.events.trigger(bottom_right, 'select');
        }
      }
    }

		function midSelectHandler() {
			var selectedItem = bottom_middle.getSelection()[0];
			if (selectedItem) {
				if (bottom_right.getSelection()[0] == undefined || selectedItem.row != bottom_right.getSelection()[0].row) {
					bottom_right.setSelection([{column:selectedItem.column, row:selectedItem.row}]);
					google.visualization.events.trigger(bottom_right, 'select');
				}
			}
		}


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
		};

    google.visualization.events.addListener(bottom_middle, 'select', midSelectHandler);


		google.visualization.events.addListener(bottom_middle, 'select', midSelectHandler);

		bottom_middle.draw(data, options);
	}
	// Bottom Right
	if (jsonData[i].Graph == "Bottom_Right") {
		sentimentdata = new google.visualization.DataTable();
		sentimentdata.addColumn('string', 'Month');
		for (filt = 1; i < jsonData.length
				&& jsonData[i].Graph == "Bottom_Right"; filt++) {
			sentimentdata.addColumn('number', jsonData[i].Filter);
			i++;

			for (ii = 0; i < jsonData.length
					&& jsonData[i].Graph == 'Bottom_Right'
					&& !jsonData[i].hasOwnProperty('Filter'); ii++, i++) {
				if (filt == 1)
					sentimentdata.addRow();
				sentimentdata.setCell(ii, 0, jsonData[i].Month);
				sentimentdata.setCell(ii, filt, jsonData[i].Value)
			}
		}
		colors = new Array();
		for (var color = 1; color < filt; color++) {
			colors.push(chartcolor(sentimentdata.getColumnLabel(color)));
		}


    function rightSelectHandler() {
      var selectedItem = bottom_right.getSelection()[0];
      if (selectedItem) {
        if (bottom_middle.getSelection()[0] == undefined || selectedItem.row != bottom_middle.getSelection()[0].row) {
          bottom_middle.setSelection([{column:selectedItem.column, row:selectedItem.row}]);
          google.visualization.events.trigger(bottom_middle, 'select');
        }
      }
    }

		function rightSelectHandler() {
			var selectedItem = bottom_right.getSelection()[0];
			if (selectedItem) {
				if (bottom_middle.getSelection()[0] == undefined || selectedItem.row != bottom_middle.getSelection()[0].row) {
					bottom_middle.setSelection([{column:selectedItem.column, row:selectedItem.row}]);
					google.visualization.events.trigger(bottom_middle, 'select');
				}
			}
		}


		var options = {
			hAxis : {
				showTextEvery : 1,
				textStyle : {
					fontSize : 8
				}
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
		};
    google.visualization.events.addListener(bottom_right, 'select', rightSelectHandler);

		google.visualization.events.addListener(bottom_right, 'select', rightSelectHandler);
		bottom_right.draw(sentimentdata, options);
	}

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
		return "#604460";
	case "Male":
		return "#00617F";
	case "Female":
		return "#0093C8";
	case "Asia":
		return "#FF3C14";
	case "Europe":
		return "#A60202";
	case "0-30":
		return "#BFD730";
	case "31-60":
		return "#8CA122";
	case "61-90":
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

	var json = {
		"Op" : "oe_refresh",// OE_Filter
		"Param" : "",
		"Values" : "",
		"Filter" : "",
		"Id" : sessionStorage.id
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

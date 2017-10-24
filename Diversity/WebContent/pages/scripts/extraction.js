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
var snapshots;
var monthNames = [ "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG",
		"SEP", "OCT", "NOV", "DEC" ];
var month;
var year;
var day;
var product;
var user = localStorage.user;
var finalProductColors = [];
var loaded = false;
var mediawiki = false;
var snap_name;
var snap_user;
var snap_date;
var snap_pss;
var refreshTag = true;
var triedwiki = false;
// DEBUG STUFF - DELETE WHEN DONE TESTING---------------------------------------

$(window).on('load', function() {
	if (getCookie("Developer") == "Guilherme") {
		$("#DEBUG_USER").toggle();
	}
});

function loadingscreen(){
	var choice = Math.floor(Math.random() * (5 - 1 + 1)) + 1;
	switch (choice){
	case 1: $('#loading').html('<i class="fa fa-spinner fa-3x fa-spin" aria-hidden="true"></i><br>Apparently, it is taking too long. I’ll try again, please wait...');break;
	case 2: $('#loading').html('<i class="fa fa-spinner fa-3x fa-spin" aria-hidden="true"></i><br>Backend seems to be hung up, please wait a little bit more...');break;
	case 3: $('#loading').html('<i class="fa fa-spinner fa-3x fa-spin" aria-hidden="true"></i><br>Big amounts of data can take a long time, please wait...');break;
	case 4: $('#loading').html('<i class="fa fa-spinner fa-3x fa-spin" aria-hidden="true"></i><br>Backend says it’s almost done, please wait...');break;
	case 5: $('#loading').html('<i class="fa fa-spinner fa-3x fa-spin" aria-hidden="true"></i><br>Data should show up any moment now, please wait...');break;
	}

}
var loadingtimer=window.setInterval(loadingscreen, 10000);

$("#USER_LIST")
		.on(
				"change",
				function() {
					var user_l = parseInt(this.value.split(" ")[1]);

					var json = {
						"Op" : "tagcloud",
						"Id" : sessionStorage.id,
						//"Param" : "Month",
						//"Values" : month,
						"Day" : day,
						"Month" : month,
						"Year" : year !== undefined ? 1900 + year : undefined,
						"Product" : product != undefined && product != "Global" ? product
								: undefined,
						'Key' : getCookie("JSESSIONID"),
						'User' : user,
						'Type' : "All"
					}
					ws.send(JSON.stringify(json));

					//console.log("Selected user: " + user);
				});

// -----------------------------------------------------------------------------

/*
 * Toggles the 'extra' variable, which determines whether the extrapolation
 * checkbox is checked or not.
 */
function setExtra() {
	extra = !extra;
}

/*
 * Returns the value of a cookie
 */
function getCookie(name) {
	var value = '; ' + document.cookie;
	var parts = value.split('; ' + name + '=');
	if (parts.length == 2)
		return parts.pop().split(';').shift();
}

//Sends a message when a point in the bottom right chart is selected, which
// will change the displayed posts in the table.
function getPosts() {
	var selection = bottom_right.getSelection()[0];
	if (selection != undefined
			&& (selection.hasOwnProperty('row') && selection.row != null)) {
		var row = selection.row;
		var col = selection.column;
		month = sentimentdata.getValue(row, 0) != null ? monthNames[sentimentdata.getValue(row, 0).getMonth()] : 'JAN';
		day = sentimentdata.getValue(row, 0) != null ? sentimentdata.getValue(row, 0).getDay() : 1;
		year = sentimentdata.getValue(row, 0) != null? sentimentdata.getValue(row, 0).getYear() : 1970;
		product = sentimentdata.getColumnLabel(selection.column);

		if (product != "Global" && filteredByProduct) {
			json = {
				"Op" : "getposts",
				"Id" : sessionStorage.id,
				//"Param" : "Month",
				//"Values" : month,
				"Day" : day,
				"Month" : month,
				"Year" : year !== undefined ? 1900 + year : undefined,
				"Product" : product,
				"Wiki" : document.getElementById('radio_wiki').checked ? true : false,
				'Key' : getCookie("JSESSIONID")
			}
		} else {
			json = {
				"Op" : "getposts",
				"Id" : sessionStorage.id,
				//"Param" : "Month",
				//"Values" : month,
				"Day" : day,
				"Month" : month,
				"Year" : year !== undefined ? 1900 + year : undefined,
				"Wiki" : document.getElementById('radio_wiki').checked ? true : false,
				'Key' : getCookie("JSESSIONID")
			}
		}
		refreshTag = true;
		ws.send(JSON.stringify(json));

	} else {
		month = undefined;
		product = undefined;
		json = {
			"Op" : "getposts",
			"Id" : sessionStorage.id,
			'Key' : getCookie("JSESSIONID"),
			"Wiki" : document.getElementById('radio_wiki').checked ? true : false,
		}

		ws.send(JSON.stringify(json));
	}
}

/*
 * Connects to the server and performs some initialization.
 */
function connect() {
	var css = /* Needle */"#globalgauge path:nth-child(2){ fill:"
			+ needlecolor
			+ " ; stroke-width:0; } #globalgauge circle:nth-child(1){ fill:"
			+ needlecolor + " ; stroke-width:0; }", head = document.head
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
	document.getElementById("Cookie").innerHTML = "Model: "
			+ window.sessionStorage.model + "; PSS: "
			+ window.sessionStorage.pss;

	if (window.location.href.indexOf('https://') != -1) {
		ws = new WebSocket('wss://' + window.location.hostname + ":"
				+ window.location.port + '/Diversity/server');
	} else {
		ws = new WebSocket('ws://' + window.location.hostname + ":"
				+ window.location.port + '/Diversity/server');
	}

	// When the connection is opened, ask the server for the chart configuration
	// settings (gender, location and age segments to be displayed)
	ws.onopen = function() {

		json = {
			"Op" : "getconfig",
			"Id" : sessionStorage.id,
			"Key" : getCookie("JSESSIONID")
		}

		ws.send(JSON.stringify(json));
	};

	ws.onmessage = function(event) {
		json = JSON.parse(event.data);

		// If it's a snapshot, hide the segmentation options
		if (snap) {
			$('#genderfilt').hide();
			$('#agefilt').hide();
			$('#locationfilt').hide();
			$('#finalfilt').hide();
		}

		// If Op is 'Error', display the server message in an overlay window
		if (json[0].Op == "Error") {
			if (snap && json[0].Message != 'Requested Model Not Found') {
				$('#loading')
						.html(
								json[0].Message
										+ '<br><br><button class="btn btn-default" id="ok" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide()">OK</button>');
				$('#overlay').show();
				$('#overlay-back').show();
			}
			return;
		}

		if (json[0].Op == "DBLoading") {
			$('#dbload').show();
		}

		// If the message contains 'Snapshots', build a dropdown with the
		// availiable snapshots to be loaded
		if (json[0] == "Snapshots") {
			snap = true;
			snapshots = json[1];
			displaySnapshots();
		}

		// If Op is 'Configs', set the segmentation options to the ones
		// specified in the message
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
					if (jsonData1[i] !== undefined) {
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
					}
					x.add(option);
				}
			}

			// After the configuration, ask for the opinion extraction (chart)
			// data or the snapshot if the user comes from a snapshot URL

			if (window.location.href.indexOf('snapshot=') != -1) {
				var snapID = window.location.href.split("snapshot=")[1]
						.split("&")[0].replace('%20', ' ');

				snap = true;
				json = {
					"Op" : "load_snapshot",
					"Name" : snapID,
					"Type" : "All",
					'Key' : getCookie("JSESSIONID")
				}
				$('#Cookie').html = 'Snapshot: ' + snap_name;
				name = snap_name;
			} else {
				json = {
					"Op" : "opinion_extraction",
					"Id" : window.sessionStorage.id,
					'Key' : getCookie("JSESSIONID")
				}
			}
			ws.send(JSON.stringify(json));
			return;
		}

		// If Op is 'OE_Redone' and data is available, draw the charts
		if (json[0].Op == "OE_Redone") {
			jsonData = JSON.parse(JSON.stringify(json));
			if (json[1].hasOwnProperty("Error")) {
				if (json[1].Error == "No_data") {
					if(!triedwiki){
							if (json[2].hasOwnProperty('has_wiki') && json[2].has_wiki) {
							document.getElementById('radio_wiki').checked=true;
							triedwiki=true;
							changeRequest('wiki');
							return;
						}
					}

					$('#loading')
							.html(
									'No data to display.<br><br><button class="btn btn-default" id="ok" onclick="location.href = \'index.html\'">OK</button>');
					$('#overlay').show();
					$('#overlay-back').show();
				}
			} else {
				if (jsonData[jsonData.length-2].hasOwnProperty('has_wiki')) {
					if (jsonData[jsonData.length-2].has_wiki == false) {
						$('#radio_wiki_label').hide();
						document.getElementById('radio_social').checked = true;
						//$('#radio_social').click(); //this triggers the onclick event, we dont want that
					} else {
						$('#radio_wiki_label').show();
					}
				} else {
					$('#radio_wiki_label').show();
				}

				if (jsonData[jsonData.length-1].hasOwnProperty('has_social')) {

					if (jsonData[jsonData.length-1].has_social == false) {
						$('#radio_social_label').hide();
						document.getElementById('radio_wiki').checked = true;
						//$('#radio_wiki').click(); //this triggers the onclick event, we dont want that
					} else {
						$('#radio_social_label').show();
					}
				} else {
					$('#radio_social_label').show();
				}
				// console.log("redone");
				drawChart();
				if (snap) {
					if (jsonData[jsonData.length - 1].hasOwnProperty('Date')) {
						var d = jsonData[jsonData.length - 1].Date.split(" ");
						var dateString = d[1] + " " + d[2] + ", " + d[5];
						snap_date = dateString;
					}

					if (jsonData[jsonData.length - 1].hasOwnProperty('User')) {
						snap_user = jsonData[jsonData.length - 1].User;
					}



					if (jsonData[jsonData.length - 1].hasOwnProperty('PSS')) {
						snap_pss = jsonData[jsonData.length - 1].PSS;
					}
					if (document)
					document.getElementById("Cookie").innerHTML = "Snapshot: " + (getParam('snapshot') !== undefined ? getParam('snapshot') : name) + "<br>Created by " + snap_user + " on " + snap_date + "<br>PSS: " + snap_pss;
				} else {
					document.getElementById("Cookie").innerHTML = "Model: "
							+ window.sessionStorage.model + "; PSS: "
							+ window.sessionStorage.pss;
				}
			}

			// Request posts to build the post table
			var json = {
				"Op" : "getposts",
				"Wiki" : document.getElementById('radio_wiki').checked ? true : false,
				"Id" : sessionStorage.id,
				'Key' : getCookie("JSESSIONID")
			}

			ws.send(JSON.stringify(json));
			return;
		}

		// If Op is 'table', build the table with the data from the server
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
			var type;
			switch ($("input[name='radioName']:checked").val()) {
				case 1:
					type = 'All';
					break;
				case 2:
					type = 'Positive';
					break;
				case 3:
					type = 'Negative';
					break;
				default:
					type = 'All';
			}
			// Request the tagcloud for the current user
			if (refreshTag) {
				var json = {
					"Op" : "tagcloud",
					"Id" : sessionStorage.id,
					//"Param" : "Month",
					//"Values" : month,
					"Wiki" : document.getElementById('radio_wiki').checked ? true:false,
					"Day" : day,
					"Month" : month,
					"Year" : year !== undefined ? 1900 + year : undefined,
					"Product" : product != undefined && product != "Global" ? product
							: undefined,
					'Key' : getCookie("JSESSIONID"),
					'User' : user,
					'Type' : type
				}
				ws.send(JSON.stringify(json));
			}
			return;
		}

		// If Op is 'words', build the tag cloud
		if (json[0].Op == "words") {
			makeCloud(json[0].Words);
		}

		// If Op is 'graph', draw the charts
		if (json[0].Op == "graph") {
			jsonData = JSON.parse(JSON.stringify(json));
			drawChart();
			return;
		}
		// If Op is 'comments' display an overlay window with the comments from
		// the selected post
		if (json[0].Op == "comments") {
			clicker();
			return;
		}
	};
}



google.charts.load('current', {
	packages : [ 'corechart', 'bar', 'gauge' ]
});
$(document).ready(function() {
	google.charts.setOnLoadCallback(connect);
});
/*$(window).load(function() {
	$('#overlay').hide();
	$('#overlay-back').hide();
});*/

$(document).ready(function() {
	$('[data-toggle="tooltip"]').tooltip();
});

function goToByScroll(id) { // simple scroll to element
	// Remove "link" from the ID
	id = id.replace("link", "");
	// Scroll
	$('html,body').animate({
		scrollTop : $("#" + id).offset().top - 200
	}, 'ease');
}

function getParam(param) {
	var url = window.location.search.substring(1);
	var params = url.split('&');
	for (var i = 0; i < params.length; i++) {
		var name = params[i].split('=');

		if (name[0] == param) {
			return name[1];
		}
	}
}

// tutorial functions, should be
// refactored?-------------------------------------

function request_tutorial() {
	$('#loading')
			.html(
					"Would you like to see a tutorial for this page?"
							+ '<br><br><button class="btn btn-default" id="yes" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide();start_tutorial();">Yes</button><button class="btn btn-default" id="no" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide();">No</button>');
	$('#overlay').show();
	$('#overlay-back').show();
}

function start_tutorial() {
	var pos = $('#Cookie').offset();
	var h = $('#Cookie').height() + 10;
	var w = $('#Cookie').width();

	$('#tutorial_box').css({
		left : pos.left,
		top : pos.top + h
	});
	$('#tutorial')
			.html(
					'In the Opinion Extraction page you can find data about the selected model. At the top of the page you can see which model you selected and the PSS associated to that model.<br><br><center><button class="btn btn-default" id="next" style="margin-left:5px;" onclick="snapshot_tutorial();">Next</button></center>');

	$('#tutorial_box').toggle();

	goToByScroll('tutorial_box');

}

function snapshot_tutorial() {
	var pos = $('#save').offset();
	var h = $('#save').height() + 10;
	var w = $('#save').width();

	$('#tutorial_box').css({
		left : pos.left,
		top : pos.top + h
	});

	$('#tutorial')
			.html(
					'This is the snapshot menu. Here you can choose to save the data displayed in this page, or load a previously saved snapshot. This allows you to access the data at a specific point in time, without any updates.<br><br><center><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="filter_tutorial();">Next</button></center>');

	goToByScroll('tutorial_box');

}

function filter_tutorial() {
	var pos = $('#genderfilt').offset();
	var h = $('#genderfilt').height() + 10;
	var w = $('#genderfilt').width();

	$('#tutorial_box').css({
		left : pos.left,
		top : pos.top + h
	});
	$('#tutorial')
			.html(
					'This is the filters section. Here you can change the filter and segmentation settings displayed in the charts below.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="snapshot_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="extrapolation_tutorial();">Next</button></center>');

	goToByScroll('tutorial_box');

}

function extrapolation_tutorial() {
	var pos = $('#extrapolate').offset();
	var h = $('#extrapolate').height() + 10;
	var w = $('#extrapolate').width();

	$('#tutorial_box').css({
		left : pos.left,
		top : pos.top + h
	});
	$('#tutorial')
			.html(
					'This checkbox defines whether to extrapolate the results or not. If toggled, the Global Sentiment chart below will display an additional line that represents the extrapolation of the current data for the next 3 months.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="filter_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="opinion_tutorial();">Next</button></center>');

	goToByScroll('tutorial_box');

}

function opinion_tutorial() {
	var pos = $('#opinionpie').offset();
	var h = $('#opinionpie').height() + 10;
	var w = $('#opinionpie').width();

	$('#tutorial_box').css({
		left : pos.left,
		top : pos.top + h
	});
	$('#tutorial')
			.html(
					'This is the total opinions number. It represents the number of posts that were used to generate the sentiment analysis for this model.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="extrapolation_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="polarity_tutorial();">Next</button></center>');

	goToByScroll('tutorial_box');

}

function polarity_tutorial() {
	var pos = $('#polaritybar').offset();
	var h = $('#polaritybar').height() + 10;
	var w = $('#polaritybar').width();

	$('#tutorial_box').css({
		left : pos.left,
		top : pos.top + h
	});
	$('#tutorial')
			.html(
					'The polarity bar chart displays the sentiment distribution over the total number of posts and comments, ranging from \'--\' (negative) to \'++\' (positive).<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="opinion_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="gauge_tutorial();">Next</button></center>');

	goToByScroll('tutorial_box');

}

function gauge_tutorial() {
	var pos = $('#globalgauge').offset();
	var h = $('#globalgauge').height() + 10;
	var w = $('#globalgauge').width();

	$('#tutorial_box').css({
		left : pos.left,
		top : pos.top + h
	});
	$('#tutorial')
			.html(
					'This gauge displays the value of the global sentiment for the PSS associated to this model.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="polarity_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="avg_reach_tutorial();">Next</button></center>');

	goToByScroll('tutorial_box');

}

function avg_reach_tutorial() {
	var pos = $('#reachpie').offset();
	var h = $('#reachpie').height() + 10;
	var w = $('#reachpie').width();

	$('#tutorial_box').css({
		left : pos.left,
		top : pos.top + h
	});
	$('#tutorial')
			.html(
					'Here you can see the average reach value of the current model. Reach is a value that indicates the visibility of the posts about this PSS and it takes into account the number of views, comments and likes.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="gauge_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="reach_tutorial();">Next</button></center>');

	goToByScroll('tutorial_box');

}

function reach_tutorial() {
	var pos = $('#reachline').offset();
	var h = $('#reachline').height() + 10;
	var w = $('#reachline').width();

	$('#tutorial_box').css({
		left : pos.left,
		top : pos.top + h
	});
	$('#tutorial')
			.html(
					'This line chart displays the reach value over time. The time span is 12 months by default, but can be customized in the Chart Setup page. The update frequency, which is the interval between each point in the chart, is defined when creating the model.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="avg_reach_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="global_tutorial();">Next</button></center>');

	goToByScroll('tutorial_box');

}

function global_tutorial() {
	var pos = $('#globalline').offset();
	var h = $('#globalline').height() + 10;
	var w = $('#globalline').width();

	$('#tutorial_box').css({
		left : pos.left,
		top : pos.top + h
	});
	$('#tutorial')
			.html(
					'This line chart displays the global sentiment value over time. Like the reach chart, it has a default time span of 12 months that can be customized in the Chart Setup page, and the update frequency was defined when creating the model. By clicking on any point in this chart, the table below will be updated with posts relative to the date of that point. If the Extrapolate Results checkbox is toggled, this chart displays an additional line that maps the extrapolation values for the next 3 months.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="reach_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="table_tutorial();">Next</button></center>');

	goToByScroll('tutorial_box');

}

function table_tutorial() {
	var pos = $('#table_container').offset();
	var h = $('#table_container').height() + 10;
	var w = $('#table_container').width();

	$('#tutorial_box').css({
		left : pos.left,
		top : pos.top + h
	});
	$('#tutorial')
			.html(
					'The Top 5 table displays the five posts with the highest reach relative to the model\'s PSS. By clicking on any post, you can see all the comments associated to that post. If the global sentiment or reach charts have a point selected, the Top 5 table will display the five posts with highest reach on that point\'s date.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="global_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="tag_tutorial();">Next</button></center>');

	goToByScroll('tutorial_box');

}

function tag_tutorial() {
	var pos = $('#cloud_wrapper').offset();
	var h = $('#cloud_wrapper').height() + 10;
	var w = $('#cloud_wrapper').width();

	$('#tutorial_box').css({
		left : pos.left,
		top : pos.top + h
	});
	$('#tutorial')
			.html(
					'The tag cloud shows the most mentioned words on the users\' posts and comments. The displayed size of each word is related to the number of occurrences, which means that words displayed in a large font size occur more often than words with a smaller font size.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="table_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="end_tutorial();">Next</button></center>');

	goToByScroll('tutorial_box');

}

function end_tutorial() {
	var pos = $('#chart_title').offset();
	var h = $('#chart_title').height();
	var w = $('#chart_title').width();

	$('#tutorial')
			.html(
					'You\'ve reached the end of the tutorial. You can access it at any time by clicking the <i class="fa fa-question-circle" aria-hidden="true"></i> button at the top right corner of the page.<br><br><center><button class="btn btn-default" style="margin-left:5px;" id="end" onclick="$(\'#tutorial_box\').toggle();">Finish</button></center>');

	if (localStorage.tutorial.indexOf("extraction=done") == -1) {
		localStorage.tutorial += "extraction=done;";
	}

	goToByScroll('tutorial_box');
}

// ------------------------------------------------------------------------------

var clickedWord = "";
$(document).bind("contextmenu", function(event) { // override right click
	if ($(event.target).is(".word")) {
		event.preventDefault(); // avoid browser default
		$(".custom-menu").finish().toggle(100).css({
			top : event.pageY - 50 + "px",
			left : event.pageX - 50 + "px"
		});
		clickedWord = event.target.text;
	} else {
	}
});

$(document).bind("mousedown", function(e) {
	// If the clicked element is not the menu
	if (!$(e.target).parents(".custom-menu").length > 0) {
		// Hide it
		$(".custom-menu").hide(100);
	}
});

// If the menu element is clicked
$(".custom-menu li").click(function(e) {

	// triggers data-action, defined in the HTML
	switch ($(this).attr("data-action")) {

	case "ignore_word": // the only defined action
		ignore_words(clickedWord);
		break;
	}
	$(".custom-menu").hide(100);
});

function ignore_words(word) { // sends a message to start ignoring the word we
	// clicked on
	var json = {
		'Op' : 'set_ignore_word',
		"Id" : sessionStorage.id,
		'Word' : word,
		'User' : user,
		'Key' : getCookie("JSESSIONID"),
		'Wiki' : document.getElementById('radio_wiki').checked
	}

	ws.send(JSON.stringify(json));
}
/*
 * NEW TAG CLOUD - CLICK NOT WORKING function makeCloud(words) {
 * $('#cloud').html(''); var cloud = []; for (var i = 0; i < words.length; i++) {
 * var currWord = words[i].word; cloud[i] = { text: currWord, weight:
 * words[i].frequency, handlers: {click: function() { var word = currWord;
 * return tagClick(word); } } }; }
 *
 * $('#cloud').jQCloud(cloud); }
 */
function makeCloud(words) {
	var str = '';
	var word_counter = 0;
	var avg_frequency=0;
	for(var i=0; i < words.length;i++){
		avg_frequency+=words[i].frequency;
	}
	avg_frequency = avg_frequency/words.length;


	for (var i = 0; i < words.length; i++) {
		if(words[i].frequency < avg_frequency) continue;
		str += '<a class=\'word\' onclick=\'tagClick("' + words[i].word
				+ '");\' rel=' + words[i].frequency + '>' + words[i].word
				+ '</a>';
		if (word_counter > 5) {
			str += "<br>"
			word_counter = 0;
		}
	}

	$('#cloud').html(str);

	$.fn.tagcloud.defaults = {
		size : {
			start : 12,
			end : 30,
			unit : 'pt'
		},
		color : {
			start : '#ADADAD',
			end : '#604460'
		}
	};
	word_counter++;
	$('#cloud a').tagcloud();
}

function tagClick(word) {
	var json = {
		"Op" : "getposts",
		"Id" : sessionStorage.id,
		"word" : word,
		//"Param" : "Month",
		//"Values" : month,
		"Day" : day,
		"Month" : month,
		"Year" : year !== undefined ? 1900 + year : undefined,
		"Product" : product != undefined ? product : undefined,
		"Wiki" : document.getElementById('radio_wiki').checked,
		'Key' : getCookie("JSESSIONID")
	}
	refreshTag = false;
	ws.send(JSON.stringify(json));
}

/*
 * Displays an overlay window to save a new snapshot.
 */
function save() {
	var code = '<center><b>Save snapshot</b></center><br><label for="snap_name">Name: </label><input id="snap_name" type="text" placeholder="Snapshot name..."><br><br><button class="btn btn-default" id="save" onclick="send($(\'#snap_name\').val());$(\'#overlay\').hide();$(\'#overlay-back\').hide()">Save</button> <button class="btn btn-default" id="cancel" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide()">Cancel</button>';
	$('#loading').html(code);
	$('#overlay').show();
	$('#overlay-back').show();
}

/*
 * Sends a message requesting a list of snapshots.
 */
function load() {
	// send request for snapshot list
	var json = {
		"Op" : "load_snapshot",
		"Type" : "Extraction",
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
		"type" : "Extraction",
		"Wiki" : document.getElementById('radio_wiki').checked ? true : false,
		"name" : val,
		"creation_date" : new Date(),
		"timespan" : 12,
		"user" : user,
		"Id" : sessionStorage.id,
		'Key' : getCookie("JSESSIONID")
	}
	ws.send(JSON.stringify(json));
}

/*
 * Builds a dropdown list of availiable snapshots and displays them in an
 * overlay window to be loaded.
 */
function displaySnapshots() {
	var code = '<center><b>Load snapshot</b></center><br><label for="snap_name">Select a snapshot: </label><select id="select_snap" style="margin-left:15px;"></select><br><br><button class="btn btn-default" id="sel_btn" onclick="requestSnapshot($(\'#select_snap\').find(\':selected\').text());$(\'#overlay\').hide();$(\'#overlay-back\').hide()">Load</button> <button class="btn btn-default" id="cancel" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide()">Cancel</button>';
	$('#loading').html(code);
	for (var i = 0; i < snapshots.length; i++) {
		$('#select_snap').append($('<option>', {
			value : snapshots[i].Name,
			text : snapshots[i].Name
		}));
	}
	$('#overlay').show();
	$('#overlay-back').show();
}

/*
 * Sends a message requesting a specific snapshot to be loaded.
 */
function requestSnapshot(val) {
	name = val;
	var json = {
		"Op" : "load_snapshot",
		"Name" : val,
		"Type" : "All",
		'Key' : getCookie("JSESSIONID")
	}
	snap = true;
	ws.send(JSON.stringify(json));
}


$(document).keyup(function(e) {
	//console.log(e.keyCode);
	if (e.keyCode == 27) {
		//console.log("ESCAPE!");
		if (document.getElementById('displaybox').style.display != "none") {
			return clicker();
		}
	}
});

/*
 * Detects a table click and displays an overlay window with comments from the
 * selected post.
 */
function clicker(hidden) {
	var thediv = document.getElementById('displaybox');
	var embedCode = '<div style="background-color: #ffffff; width:75%; height:45%" align="right"><a href="#" align="left" onclick="return clicker();"><i class="fa fa-window-close fa-2x" aria-hidden="true"></i></a><iframe width="100%" height="95%" src="comments.html?id='
			+ hidden + ' frameborder="0" seamless="seamless" allowfullscreen="no"></iframe></div>';
	if (thediv.style.display == "none") {
		thediv.style.display = "";
		thediv.innerHTML = "<script>$(document).keyup(function(e) {if (e.keyCode == 27) { return clicker(); }})</script><table width='100%' height='100%'><tr><td align='center' valign='bottom' width='80%' height='80%'>"
				+ "<param name='bgcolor' value='#000000'>"
				+ embedCode
				+ "</tr><tr align='center' valign='top' width='10%' height='10%'><td><center></center></td></tr></table>";
		// thediv.innerHTML = ""++ "<button onclick='clicker()' id='closepage'
		// class='btn btn-default'>Close Page</button>";
	} else {
		thediv.style.display = "none";
		thediv.innerHTML = '';
	}
	return false;
}

/*
 * Draws all the charts with the opinion extraction data.
 */
function drawChart() {
	// Top Left
	var data = new google.visualization.DataTable();
	var i = 1;
	data.addColumn('string', 'Param');
	if (jsonData[i].Graph == "Top_Left") {
		triedwiki=false;
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
			backgroundColor : {
				fill : 'transparent'
			},
		};

		top_left.draw(data, options);
	}

	// Top Middle
	if (jsonData[i].Graph == "Top_Middle") {
		var data = new google.visualization.DataTable();
		data.addColumn('string', 'Polarity')
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
			backgroundColor : {
				fill : 'transparent'
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
			if (jsonData[i].Value != -1) {
				data.setCell(ii, 1, jsonData[i].Value)
			}
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
			backgroundColor : {
				fill : 'transparent'
			},
		};

		if (!document.getElementById('radio_wiki').checked) {
			document.getElementById('reachpie').style.display = 'block';
			$('#reachpie').show();
				bottom_left.draw(data, options);
		} else {
			document.getElementById('reachpie').style.display = 'none';
			$('#reachpie').hide();
		}
	}

	// Bottom Middle
	var mid_data;
	if (jsonData[i].Graph == "Bottom_Middle") {

		var data = new google.visualization.DataTable();
		data.addColumn('date', 'Date');

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
				var time = jsonData[i].Date.split(" ");
				if (jsonData[i].Value != -1 ) {

					data.setCell(ii, 0, new Date(time[2] + "/" + time[1] + "/"
							+ time[0])); // month comes as a number from
					// server, if it changes use
					// getMonthFromString
					data.setCell(ii, filt, jsonData[i].Value)
				} else {
					data.setCell(ii, 0, new Date(time[2] + "/" + time[1] + "/"
							+ time[0]));
				}
			}
		}

		colors = new Array();
		for (var color = 1; color < filt; color++) {
			colors.push(chartcolor(data.getColumnLabel(color)));
		}
		finalProductColors = colors;
		function midSelectHandler() {
			var selection = bottom_middle.getSelection()[0];
			var row = selection.row;
			var col = selection.column;
			console.log(sentimentdata.getValue(row, 0));
			var selectedItem = bottom_middle.getSelection()[0] != undefined ? bottom_middle
					.getSelection()[0]
					: false;
			if (selectedItem) {
				if ((selectedItem.row != null && bottom_right.getSelection()[0] == undefined)
						|| (selectedItem.row != null && selectedItem.row != bottom_right
								.getSelection()[0].row)) {
					bottom_right.setSelection([ {
						column : selectedItem.column,
						row : selectedItem.row
					} ]);
					//google.visualization.events.trigger(bottom_right, 'select');
					sentimentdata = data;
					getPosts();
				}
			} else {
				bottom_right.setSelection([]);
			}
		}
		/*
		 * function midSelectHandler() { var selectedItem =
		 * bottom_middle.getSelection()[0]; if (selectedItem) { if
		 * (bottom_right.getSelection()[0] == undefined || selectedItem.row !=
		 * bottom_right.getSelection()[0].row) {
		 * bottom_right.setSelection([{column:selectedItem.column,
		 * row:selectedItem.row}]);
		 * google.visualization.events.trigger(bottom_right, 'select'); } } }
		 */
		var start = new Date(localStorage.start_date).toDateString() != "Invalid Date" ? new Date(
				localStorage.start_date)
				: 0;
		var end = new Date(localStorage.end_date).toDateString() != "Invalid Date" ? new Date(
				localStorage.end_date)
				: 0;

		var mid_options = {
			hAxis : {
				showTextEvery : 1,
				textStyle : {
					fontSize : 8
				},
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
			backgroundColor : {
				fill : 'transparent'
			},
			legend : {
				maxLines : 5,
				position : 'bottom'
			},
			explorer : {
				axis : 'horizontal',
				keepInBounds : true,
				maxZoomIn : 4.0,
				maxZoomOut : 1
			},
			pointsVisible : (localStorage.showPoints != undefined && localStorage.showPoints == 'true') ? true
					: false,
		};

		if (start != 0 && end != 0) {
			mid_options.hAxis.viewWindow = {
				min : start,
				max : end
			}
		} else if (start != 0 && end == 0) {
			mid_options.hAxis.viewWindow = {
				min : start
			}
		} else if (start == 0 && end != 0) {
			mid_options.hAxis.viewWindow = {
				max : start
			}
		}

		google.visualization.events.addListener(bottom_middle, 'select',
				midSelectHandler);

		// google.visualization.events.addListener(bottom_middle, 'select',
		// midSelectHandler);
		mid_data = data;
		if (!document.getElementById('radio_wiki').checked) {
			document.getElementById('reachline').style.display = 'block';
			$('#reachline').show();
			bottom_middle.draw(data, mid_options);
		} else {
			document.getElementById('reachline').style.display = 'none';
			$('#reachline').hide();
		}
	}

	function getCoordsMid() {
		var chartLayout = bottom_middle.getChartLayoutInterface();
		var chartBounds = chartLayout.getChartAreaBoundingBox();
		return {
			x : {
				min : chartLayout.getHAxisValue(chartBounds.left),
				max : chartLayout.getHAxisValue(chartBounds.width
						+ chartBounds.left)
			},
		/*
		 * y: { min: chartLayout.getVAxisValue(chartBounds.top), max:
		 * chartLayout.getVAxisValue(chartBounds.height + chartBounds.top) }
		 */
		};
	}

	function setRangeMid(coords) {
		mid_options.hAxis.viewWindow = {};
		// options.vAxis.viewWindow = {};
		if (coords) {
			mid_options.hAxis.viewWindow.min = coords.x.min;
			mid_options.hAxis.viewWindow.max = coords.x.max;
			// options.vAxis.viewWindow.min = coords.y.min;
			// options.vAxis.viewWindow.max = coords.y.max;
		}
		bottom_middle.draw(data, mid_options);
	}

	var right_data;
	// Bottom Right
	if (jsonData[i].Graph == "Bottom_Right"
			|| jsonData[i].Graph == "Bottom_Right_Ex") {

		var date = new Date();
		var locale = "en-us";
		var month = date.toLocaleString(locale, {
			month : "short"
		}).toUpperCase;

		var first = -1;
		var prev = -1;
		var ext = false;
		var trigger = false;
		var series = [];
		var randomYear = 2000;
		var columns = [];

		sentimentdata = new google.visualization.DataTable();
		sentimentdata.addColumn('date', 'Date');
		columns.push('Date');
		var count = 0;
		for (filt = 1; i < jsonData.length
				&& (jsonData[i].Graph == "Bottom_Right" || jsonData[i].Graph == "Bottom_Right_Ex"); filt++) {
			var name = jsonData[i].Filter;
			if (jsonData[i].Graph == "Bottom_Right") {
				if (columns.indexOf(name) == -1) {
					sentimentdata.addColumn('number', name, name);
					columns.push(name);
				}
			}
			i++;

			for (ii = 0; i < jsonData.length
					&& (jsonData[i].Graph == 'Bottom_Right')
					&& !jsonData[i].hasOwnProperty('Filter'); ii++, i++) {
				var time = jsonData[i].Date.split(" ");
				if (filt == 1) {
					sentimentdata.addRow();
					count++;
				}

				if (jsonData[i].Value != -1) {
					if (jsonData[i].Graph == 'Bottom_Right') {
						sentimentdata.setCell(ii, 0, new Date(time[2],
								time[1] - 1, time[0]));
						sentimentdata.setCell(ii, filt, jsonData[i].Value);

					}
				} else {
					sentimentdata.setCell(ii--, 0, new Date(time[2] +"/"+ time[1] - 1+"/"+
							time[0]));

				}

			}
			var time2;
			for (var iii = count; i < jsonData.length
					&& (jsonData[i].Graph == 'Bottom_Right_Ex')
					&& !jsonData[i].hasOwnProperty('Filter'); iii++, ii++, i++) {
				if (jsonData[i].Graph == 'Bottom_Right_Ex') {
					if (jsonData[i].hasOwnProperty('Date')) {
						time2 = jsonData[i].Date.split(" ");
					}
					if (columns.indexOf('Extrapolation for ' + name) == -1) {
						sentimentdata.addColumn('number', 'Extrapolation for '
								+ name, 'Extrapolation for ' + name);
						columns.push('Extrapolation for ' + name);
						series.push(sentimentdata.getNumberOfColumns() - 2);
					}
					if (jsonData[i].Value != -1) {
						sentimentdata.addRow();
						sentimentdata.setCell(iii, 0, new Date(time2[2],
							time2[1] - 1, time2[0]));

						sentimentdata.setCell(iii, filt, jsonData[i].Value);
					}
				}
			}
		}

		colors = new Array();
		for (var color = 1; color < filt; color++) {
			colors.push(chartcolor(sentimentdata.getColumnLabel(color)));
		}

		function rightSelectHandler() {
			var selectedItem = bottom_right.getSelection()[0] != undefined ? bottom_right
					.getSelection()[0]
					: false;
			if (!document.getElementById('radio_wiki').checked) {
				if (selectedItem) {
					if ((selectedItem.row != null && bottom_middle.getSelection()[0] == undefined)
							|| (selectedItem.row != null && selectedItem.row != bottom_middle
									.getSelection()[0].row)) {
						bottom_middle.setSelection([ {
							column : selectedItem.column,
							row : selectedItem.row
						} ]);

					}
				} else {
					bottom_middle.setSelection([]);
				}
			}
			getPosts();

		}

		/*
		 * function rightSelectHandler() { var selectedItem =
		 * bottom_right.getSelection()[0]; if (selectedItem) { if
		 * (bottom_middle.getSelection()[0] == undefined || selectedItem.row !=
		 * bottom_middle.getSelection()[0].row) {
		 * bottom_middle.setSelection([{column:selectedItem.column,
		 * row:selectedItem.row}]);
		 * google.visualization.events.trigger(bottom_middle, 'select'); } } }
		 */

		var right_options = {
			hAxis : {
				showTextEvery : 1,
				textStyle : {
					fontSize : 8
				},
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
			backgroundColor : {
				fill : 'transparent'
			},
			legend : {
				maxLines : 5,
				position : 'bottom'
			},
			explorer : {
				axis : 'horizontal',
				keepInBounds : true,
				maxZoomIn : 4.0,
				maxZoomOut : 1,
			},
			pointsVisible : (localStorage.showPoints != undefined && localStorage.showPoints == 'true') ? true
					: false,
		};

		for (var v = 0; v < series.length; v++) {
			right_options["series"][series[v]] = {
				lineDashStyle : [ 4, 4 ]
			}
		}

		if (!extra) {
			if (start != 0 && end != 0) {
				right_options.hAxis.viewWindow = {
					min : start,
					max : end
				}
			} else if (start != 0 && end == 0) {
				right_options.hAxis.viewWindow = {
					min : start
				}
			} else if (start == 0 && end != 0) {
				right_options.hAxis.viewWindow = {
					max : start
				}
			}
		}
		//google.visualization.events.addListener(bottom_right, 'select',
		//		rightSelectHandler);

		google.visualization.events.addListener(bottom_right, 'select', rightSelectHandler);
		bottom_right.draw(sentimentdata, right_options);
		right_data = sentimentdata;
		function getCoordsRight() {
			var chartLayout = bottom_right.getChartLayoutInterface();
			var chartBounds = chartLayout.getChartAreaBoundingBox();
			return {
				x : {
					min : chartLayout.getHAxisValue(chartBounds.left),
					max : chartLayout.getHAxisValue(chartBounds.width
							+ chartBounds.left)
				},
			/*
			 * y: { min: chartLayout.getVAxisValue(chartBounds.top), max:
			 * chartLayout.getVAxisValue(chartBounds.height + chartBounds.top) }
			 */
			};
		}

		function setRangeRight(coords) {
			right_options.hAxis.viewWindow = {};
			// options.vAxis.viewWindow = {};
			if (coords) {
				right_options.hAxis.viewWindow.min = coords.x.min;
				right_options.hAxis.viewWindow.max = coords.x.max;
				// options.vAxis.viewWindow.min = coords.y.min;
				// options.vAxis.viewWindow.max = coords.y.max;
			}
			bottom_right.draw(right_data, right_options);
		}

		// google.visualization.events.addListener(bottom_right,'scroll',
		// function(){setRangeMid(getCoordsRight())});
		// google.visualization.events.addListener(bottom_middle,'scroll',
		// function(){setRangeRight(getCoordsMid())});
	}
	$('#overlay').fadeOut(2000);
	$('#overlay-back').fadeOut(2000);
	window.clearInterval(loadingtimer);

	if (!loaded) {
		if (localStorage.tutorial != undefined
				&& localStorage.tutorial.indexOf("extraction=done") == -1) { // if
																				// the
																				// user
																				// never
																				// opened
																				// this
																				// page,
																				// start
																				// the
																				// tutorial
			request_tutorial();
		}
		if (localStorage.tutorial == undefined) {
			localStorage.tutorial += "";
			request_tutorial();
		}
		loaded = !loaded;
	}

	if (document.getElementById('radio_wiki').checked) {
		$('#reachline').hide();
		$('#reachpie').hide();
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

function getMonthFromString(mon) {
	return new Date(Date.parse(mon + " 1, 2012")).getMonth();
}

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

function changeRequest(type) {
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
			"Name" : name,
			"Id" : sessionStorage.id,
			'Key' : getCookie("JSESSIONID"),
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
			"Op" : document.getElementById('radio_wiki').checked ? "oe_wiki" : "oe_refresh",// OE_Filter
			"Param" : "",
			"Values" : "",
			"Filter" : "",
			"Id" : sessionStorage.id,
			"Extrapolate" : extrapolate ? 1 : undefined,
			'Key' : getCookie("JSESSIONID")
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
	 * needlecolor="#5C6E0E" json.Filter="Age"; } }
	 */

	var css = /* Needle */"#globalgauge path:nth-child(2){ fill:"
			+ needlecolor
			+ " ; stroke-width:0; } #globalgauge circle:nth-child(1){ fill:"
			+ needlecolor + " ; stroke-width:0; }", head = document.head
			|| document.getElementsByTagName('head')[0], style = document
			.createElement('style');

	style.type = 'text/css';
	if (style.styleSheet) {
		style.styleSheet.cssText = css;
	} else {
		style.appendChild(document.createTextNode(css));
	}
	head.appendChild(style);
	$('#loading')
			.html(
					'<i class="fa fa-spinner fa-3x fa-spin" aria-hidden="true"></i><br>Loading, please wait...');
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

function socialPosts() {
	changeRequest("");
}

function wikiPosts() {
	changeRequest("wiki");
}

function requestTagcloud(polarity) {
	requestTag = true;
	var json = {
		"Op" : "tagcloud",
		"Id" : sessionStorage.id,
		//"Param" : "Month",
		//"Values" : month,
		"Wiki" : document.getElementById('radio_wiki').checked ? true : false,
		"Day" : day,
		"Month" : month,
		"Year" : year !== undefined ? 1900 + year : undefined,
		"Product" : product != undefined && product != "Global" ? product
				: undefined,
		'Key' : getCookie("JSESSIONID"),
		'User' : user,
		'Type' : polarity
	};
	//console.log('requested ' + polarity + ' tagcloud');
	//console.log(JSON.stringify(json))
	ws.send(JSON.stringify(json));
}

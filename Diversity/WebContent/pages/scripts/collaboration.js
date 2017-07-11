var draw = false;
var ws;
var str = "";
var start = true;
var chartData;
var users;
var jsonData; /* = [{"Op":"Tree"},{"Id":1,"Name":"Morris Ground 1"},{"Id":2,"Name":"Austin Basket"},{"Id":3,"Name":"Austin Soccer"},{"Id":4,"Name":"Morris Sea 1000"},{"Id":5,"Name":"Morris Sea 2099"},{"Id":6,"Name":"Morris Wind"},{"Id":7,"Name":"Austin Polo"},{"Id":8,"Name":"Austin Cricket"},{"Id":9,"Name":"Austin XC"},{"Id":10,"Name":"Austin Base"},{"Products":[{"Products":[{"Products":[{"Products":[{"Id":21,"Name":"21"}],"Id":20,"Name":"20"}],"Id":19,"Name":"19"}],"Id":18,"Name":"18"}],"Id":11,"Name":"Sole Machine"},{"Id":12,"Name":"Sewing Machine"},{"Products":[{"Id":14,"Name":"Rubber"},{"Id":15,"Name":"Aluminium"}],"Id":13,"Name":"Cleat Applier"},{"Id":16,"Name":"Glueing Machine"},{"Id":17,"Name":"Neoprene Cutting Machine"}];
*/
var count; // for timespan
var snapshots;
var snap = false;
var snap_name;
function getCookie(name) { //not being used
	  var value = "; " + document.cookie;
	  var parts = value.split("; " + name + "=");
	  if (parts.length == 2)
	    return parts.pop().split(";").shift();
	}
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
    json = {
      "Op" : "collaboration",
      'Key' : getCookie("JSESSIONID"),
			'Products' : getParam('products').replace(/,/g, ';'),
			'Services' : getParam('services').replace(/,/g, ';')
    }

    ws.send(JSON.stringify(json));
  }

  ws.onmessage = function(event) {
    var json = JSON.parse(event.data.replace(/\\/g,''));

		if (localStorage.tutorial !=undefined && localStorage.tutorial.indexOf("collaboration=done") == -1) { // if the user never opened this page, start the tutorial
			request_tutorial();
		}
		if (localStorage.tutorial == undefined) {
			localStorage.tutorial += "";
			request_tutorial();
		}
    //If the message Op is 'collaboration', draw the team composition table
    if (json[0].Op == "collaboration") {
      draw = true;
			users = json;
      console.log(users);

      drawTable('');
    }

    //If the message Op is 'Error', it contains a message from the server, which is displayed in an overlay box
    if (json[0].Op == "Error") {
      if (json[0].hasOwnProperty("Message")) {
        $('#overlay-back').show();
        $('#overlay').show();
        $('#error').html(json[0].Message + '<br>' + '<input id="submit" class="btn btn-default" onclick="$(\'#overlay-back\').hide();$(\'#overlay\').hide();" style="margin-top:20px" type="submit" value="OK" />');
      }
    }
  }
});

function request_tutorial() {
  $('#error').html("Would you like to see a tutorial for this page?" + '<br><br><button class="btn btn-default" id="yes" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide();start_tutorial();">Yes</button><button class="btn btn-default" id="no" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide();">No</button>');
  $('#overlay').show();
  $('#overlay-back').show();
}

function start_tutorial() {
  end_tutorial();
  $('#tutorial_box').toggle();
}

function end_tutorial() {
  var pos=$('#submit').offset();
  var h=$('#submit').height() + 10;
  var w=$('#submit').width();

  $('#tutorial').html('You\'ve reached the end of the tutorial. You can access it at any time by clicking the <i class="fa fa-question-circle" aria-hidden="true"></i> button at the top right corner of the page.<br><br><center><button class="btn btn-default" style="margin-left:5px;" id="end" onclick="$(\'#tutorial_box\').toggle();">Finish</button></center>');

  if (localStorage.tutorial.indexOf("collaboration=done") == -1) {
    localStorage.tutorial += "collaboration=done;";
  }
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
* Draws a table with users for collaboration
*/
function drawTable(filter) {
	// | USER_NAME | USER_ROLE | USER_COMPANY | COMPANY_TYPE | USER_RATING |


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

var lastValue = '';
$("#filter").on('change keyup paste mouseup', function() {
    if ($(this).val() != lastValue) {
        lastValue = $(this).val();
        console.log(lastValue);
    }
});

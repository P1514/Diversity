$(window).on('load', function() {
  if (document.cookie.indexOf('JSESSIONID') == -1) {
    document.cookie = (Math.random().toString(36)+'00000000000000000').slice(2, 15+2);
  }
});

var ws;
var json;
var jsonData;
var dplist;
var table;

function getCookie(name) { //not being used
  var value = "; " + document.cookie;
  var parts = value.split("; " + name + "=");
  if (parts.length == 2)
    return parts.pop().split(";").shift();
}

if (window.location.href.indexOf('https://') != -1) {
  ws = new WebSocket('wss://' + window.location.hostname + ":"
      + window.location.port + '/Diversity/server');
} else {
  ws = new WebSocket('ws://' + window.location.hostname + ":"
      + window.location.port + '/Diversity/server');
}

ws.onopen = function() {

  if (sessionStorage.userKey === undefined) {
    sessionStorage.userKey = Math.floor(Math.random() * 100000000);
  }

	if(getCookie("Developer") == "Guilherme") sessionStorage.session="DESIGNER";

  if (document.cookie.indexOf('JSESSIONID') == -1) {
    document.cookie = 'JSESSIONID = ' + (Math.random().toString(36)+'00000000000000000').slice(2, 15+2);
  }
  var json2 = {
    'Op' : 'get_rules',
    'dp' : '-1',
    'Key' : getCookie("JSESSIONID")
  };

  ws.send(JSON.stringify(json2));
};


ws.onmessage = function(event) {
  json = JSON.parse(event.data);
  if (json[0].Op == 'Error') {
    // alert(json[0].Message);
	$('#alert').html(json[0].Message + '<br><br><button class="btn btn-default" id="ok" onclick="location.href = \'index.html\'">OK</button>');
	$('#overlay').show();
	$('#overlay-back').show();
  }
  if (json[0].Op == 'rules') {
    buildTable();
  }
};

function getRules(dp) {
  var e = document.getElementById("dp_list");
  var str = e.options[e.selectedIndex].text;
  var json2 = {
    'Op' : 'get_rules',
    'dp' : str,
    'Key' : getCookie("JSESSIONID")
  };

  ws.send(JSON.stringify(json2));
}

function buildTable() {
  for (var i = 1; i < json.length; i++) {
    $('#ldr').append('<tr><td style = "text-align: left;padding-right: 20px;">' + json[i].Rule + '</td><td style = "text-align : left;padding-right: 20px;">' + json[i].Projects.length + '</td><td style = "text-align : left;padding-right: 20px;">' + json[i].Score);
  }
}

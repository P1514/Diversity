var pss;
var snapshots;

$(window).load(function() {
  $('#overlay').hide();
  $('#overlay-back').hide();
});

$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip();
});

function getCookie(name) {
  var value = "; " + document.cookie;
  var parts = value.split("; " + name + "=");
  if (parts.length == 2)
    return parts.pop().split(";").shift();
}

if(window.location.href.indexOf('pss=') != -1) {
  pss = window.location.href.split('pss=')[1].split('?')[0].replace('%20', ' ');
  console.log(pss);
}

if (window.location.href.indexOf('https://') != -1) {
  ws = new WebSocket('wss://' + window.location.hostname + ":"
      + window.location.port + '/Diversity/server');
} else {
  ws = new WebSocket('ws://' + window.location.hostname + ":"
      + window.location.port + '/Diversity/server');
}

ws.onopen = function() {
  var msg = {
    'Op' : 'load_snapshot',
    'Key' : getCookie("JSESSIONID"),
    'PSS' : pss,
    'Type' : '',
  }
  ws.send(JSON.stringify(msg));
}
ws.onmessage = function(event) {
  json = JSON.parse(event.data);

  if (json[0] == 'Snapshots' ) {
    snapshots = json[1];
    displaySnapshots();
  }
}

function displaySnapshots() {
  for (var i=0; i < snapshots.length; i++) {
    console.log(snapshots[i].Name);
    var button = document.createElement('button');
    button.setAttribute("id", "snap_box_" + i);
    button.setAttribute("class", "btn btn-default text-left");
    button.setAttribute("onclick", "window.location.href='opinion_extraction_page.html?snapshot=" + snapshots[i].Name + "'");
    button.setAttribute("type", "submit");
    button.innerHTML = ("value", "<b>Name: </b>" + snapshots[i].Name);
    button.setAttribute("style", "text-align:left;border-radius: 0; width: 90%; margin-bottom: 5px;margin-top:10px;box-shadow: 2px 2px 5px 2px rgba(0, 0, 0, .1);");

    $('#snapshot_list').append(button);
  }

}

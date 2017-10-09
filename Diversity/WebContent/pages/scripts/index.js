google.charts.load('current', {packages:['corechart','controls']});
if (window.location.href.indexOf('https://') != -1) {
  ws = new WebSocket('wss://' + window.location.hostname + ":"
      + window.location.port + '/Diversity/server');
} else {
  ws = new WebSocket('ws://' + window.location.hostname + ":"
      + window.location.port + '/Diversity/server');
}
var json;
var i = 0;
var middle;
var jsonData;
var newData;
var all_models = [];
var timespan;

$(window).on('load', function() {
  if (document.cookie.indexOf('JSESSIONID') == -1) {
    document.cookie = (Math.random().toString(36)+'00000000000000000').slice(2, 15+2);
  }
});

/*
 * Hides and displays certain UI elements according to the current user's access
 * rights.
 */
function giveAcessRights(json){
      if(json[0].view_OM){
      document.getElementById("view").style.display = 'block';// show
      document.getElementById("view_gray").style.display = 'none';// hide
      }

      else{
      document.getElementById("view_gray").style.display = 'block';// show
      document.getElementById("edit").style.display =  'none';// hide
      }

      if(json[0].create_edit_delete_model){

      document.getElementById("create").style.display = 'block';// show
      document.getElementById("create_gray").style.display = 'none';// hide
      document.getElementById("edit").style.display = 'block';// show
      document.getElementById("edit_gray").style.display = 'none';// hide
      document.getElementById("delete").style.display = 'block';// show
      document.getElementById("delete_gray").style.display = 'none';// hide

      }
      else{
      document.getElementById("create").style.display =  'none';// hide
      document.getElementById("create_gray").style.display = 'block';// show
      document.getElementById("edit").style.display =  'none';// hide
      document.getElementById("edit_gray").style.display = 'block';// show
      document.getElementById("delete").style.display =  'none';// hide
      document.getElementById("delete_gray").style.display = 'block';// show

      }

      if(json[0].view_opinion_results){
      document.getElementById("_view").style.display = 'block';// show
      document.getElementById("_view_gray").style.display = 'none';// hide

      }
      else{
      document.getElementById("_view_gray").style.display = 'block';// show
      document.getElementById("_view").style.display = 'none';// hide

      }

      if(json[0].view_use_opinion_prediction){
      document.getElementById("predict").style.display = 'block';// show
      document.getElementById("predict_gray").style.display = 'none';// hide
      document.getElementById("create_prediction").style.display = 'block';
      }
      else{
      document.getElementById("predict").style.display = 'none';// hide
      document.getElementById("predict_gray").style.display = 'block';// show

      }

      if(json[0].save_delete_snapshots){
        // not implemented yet
      }
      else{

      }

      if(!json[0].create_edit_delete_model && !json[0].view_OM ){
          document.getElementById("_define").style["background-color"]= "#666666";
          // console.log('test');
        }
        else{

        }
}

/*
 * Extracts the user role from the page URL
 */
function getRole(){
  var url = window.location.href.toString();
  var type = url.split("role_desc=")
      var role;
      document.getElementById("dropdown").style.display = 'none';// hides
																	// dropdown
       if(typeof type[1] != 'undefined'){
          type = type[1].split("&");
          role = type[0];
      }

      if(typeof role == 'undefined'){ // change to == null
          role = sessionStorage.session;
      }
  else
          {
              sessionStorage.session = role;
          }

          var jsonData = {
      "Op" : 'getrestrictions',// create or update
      "Role": role==null?'no_role':role,
      'Key' : getCookie("JSESSIONID")
    };
      ws.send(JSON.stringify(jsonData));

      localStorage.user = url.split("user_id=")[1].split("&")[0];
}

function getCookie(name) {
  var value = '; ' + document.cookie;
  var parts = value.split('; ' + name + '=');
  if (parts.length == 2)
    return parts.pop().split(';').shift();
}

function setCookie() {
  // document.cookie='Product=; expires=Thu, 01-Jan-1970 00:00:01 GMT;';
// document.cookie = 'Model='
      + $('#Models :selected').text();
      var cookiemonster = document.getElementById('Models').value.split(';');
  // document.cookie = 'Id=' + cookiemonster[0];
  // document.cookie = 'PSS=' + cookiemonster[1];
  sessionStorage.model=$('#Models :selected').text();
  sessionStorage.id=cookiemonster[0];
  sessionStorage.pss=cookiemonster[1];
}

function setCookie2(name, id, pss) {
  sessionStorage.model = name;
  sessionStorage.id = id;
  sessionStorage.pss = pss;
}

// dev only feature - removes the need to set the user type every time
ws.onopen = function() {

  if (sessionStorage.userKey == undefined) {
    sessionStorage.userKey = Math.floor(Math.random() * 100000000);
  }

	if(getCookie("Developer") == "Guilherme") sessionStorage.session="DEVELOPER";
  getRole();

  if (document.cookie.indexOf('JSESSIONID') == -1) {
    document.cookie = 'JSESSIONID = ' + (Math.random().toString(36)+'00000000000000000').slice(2, 15+2);
  }

}


ws.onmessage = function(event) {
  json = JSON.parse(event.data);
  if (json[0].Op == 'Error') {
    // alert(json[0].Message);
	$('#alert').html(json[0].Message + '<br><br><button class="btn btn-default" id="ok" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide()">OK</button>');
	$('#overlay').show();
	$('#overlay-back').show();
  }
  if (json[0].Op == 'Models') {
    populatePSS();
    var jsonData = {
      'Op' : 'Top5Reach',
      'PSS' : localStorage.pss != "" ? localStorage.pss : undefined,
      'Start_date' : localStorage.start_date != "" ? localStorage.start_date : undefined,
      'End_date' : localStorage.end_date != "" ? localStorage.end_date : undefined,
    	"Key" : getCookie("JSESSIONID")
    }
    timespan
    ws.send(JSON.stringify(jsonData));
  }

  // If Op is 'Graph' and there is data to display, draw the global sentiment
	// chart
  if (json[0].Op == 'Graph') {
    if (!json[1].hasOwnProperty('Graph')) {
      newData = JSON.parse(JSON.stringify(json));
      google.charts.setOnLoadCallback(drawChart);
    } else {
      console.log("No data to display");
    }
    // drawChart();
  }
  // If Op is 'Rights', assign the access rights and request the availiable
	// models list
  if (json[0].Op == 'Rights') {
    giveAcessRights(json);
    var url = window.location.href.toString();
    var dp = "";
    /*if (url.indexOf("design_project_id=") != -1) {
      dp = url.split("design_project_id=")[1].split("&")[0];
      localStorage.dp = dp;

    } else if (localStorage.dp !== undefined) {
      dp = localStorage.dp;
    }*/
    
    /*dp = dp.replace(/%20/g," ");*/

    var jsonData = {
        'Op' : 'getmodels',
        'Project' : dp != "" ? dp : undefined,
        'Key' : getCookie("JSESSIONID")
    }
    ws.send(JSON.stringify(jsonData));

    if (localStorage.tutorial != undefined && localStorage.tutorial.indexOf("home=done") == -1) { // if the user never opened this page, start the tutorial
        request_tutorial();
      }
      if (localStorage.tutorial == undefined) {
        localStorage.tutorial += "";
        request_tutorial();
      }
  }
}

ws.onclose = function (evt) {
    // location.reload();
};

function request_tutorial() {
  $('#alert').html("Would you like to see a tutorial for this page?" + '<br><br><button class="btn btn-default" id="yes" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide();start_tutorial();">Yes</button><button class="btn btn-default" id="no" onclick="$(\'#overlay\').hide();$(\'#overlay-back\').hide();">No</button>');
  $('#overlay').show();
  $('#overlay-back').show();
}

function start_tutorial() {
  create_model_tutorial();
  $('#tutorial_box').toggle();

}

function create_model_tutorial() {
  var pos=$('#create').offset();
  var h=$('#create').height();
  var w=$('#create').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h });
  $('#tutorial').html('The Create Opinion Model option lets you <b>create</b> a new opinion model.<br><br><center><button class="btn btn-default" id="next" style="margin-left:5px;" onclick="view_model_tutorial();">Next</button></center>');
}

function view_model_tutorial() {
  var pos=$('#view').offset();
  var h=$('#view').height();
  var w=$('#view').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h});
  $('#tutorial').html('The View Opinion Model option lets you <b>view</b> a previously created opinion model.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="create_model_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="edit_model_tutorial();">Next</button></center>');
}

function edit_model_tutorial() {
  var pos=$('#edit').offset();
  var h=$('#edit').height();
  var w=$('#edit').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h});
  $('#tutorial').html('The Edit Opinion Model option lets you <b>edit</b> a previously created opinion model.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="view_model_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="delete_model_tutorial();">Next</button></center>');
}

function delete_model_tutorial() {
  var pos=$('#delete').offset();
  var h=$('#delete').height();
  var w=$('#delete').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h});
  $('#tutorial').html('The Delete Opinion Model option lets you <b>delete</b> a previously created opinion model. <b>This action cannot be undone!</b><br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="edit_model_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="models_tutorial();">Next</button></center>');
}

function models_tutorial() {
  var pos=$('#_view').offset();
  var h=$('#_view').height();
  var w=$('#_view').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h});
  $('#tutorial').html('The View panel provides an overview of the existing models that you can access. You can click on any of these models to view its opinion extraction page.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="delete_model_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="prediction_tutorial();">Next</button></center>');
}

function prediction_tutorial() {
  var pos=$('#create_prediction').offset();
  var h=$('#create_prediction').height();
  var w=$('#create_prediction').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h});
  $('#tutorial').html('The Generate Prediction option lets you choose a group of PSSs and provides an estimate of how their global sentiment will evolve over time.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="models_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="top5_tutorial();">Next</button></center>');
}

function top5_tutorial() {
  var pos=$('#chart_title').offset();
  var h=$('#chart_title').height();
  var w=$('#chart_title').width();

  $('#tutorial_box').css({ left: pos.left, top: pos.top + h});
  $('#tutorial').html('This chart displays, by default, the five PSSs with the highest global sentiment values. You can customize the chart by clicking the Chart Setup option, which allows you to choose which PSSs you want to see and the default time span to be displayed. You can also interact with the chart using the slider on the bottom.<br><br><center><button class="btn btn-default" id="previous" style="margin-left:5px;" onclick="prediction_tutorial();">Previous</button><button class="btn btn-default" style="margin-left:5px;" id="next" onclick="end_tutorial();">Next</button></center>');
}

function end_tutorial() {
  var pos=$('#chart_title').offset();
  var h=$('#chart_title').height();
  var w=$('#chart_title').width();

  $('#tutorial').html('You\'ve reached the end of the tutorial. You can access it at any time by clicking the <i class="fa fa-question-circle" aria-hidden="true"></i> button at the top right corner of the page.<br><br><center><button class="btn btn-default" style="margin-left:5px;" id="end" onclick="$(\'#tutorial_box\').toggle();">Finish</button></center>');


  if (localStorage.tutorial.indexOf("home=done") == -1) {
    localStorage.tutorial += "home=done;";
  }

}

function new_model(){
  var role = sessionStorage.session;
  sessionStorage.clear();
  sessionStorage.session = role;
  sessionStorage.internal = true; // true if the new model request comes from
									// the homepage
}

/*
 * Builds a dropdown with all the availiable models
 */
function populatePSS() {
  /*
	 * var x = document.getElementById('Models'); var Models =
	 * JSON.parse(JSON.stringify(json)); for (var i = 1; i < Models.length; i++) {
	 * var option = document.createElement('option'); option.text =
	 * Models[i].Name; option.value = Models[i].Id+';'+Models[i].PSS;
	 * if(getCookie('Product') == option.text) option.selected=true;
	 * x.add(option); } checkEditable();
	 */
  var x = document.getElementById('Models');
  var Models = JSON.parse(JSON.stringify(json));
  for (var i = 1; i < Models.length; i++) {
    var option = document.createElement('option');
    option.text = Models[i].Name;
    option.value = Models[i].Id + ';' + Models[i].PSS;
    if(getCookie('Product') == option.text)
      option.selected=true;
    x.add(option);
  }
  all_models = Models;
  displayModels();
  // checkEditable();
}


function checkEditable() {
  if ($('#Models option').size() == 0) {
    document.getElementById('edit_model').disabled = true;
  } else {
    document.getElementById('edit_model').disabled = false;
  }
}

$(window).resize(function() {
  if (this.resizeTO)
    clearTimeout(this.resizeTO);
  this.resizeTO = setTimeout(function() {
    $(this).trigger('resizeEnd');
  }, 500);
});

// redraw graph when window resize is completed
$(window).on('resizeEnd', function() {
  if (newData != 0) {
    drawChart();
  }
});

/*
 * Reload the database (currently not availiable in the UI)
 */
function refreshDB(clean) {
  if (clean == 1) {
    json = {
      'Op' : 'clean',
      "Key" : getCookie("JSESSIONID")
    }

    ws.send(JSON.stringify(json));
    setTimeout(function(){json = {
        'Op' : 'load',
        'Key' : getCookie("JSESSIONID")
      }

      ws.send(JSON.stringify(json));}, 3000);
  }else{
  setTimeout(function(){json = {
    'Op' : 'load',
    'Key' : getCookie("JSESSIONID")
  }

  ws.send(JSON.stringify(json));}, 0);
  }
};

function getMonthFromString(mon){
   return new Date(Date.parse(mon +" 1, 2012")).getMonth();
}

function drawChart() {
  // top 5 PSS
  middle = new google.visualization.LineChart(document.getElementById('top5'));

  var data = JSON.parse(JSON.stringify(newData));


  var globaldata = new google.visualization.DataTable();
  globaldata.addColumn('date', 'Date');

  var counter = data[1];
/*
 * var pssNumber = 0;
 *
 * for (var i = 0; i < counter.length; i++) { // find the number of PSSs to
 * display (amount of 'Filter' in JSON) if (counter[i].hasOwnProperty('Filter')) {
 * globaldata.addColumn('number', counter[i].Filter); pssNumber++; } }
 */
  var pssNumber = 0;
  for (var i = 0; i < counter.length; i++) {
    if (counter[i].hasOwnProperty('Filter')) {
      globaldata.addColumn('number', counter[i].Filter);
      pssNumber++;
    } else {
      globaldata.addRow();
    }
  }

  globaldata.addRow();

  var column = 0;
  for (var i = 0; i < counter.length - (pssNumber - 1); i++) {

    if (!counter[i].hasOwnProperty('Filter')) {
      var time = counter[i].Date.split(" ");
      globaldata.setCell(i, 0, new Date(time[1] + "/" + time[0] + "/" + time[2]));
      if (counter[i].Value != -1) {
        globaldata.setCell(i, column, counter[i].Value);
      }
    } else {
      column++;
    }
  }
/*
 * for (var i = 0; i < pssNumber + 1 ; i++) { // until we reach the last PSS...
 * if (!counter[i].hasOwnProperty('Filter')) { // if its a month,value pair var
 * time = counter[i].Date.split(" "); for (var j = 0; j < 12; j++) { // for each
 * month if (i == 1 && j == 0) { globaldata.addRows(12); // add 12 rows for the
 * 12 months } //globaldata.setCell(j,0,new Date(new
 * Date(localStorage.start_date).getFullYear(),getMonthFromString(counter[((i-1)*12) +
 * i+j].Month),01)); // the first cell of each line is the name of the month
 * globaldata.setCell(j,0, new Date(time[1] + "/" + time[0] + "/" + time[2]));
 * if (counter[((i-1)*12) + i+j].Value != -1) {
 * globaldata.setCell(j,i,counter[((i-1)*12) + i+j].Value); // set the value of
 * the cell } } // if it's the first iteration, the array position is i+j. for
 * the other iterations it } // adds 12 positions per iteration }
 */

  var start = new Date(localStorage.start_date).toDateString() != "Invalid Date" ? new Date(localStorage.start_date) : 0;
  var end = new Date(localStorage.end_date).toDateString() != "Invalid Date" ? new Date(localStorage.end_date) : 0;


  var options = {
   backgroundColor: { fill:'transparent' },
   lineWidth: 3,
   legend : {
     position : 'bottom'
   },
   hAxis: {
     showTextEvery: 1,
     textStyle : {
       fontSize: 12
     },
   },
   vAxis: {
     title: 'Global Sentiment',
     viewWindow: {
      max: 0,
      min: 100
    },
  },
  width : '100%',
  height : '100%',
  explorer: {
    axis: 'horizontal',
    keepInBounds: true,
    maxZoomIn: 2.0,
    maxZoomOut : 1,
  },
  pointsVisible: (localStorage.showPoints != undefined && localStorage.showPoints == 'true') ? true : false,
  legend : {
    maxLines: 5,
    position: 'bottom'
  }
 };
/*
 * if (start != 0 && end != 0) { options.hAxis.viewWindow = { min : start, max :
 * end } } else if (start != 0 && end == 0) { options.hAxis.viewWindow = { min :
 * start } } else if (start == 0 && end != 0) { options.hAxis.viewWindow = { max :
 * start } }
 */
 var dashboard = new google.visualization.Dashboard(document.getElementById('dashboard'));

  var control = new google.visualization.ControlWrapper({
    controlType: 'ChartRangeFilter',
    containerId: 'control',
    options : {
     'filterColumnIndex': 0,
     ui : {
       chartOptions : {
         backgroundColor: { fill:'transparent' },
         chartArea: {
           height: '30%',
           width: '20%'
         }
       },
       minRangeSize : 7889238000,
     },
    },
    state: {
      range : {
        'start': start != 0 ? start : undefined,
        'end' : end != 0 ? end : undefined
      }
    }
  });

  var chart = new google.visualization.ChartWrapper({
    'chartType': 'LineChart',
    'containerId': 'top5',
    'dataTable' : globaldata,
    'options': options,
  });

  dashboard.bind(control, chart);
  dashboard.draw(globaldata);
 // middle.draw(globaldata, options);
}

function viewModels(op) {
  if($('#view').css('display')!='none') {
    $('#create').hide();
    $('#view').hide();
    $('#edit').hide();
    $('#delete').hide();
          $('#create_gray').hide();
    $('#view_gray').hide();
    $('#edit_gray').hide();
    $('#delete_gray').hide();
    $('#view_models').show();
    if (op == "edit") {
      $('#edit').show();
      $('#view_edit').show();
      $('#view_select').hide();
      $('#view_delete').hide();
    }
    if (op == "view") {
      $('#view').show();
      $('#view_edit').hide();
      $('#view_edit').hide();
      $('#view_select').show();
      $('#view_delete').hide();
    }
    if (op == "delete") {
      $('#delete').show();
      $('#view_edit').hide();
      $('#view_select').hide();
      $('#view_delete').show();
    }
  }

  else if ($('#view_models').css('display')!='none') {
    $('#create').show();
    $('#view').show();
    $('#edit').show();
    $('#delete').show();
    $('#view_models').hide();
  }

  sessionStorage.setItem('view', 'false');
}

function viewModel() {
  sessionStorage.setItem('view','true');
}

function displayModels() {
  var form = document.createElement('form');
  form.setAttribute("action", "opinion_extraction_page.html");
  for (i = 1; i<all_models.length; i++) {
    var button = document.createElement('button');
    button.setAttribute("id", "model_box");
    button.setAttribute("class", "btn btn-default text-left");
    button.setAttribute("onclick", "setCookie2('" + all_models[i].Name + "','" + all_models[i].Id + "','" + all_models[i].PSS + "');");
    button.setAttribute("type", "submit");
    button.innerHTML = ("value", "<b>Opinion Model: </b>" + all_models[i].Name + "<br><b>PSS: </b>" + all_models[i].PSS);
    button.setAttribute("style", "text-align:left;border-radius: 0; width: 90%; margin-bottom: 5px;box-shadow: 2px 2px 5px 2px rgba(0, 0, 0, .1);");
    form.appendChild(button);
  }
  var form2= form;
  $('#_view').append(form);
}

function ok(val) {

	 if (val) {

	  var name = $('#Models :selected').text();
    var model_data = document.getElementById('Models').value.split(';');
    var jsonData = {
      "Op" : "update_model",// create or update
      "URI" : true,
      "Update" : 1,
      "PSS" : model_data[1],
      /*
		 * "Age" : document.getElementById('age').value != "" ? document
		 * .getElementById('age').value : erro = true, "Gender" :
		 * document.getElementById('gender').value != "" ? document
		 * .getElementById('gender').value : erro = true,
		 */
      "Final_Product" : true,
      "Archive" : true,
      "Name" : name,

      "User" : localStorage.user,

      "Id": model_data[0],
      "Start_date": 0,
      'Key' : getCookie("JSESSIONID")
    };
    ws.send(JSON.stringify(jsonData));
	  $('#alert').html('Model ' + name + ' deleted.<br><br><button class="btn btn-default" id="ok" onclick="location.reload()// = \'index.html\'">OK</button>');
    $('#overlay').show();
	  $('#overlay-back').show();
	  // window.alert("Model " + name + " deleted.");
  } else {
    $('#alert').html('No models were deleted.<br><br><button class="btn btn-default" id="ok" onclick="location.reload()//href = \'index.html\'">OK</button>');
  	$('#overlay').show();
  	$('#overlay-back').show();
   }
 }

function deleteModel() {
	// var confirm = window.confirm("Do you really want to delete model " + name
	// + "?");
  var select = document.getElementById("Models");
  var selectedModel = select.options[select.selectedIndex].text;
	$('#alert').html('Do you really want to delete model ' + selectedModel + '?' + '<br><br><button class="btn btn-default" id="yes" onclick="ok(true)">Yes</button><button class="btn btn-default" id="no" onclick="ok(false)">No</button>');
  $('#overlay').show();
	$('#overlay-back').show();

}

/*
 * When the user clicks on the button, toggle between hiding and showing the
 * dropdown content
 */
function myFunction() {
    document.getElementById("myDropdown").classList.toggle("show");
}

// Close the dropdown menu if the user clicks outside of it
window.onclick = function(event) {
  if (!event.target.matches('.dropbtn')) {

    var dropdowns = document.getElementsByClassName("dropdown-content");
    var i;
    for (i = 0; i < dropdowns.length; i++) {
      var openDropdown = dropdowns[i];
      if (openDropdown.classList.contains('show')) {
        openDropdown.classList.remove('show');

      }
    }
  }
}

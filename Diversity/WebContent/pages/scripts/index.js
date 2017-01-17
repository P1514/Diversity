google.charts.load('current', {packages:['corechart']});
ws = new WebSocket('ws://' + window.location.hostname + ':'
    + window.location.port + '/Diversity/server');
var json;
var i = 0;
var middle;
var jsonData;
var newData;
var all_models = [];

function giveAcessRights(json){
      if(json[0].view_OM){
      document.getElementById("view").style.display = 'block';//show
      document.getElementById("view_gray").style.display = 'none';//hide
      }

      else{
      document.getElementById("view_gray").style.display = 'block';//show
      document.getElementById("edit").style.display =  'none';//hide
      }

      if(json[0].create_edit_delete_model){

      document.getElementById("create").style.display = 'block';//show
      document.getElementById("create_gray").style.display = 'none';//hide
      document.getElementById("edit").style.display = 'block';//show
      document.getElementById("edit_gray").style.display = 'none';//hide
      document.getElementById("delete").style.display = 'block';//show
      document.getElementById("delete_gray").style.display = 'none';//hide

      }
      else{
      document.getElementById("create").style.display =  'none';//hide
      document.getElementById("create_gray").style.display = 'block';//show
      document.getElementById("edit").style.display =  'none';//hide
      document.getElementById("edit_gray").style.display = 'block';//show
      document.getElementById("delete").style.display =  'none';//hide
      document.getElementById("delete_gray").style.display = 'block';//show

      }

      if(json[0].view_opinion_results){
      document.getElementById("_view").style.display = 'block';//show
      document.getElementById("_view_gray").style.display = 'none';//hide

      }
      else{
      document.getElementById("_view_gray").style.display = 'block';//show
      document.getElementById("_view").style.display = 'none';//hide

      }

      if(json[0].view_use_opinion_prediction){
      document.getElementById("predict").style.display = 'block';//show
      document.getElementById("predict_gray").style.display = 'none';//hide

      }
      else{
      document.getElementById("predict").style.display = 'none';//hide
      document.getElementById("predict_gray").style.display = 'block';//show

      }

      if(json[0].save_delete_snapshots){
        //not implemented yet
      }
      else{

      }

      if(!json[0].create_edit_delete_model && !json[0].view_OM ){
          document.getElementById("_define").style["background-color"]= "#666666";
          console.log('test');
        }
        else{

        }
}


function getRole(){
  var url = window.location.href.toString();
  var type = url.split("role_desc=")
      var role;
      document.getElementById("dropdown").style.display = 'block';//hides dropdown
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
      "Op" : 'getrestrictions',//create or update
      "Role": role==null?'no_role':role,
    };
      ws.send(JSON.stringify(jsonData));
}

function getCookie(name) {
  var value = '; ' + document.cookie;
  var parts = value.split('; ' + name + '=');
  if (parts.length == 2)
    return parts.pop().split(';').shift();
}

function setCookie() {
  // document.cookie='Product=; expires=Thu, 01-Jan-1970 00:00:01 GMT;';
//	document.cookie = 'Model='
      + $('#Models :selected').text();
      var cookiemonster = document.getElementById('Models').value.split(';');
  //document.cookie = 'Id=' + cookiemonster[0];
  //document.cookie = 'PSS=' + cookiemonster[1];
  sessionStorage.model=$('#Models :selected').text();
  sessionStorage.id=cookiemonster[0];
  sessionStorage.pss=cookiemonster[1];
}

function setCookie2(name, id, pss) {
  sessionStorage.model = name;
  sessionStorage.id = id;
  sessionStorage.pss = pss;
}

ws.onopen = function() {
	if(getCookie("Developer") == "Guilherme") sessionStorage.session="DESIGNER";
  getRole();
}


ws.onmessage = function(event) {
  json = JSON.parse(event.data);
  if (json[0].Op == 'Error') {
    //alert(json[0].Message);
	$('#alert').html(json[0].Message + '<br><br><button class="btn btn-default" id="ok" onclick="location.href = \'index.html\'">OK</button>');
	$('#overlay').show();
	$('#overlay-back').show();
  }
  if (json[0].Op == 'Models') {
    populatePSS();
    var jsonData = {
      'Op' : 'Top5Reach',
    }
    ws.send(JSON.stringify(jsonData));
  }

  if (json[0].Op == 'Graph') {
    if (!json[1].hasOwnProperty('Graph')) {
      newData = JSON.parse(JSON.stringify(json));
      google.charts.setOnLoadCallback(drawChart);
    } else {
      console.log("No data to display");
    }
    //drawChart();
  }
  if (json[0].Op == 'Rights') {
    giveAcessRights(json);
    var jsonData = {
        'Op' : 'getmodels',
      }
      ws.send(JSON.stringify(jsonData));
  }
}

ws.onclose = function (evt) {
    //location.reload();
};

function new_model(){
  var role = sessionStorage.session;
  sessionStorage.clear();
  sessionStorage.session = role;
  sessionStorage.internal = true; // true if the new model request comes from the homepage
}

function populatePSS() {
  /*
  var x = document.getElementById('Models');
  var Models = JSON.parse(JSON.stringify(json));
  for (var i = 1; i < Models.length; i++) {
    var option = document.createElement('option');
    option.text = Models[i].Name;
    option.value = Models[i].Id+';'+Models[i].PSS;
    if(getCookie('Product') == option.text)
      option.selected=true;
    x.add(option);
  }
  checkEditable();
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
  //checkEditable();
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

//redraw graph when window resize is completed
$(window).on('resizeEnd', function() {
  if (newData != 0) {
    drawChart();
  }
});


function refreshDB(clean) {
  if (clean == 1) {
    json = {
      'Op' : 'clean',
    }

    ws.send(JSON.stringify(json));
    setTimeout(function(){json = {
        'Op' : 'load',
      }

      ws.send(JSON.stringify(json));}, 3000);
  }else{
  setTimeout(function(){json = {
    'Op' : 'load',
  }

  ws.send(JSON.stringify(json));}, 0);
  }
};

function drawChart() {
  //top 5 PSS
  middle = new google.visualization.LineChart(document.getElementById('top5'));

  var data = JSON.parse(JSON.stringify(newData));


  var globaldata = new google.visualization.DataTable();
  globaldata.addColumn('string', 'Month');

  var counter = data[1];

  var pssNumber = 0;
  for (var i = 0; i < counter.length; i++) {							// find the number of PSSs to display (amount of 'Filter' in JSON)
    if (counter[i].hasOwnProperty('Filter')) {
      globaldata.addColumn('number', counter[i].Filter);
      pssNumber++;
    }
  }

  for (var i = 0; i < pssNumber + 1 ; i++) {							// until we reach the last PSS...
    if (!counter[i].hasOwnProperty('Filter')) {						// if its a month,value pair
      for (var j = 0; j < 12; j++) {											// for each month
        if (i == 1 && j == 0) {
          globaldata.addRows(12);													// add 12 rows for the 12 months
        }
        globaldata.setCell(j,0,counter[((i-1)*12) + i+j].Month);				// the first cell of each line is the name of the month
        globaldata.setCell(j,i,counter[((i-1)*12) + i+j].Value); // set the value of the cell
      }															// if it's the first iteration, the array position is i+j. for the other iterations it
    }																// adds 12 positions per iteration
  }

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
     }
   },
   vAxis: {
     title: 'Global Sentiment',
     viewWindow: {
      max: 0,
      min: 100
    }
  },
  width : '100%',
  height : '100%',
 };

 middle.draw(globaldata, options);
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
      "Op" : "update_model",//create or update
      "URI" : true,
      "Update" : 1,
      "PSS" : model_data[1],
      /*"Age" : document.getElementById('age').value != "" ? document
          .getElementById('age').value : erro = true,
      "Gender" : document.getElementById('gender').value != "" ? document
          .getElementById('gender').value : erro = true,*/
      "Final_Product" : true,
      "Archive" : true,
      "Name" : name,
      "User" : 1,//TODO find this field
      "Id": model_data[0],
      "Start_date": 0,
    };
    ws.send(JSON.stringify(jsonData));
	$('#alert').html('Model ' + name + ' deleted.<br><br><button class="btn btn-default" id="ok" onclick="location.href = \'index.html\'">OK</button>');
    $('#overlay').show();
	$('#overlay-back').show();
	//window.alert("Model " + name + " deleted.");
  } else {
    $('#alert').html('No models were deleted.<br><br><button class="btn btn-default" id="ok" onclick="location.href = \'index.html\'">OK</button>');
	$('#overlay').show();
	$('#overlay-back').show();
  }
}

function deleteModel() {



	//var confirm = window.confirm("Do you really want to delete model " + name + "?");
	$('#alert').html('Do you really want to delete model ' + name + '?' + '<br><br><button class="btn btn-default" id="yes" onclick="ok(true)">Yes</button><button class="btn btn-default" id="no" onclick="ok(false)">No</button>');
    $('#overlay').show();
	$('#overlay-back').show();

}

/* When the user clicks on the button,
toggle between hiding and showing the dropdown content */
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

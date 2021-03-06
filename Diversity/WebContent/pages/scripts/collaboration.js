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
var userCompany;
var userStorage = [];
var teamRoles = [];
var availableUsers = [];
var team = [];
var roles = [];
var loadAmount = 0;
function loadingscreen(amount){
	$('#loading').html('<center><div style="width: 90%"><div class="progress-bar active" role="progressbar" aria-valuenow="' + amount + '" aria-valuemin="0" aria-valuemax="100" style="margin-left:5px; margin-right: 5px; min-height:20px; background-color: #604460; width:' + amount + '%">' + amount + '%</div><br><div>Loading, please wait...</div></div>');
}


//var loadingtimer=window.setInterval(loadingscreen(loadAmount), 10000);
function getCookie(name) { //not being used
	  var value = "; " + document.cookie;
	  var parts = value.split("; " + name + "=");
	  if (parts.length == 2)
	    return parts.pop().split(";").shift();
	}
document.addEventListener('DOMContentLoaded', function() {

	$('#loading').html('<div class="progress-bar active" role="progressbar" aria-valuenow="' + loadAmount + '" aria-valuemin="0" aria-valuemax="100" style="margin-left:5px; margin-right: 5px; min-height:20px; background-color: #604460; width:' + loadAmount + '%">' + loadAmount + '%</div><br><div>Loading, please wait...</div>');
	$('#overlay').show();
	$('#overlay-back').show();
	userCompany = getParam("company") !== undefined ? getParam("company").toLowerCase() : "no company specified";
	var str = userCompany;

	$('#comp').append(str[0].toUpperCase() + str.slice(1));
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
			'Op' : 'getrestrictions',
			'Role' : 'Designer',
			'Key' : "Colab",
		}

		ws.send(JSON.stringify(json));

  }

  ws.onmessage = function(event) {
    var json = JSON.parse(event.data.replace(/\\/g,''));
		if (json[0].Op == "Loading") {
			loadAmount = json[0].Ammount
			loadingscreen(Math.round(loadAmount));
		}

		if (json[0].Op == "Rights") {
			json = {
				'Op' : 'get_roles',
				'Key' : "Colab",
			}

			ws.send(JSON.stringify(json));
			return;
		}

		if (json[0].Op == "Roles") {
			roles = json[1].Roles.substring(1, json[1].Roles.length).split(',');
			//console.log(roles);
			var json2 = {
				"Op" : "collaboration",
				'Key' : "Colab",
				'Products' : getParam('products'),
				'Services' : getParam('services'),
				'Company' : getParam('company')
			}

			ws.send(JSON.stringify(json2));
			return;
		}



    //If the message Op is 'collaboration', draw the team composition table
    if (json[0].Op == "collaboration") {
			$('#loading').fadeOut(2000);
			$('#overlay-back').fadeOut(2000);
      draw = true;
			users = json[1];
			for (var i = 0; i < users.length; i++) {
					teamRoles[i] = users[i].Role;
					userStorage[i] = users[i];
			}
      //console.log(users);
			var tmp = getMultipleParams("user");
			var users2 = [];
			for (var i = 0; i < tmp.length; i++) {
				users2.push({
					'User_ID' : tmp[i].split(',')[0],
					'Role_ID' : tmp[i].split(',')[1]
				});
			}
			var json3 = {
				'Op': 'get_user_roles',
				'IDs' : users2,
				'Key' : "Colab",
			}

			ws.send(JSON.stringify(json3));

      drawTable();

      	if (sessionStorage.all == undefined || sessionStorage.unranked == undefined) {
			if (getParam('products') === undefined && getParam('services') === undefined && getParam('company') === undefined) {
				$('#all').click();
				$('#unranked').click();
			}

		    if (getParam('products') == 'null' && getParam('services') == 'null') {
					$('#all').click();
					$('#unranked').click();
				}
      	} else {
		    if (sessionStorage.unranked != undefined && (document.getElementById('unranked').checked.toString() != sessionStorage.unranked)) {
		    	$('#unranked').click();
		    }
		    
		    if (sessionStorage.all != undefined && (document.getElementById('all').checked.toString() != sessionStorage.all)) {
		    	$('#all').click();
		    }
	    }

	    
/*
			if (getParam('company') == 'null' || getParam('company') === undefined) {
				$('#all').prop('checked', true);
			}
*/
	    		return;

    }

		if (json[0].Op == "names") {
			for (var i = 0; i < json.length; i++) {
				if (!json[i].hasOwnProperty('Op')) {
					console.log(i);
					for (var j = 0; j < userStorage.length; j++) {
						console.log(userStorage[j]);
						var name1 = json[i].First_name + ' ' + json[i].Last_name;
						var name2 = userStorage[j].First_name + ' ' + userStorage[j].Last_name;
						if (name1 == name2 /*&& json[i].Company == availableUsers[i].Company*/) {
							userStorage[j].Role = json[i].Role;
							addMember(j);

						}
					}
				}
			}
			

			return;
		}

    //If the message Op is 'Error', it contains a message from the server, which is displayed in an overlay box
    if (json[0].Op == "Error") {
        $('#overlay-back').show();
        $('#overlay').show();
        $('#loading').html(json[0].Message + '<br>' + '<input id="submit" class="btn btn-default" onclick="$(\'#overlay-back\').hide();$(\'#overlay\').hide();" style="margin-top:20px" type="submit" value="OK" />');

    }

		if (json[0].Op == "DBLoading") {
			$('#dbload').show();
		}
  }
});


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
function drawTable() {
	// | USER_NAME | USER_ROLE | USER_COMPANY | COMPANY_TYPE | USER_RATING |
	$('#users_body').empty();
	availableUsers = [];
	var name;
	var company;
	var rating;
	var role;
	for (var i = 0; i < users.length; i++) {
		user = users[i];
		company = user.Company.toLowerCase();
		if ((company.toLowerCase() == userCompany.toLowerCase() || document.getElementById('all').checked) && team.indexOf(user) == -1 && (user.hasOwnProperty('Ranking') || document.getElementById('unranked').checked)) {
			name = user.First_name + ' ' + user.Last_name;
			rating = user.hasOwnProperty('Ranking') ? parseInt(user.Ranking, 10) : '--';
			role = user.Role;
			//console.log("Adding user " + name + " from " + company);
			$('#users_body').append('<tr id=user_' + i + '><td style="padding-left:10px;padding-top:2px;padding-bottom:2px;padding-right:2px;"><a onClick="addMember(' + i + ');" ><img style="height: 20px;width: 20px;" src="..\\images\\icons\\team_add.png" onClick="addMember(' + i + ');"></a><td style="padding:2px;" class="name">' + name + '</td><td style="padding:2px;" class="role">' + role + '</td><td style="padding:2px;" class="company">' + company + '</td><td style="padding-right:10px;padding-left:2px;padding-top:2px;padding-bottom:2px;" class="rating">' + rating + '</td></tr>');
			userStorage[i] = user;
			availableUsers.push(user);
		}
	}

	var options = {
		valueNames: [ 'name', 'role', 'company', 'rating']
	};

	var userList = new List('table', options);
	//window.clearInterval(loadingtimer);
	$('#overlay').fadeOut(2000);
	$('#overlay-back').fadeOut(3000);
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

function getMultipleParams(param) {
  var query = location.search.substr(1);
  var params = query.split("&");
  var result = [];
  for(var i=0; i<params.length; i++) {
    var item = params[i].split("=");
		if (item[0] == param) {
		  result.push(item[1]);
			console.log(item[1]);
		}
  }
  return result;
}

$('#all').change(function() {
    // this will contain a reference to the checkbox
		drawTable();
});

$('#unranked').change(function() {
    // this will contain a reference to the checkbox
		drawTable();
});

function addMember(position) {
	var rolesOptions = '';
	user = userStorage[position];
	if (team.indexOf(user) == -1) {
		user.Position = position;
		company = user.Company;
		name = user.First_name + ' ' + user.Last_name;
		rating = user.hasOwnProperty('Ranking') ? parseInt(user.Ranking, 10) : '--';
		role = user.Role;
		for (var i = 0; i < roles.length; i++) {
			if (roles[i] == role) {
				rolesOptions += '<option value="' + roles[i] + '" selected>' + roles[i] + '</option>';
			} else {
				rolesOptions += '<option value="' + roles[i] + '">' + roles[i] + '</option>';
			}
		}
		$('#team_body').append('<tr id="team_' + position + '"><td style="padding-left:10px;padding-top:2px;padding-bottom:2px;padding-right:2px;"><a onClick="removeMember(' + position + ');" ><img style="height: 20px;width: 20px;" src="..\\images\\icons\\team_remove.png" onClick="removeMember(' + position + ');"></a><td style="padding:2px;" class="name">' + name + '</td><td style="padding:2px;" class="role">' +
		'<select id="role' + position +'">' + rolesOptions + '</select>' +
		'</td><td style="padding:2px;" class="company">' + company + '</td><td style="padding-right:10px;padding-left:2px;padding-top:2px;padding-bottom:2px;" class="rating">' + rating + '</td></tr>');
		$('#user_' + position).remove();
		team.push(user);
		availableUsers.splice(availableUsers.indexOf(user));


		var options = {
			valueNames: [ 'name', 'role', 'company', 'rating']
		};
		var userList = new List('table', options);
	}
}

function setRole(position, role) {
	userStorage[position].Role = role;
}

function addToTeam(user, position) {
	var newRole = $('#role' + position).val();

	user.Role = newRole;
	if (team.indexOf(user) == -1) {
		team.push(user);
	}
}

function removeMember(position) {
	user = userStorage[position];
	if (availableUsers.indexOf(user) == -1) {
		user.Position = position;
		company = user.Company;
		name = user.First_name + ' ' + user.Last_name;
		rating = user.hasOwnProperty('Ranking') ? parseInt(user.Ranking, 10) : '--';
		role = teamRoles[position];
		$('#users_body').append('<tr id=user_' + position + '><td style="padding-left:10px;padding-top:2px;padding-bottom:2px;padding-right:2px;"><a onClick="addMember(' + position + ');" ><img style="height: 20px;width: 20px;" src="..\\images\\icons\\team_add.png" onClick="addMember(' + position + ');"></a><td style="padding:2px;" class="name">' + name + '</td><td style="padding:2px;" class="role">' + role + '</td><td style="padding:2px;" class="company">' + company + '</td><td style="padding-right:10px;padding-left:2px;padding-top:2px;padding-bottom:2px;" class="rating">' + rating + '</td></tr>');
		availableUsers.push(user);

		var options = {
			valueNames: ['name', 'role', 'company', 'rating']
		};
		var userList = new List('table', options);
	}
	$('#team_' + position).remove();
	team.splice(team.indexOf(user));

}

function createCORSRequest(method, url) {
  var xhr = new XMLHttpRequest();
  if ("withCredentials" in xhr) {

    // Check if the XMLHttpRequest object has a "withCredentials" property.
    // "withCredentials" only exists on XMLHTTPRequest2 objects.
    xhr.open(method, url, true);

  } else if (typeof XDomainRequest != "undefined") {

    // Otherwise, check if XDomainRequest.
    // XDomainRequest only exists in IE, and is IE's way of making CORS requests.
    xhr = new XDomainRequest();
    xhr.open(method, url);

  } else {

    // Otherwise, CORS is not supported by the browser.
    xhr = null;

  }
  return xhr;
}

function submit() {
	var result = [];

	var transaction = {
		'transactionId' : getParam('transactionId')
	}
	result.push(transaction);

	for (var i = 0; i < team.length; i++) {
		team[i].Role = $('#role' + team[i].Position).val();
		result.push(team[i]);
		//console.log(team[i].Role);
	}


	var json = {
		'Op' : 'send_collab',
		'Message' : result,
		'Key' : "Colab",
	};

	ws.send(JSON.stringify(json));



/*
	$(function () {
		$.ajax({
	  	type: "POST",
	    data :JSON.stringify(result),
	    url: "https://diversity.euprojects.net/collaborationTool/suggestions",
	    contentType: "application/json"
	  });
	});
	//console.log(JSON.stringify(result));
*/

}

function refresh_users() {
	var json = {
		'Op' : 'refresh_users',
		'Key' : "Colab",
	};

	sessionStorage.all = document.getElementById('all').checked;
	sessionStorage.unranked = document.getElementById('unranked').checked;
	ws.send(JSON.stringify(json));
	window.location.reload();
}

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
var availableUsers = [];
var team = [];

function getCookie(name) { //not being used
	  var value = "; " + document.cookie;
	  var parts = value.split("; " + name + "=");
	  if (parts.length == 2)
	    return parts.pop().split(";").shift();
	}
document.addEventListener('DOMContentLoaded', function() {

  $('#overlay-back').hide();
  $('#overlay').hide();
	userCompany = getParam("company").toLowerCase();
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
			'Role' : 'DESIGNER',
			'Key' : getCookie('JSESSIONID'),
		}

		ws.send(JSON.stringify(json));

  }

  ws.onmessage = function(event) {
    var json = JSON.parse(event.data.replace(/\\/g,''));

		if (json[0].Op == "Rights") {
			json2 = {
	      "Op" : "collaboration",
	      'Key' : getCookie("JSESSIONID"),
				'Products' : getParam('products'),
				'Services' : getParam('services'),
				'Company' : getParam('company')
	    }

	    ws.send(JSON.stringify(json2));
		}
    //If the message Op is 'collaboration', draw the team composition table
    if (json[0].Op == "collaboration") {
      draw = true;
			users = json[1];
      console.log(users);

      drawTable();
    }

    //If the message Op is 'Error', it contains a message from the server, which is displayed in an overlay box
    if (json[0].Op == "Error") {
        $('#overlay-back').show();
        $('#overlay').show();
        $('#error').html(json[1] + '<br>' + '<input id="submit" class="btn btn-default" onclick="$(\'#overlay-back\').hide();$(\'#overlay\').hide();" style="margin-top:20px" type="submit" value="OK" />');

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
			console.log("Adding user " + name + " from " + company);
			$('#users_body').append('<tr id=user_' + i + '><td style="padding-left:10px;padding-top:2px;padding-bottom:2px;padding-right:2px;"><a onClick="addMember(' + i + ');" ><img style="height: 20px;width: 20px;" src="..\\images\\icons\\team_add.png" onClick="addMember(' + i + ');"></a><td style="padding:2px;" class="name">' + name + '</td><td style="padding:2px;" class="role">' + role + '</td><td style="padding:2px;" class="company">' + company + '</td><td style="padding-right:10px;padding-left:2px;padding-top:2px;padding-bottom:2px;" class="rating">' + rating + '</td></tr>');
			userStorage[i] = user;
			availableUsers.push(user);
		}
	}

	var options = {
		valueNames: [ 'name', 'role', 'company', 'rating']
	};

	var userList = new List('table', options);
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

$('#all').change(function() {
    // this will contain a reference to the checkbox
		drawTable();
});

$('#unranked').change(function() {
    // this will contain a reference to the checkbox
		drawTable();
});

function addMember(position) {
	user = userStorage[position];
	if (team.indexOf(user) == -1) {
		company = user.Company;
		name = user.First_name + ' ' + user.Last_name;
		rating = user.hasOwnProperty('Ranking') ? parseInt(user.Ranking, 10) : '--';
		role = user.Role;
		$('#team_body').append('<tr id="team_' + position + '"><td style="padding-left:10px;padding-top:2px;padding-bottom:2px;padding-right:2px;"><a onClick="removeMember(' + position + ');" ><img style="height: 20px;width: 20px;" src="..\\images\\icons\\team_remove.png" onClick="removeMember(' + position + ');"></a><td style="padding:2px;" class="name">' + name + '</td><td style="padding:2px;" class="role">' + role + '</td><td style="padding:2px;" class="company">' + company + '</td><td style="padding-right:10px;padding-left:2px;padding-top:2px;padding-bottom:2px;" class="rating">' + rating + '</td></tr>');
		$('#user_' + position).remove();
		availableUsers.splice(availableUsers.indexOf(user, 1));
		team.push(user);

		var options = {
			valueNames: [ 'name', 'role', 'company', 'rating']
		};
		var userList = new List('table', options);
	}
}

function removeMember(position) {
	user = userStorage[position];
	if (availableUsers.indexOf(user) == -1) {
		company = user.Company;
		name = user.First_name + ' ' + user.Last_name;
		rating = user.hasOwnProperty('Ranking') ? parseInt(user.Ranking, 10) : '--';
		role = user.Role;
		$('#users_body').append('<tr id=user_' + position + '><td style="padding-left:10px;padding-top:2px;padding-bottom:2px;padding-right:2px;"><a onClick="addMember(' + position + ');" ><img style="height: 20px;width: 20px;" src="..\\images\\icons\\team_add.png" onClick="addMember(' + position + ');"></a><td style="padding:2px;" class="name">' + name + '</td><td style="padding:2px;" class="role">' + role + '</td><td style="padding:2px;" class="company">' + company + '</td><td style="padding-right:10px;padding-left:2px;padding-top:2px;padding-bottom:2px;" class="rating">' + rating + '</td></tr>');
		availableUsers.push(user);

		var options = {
			valueNames: ['name', 'role', 'company', 'rating']
		};
		var userList = new List('table', options);
	}
	$('#team_' + position).remove();
	team.splice(team.indexOf(user, 1));

}

function submit() {
	var result = [];
	for (var i = 0; i < team.length; i++) {
		result.push(team[i]);
	}

	console.log(JSON.stringify(result));
}

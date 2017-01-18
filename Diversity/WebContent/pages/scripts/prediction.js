
var ws = new WebSocket('ws://' + window.location.hostname + ":"
  + window.location.port + '/Diversity/server');

ws.onmessage = function(event) {
  var json = JSON.parse(event.data);

  if (json[0].Op == "Prediction") {

  }

  if (json[0].Op == "Error") {
    if (json[0].hasOwnProperty("Message")) {
      console.log(json[0].Message);
    }
  }
}

function drawChart() {
  var data = new google.visualization.DataTable();
  data.addColumn('string', 'Month');
  data.addColumn('number', 'Global Sentiment');
  data.addColumn({id:'min', type:'number', role:'interval'});
   data.addColumn({id:'max', type:'number', role:'interval'});
  data.addRows([
    ['JAN', 80 , 75, 83],
    ['FEB', 77 , 70, 80],
    ['MAR', 60 , 50, 70],
    ['APR', 45 , 40, 50],
    ['MAY', 46 , 40, 50],
    ['JUN', 63 , 50, 76],
    ['JUL', 69 , 50, 76],
    ['AUG', 70 , 50, 76],
    ['SEP', 65 , 50, 70],
    ['OCT', 59 , 50, 70],
    ['NOV', 49 , 30, 50],
    ['DEC', 45 , 40, 50]
  ]);

  var options = {
    hAxis: {
      title: 'Time'
    },
    series: {
      0: {
        color: '#FF4500',
        lineWidth: 3
      }
    },
    intervals: {
      'style':'area',
    },
    vAxis: {
      title: 'Global Sentiment'
    },
    legend: {
      position: 'bottom'
    },
    backgroundColor: {
      fill:'transparent'
    },
    curveType: 'function'
  };

  var chart = new google.visualization.LineChart(document.getElementById('graph'));
  chart.draw(data,options);
}

google.charts.load('current', {packages: ['corechart', 'line']});
google.charts.setOnLoadCallback(drawChart);

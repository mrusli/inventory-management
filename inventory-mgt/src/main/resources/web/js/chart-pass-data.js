/**
 * 
 */
var dataJson;
var data = [];

function setData(jsonObj) {
  dataJson = JSON.parse(jsonObj);
  console.log(dataJson);

  var header = ['Browser', 'Percentage'];
  data.push(header)

  for(var i=0; i < dataJson.length; i++) {
    var temp=[];
    temp.push(dataJson[i].browser);
    temp.push(dataJson[i].percentage);

    data.push(temp);
  }
  
  console.log(data);

  return data; 
}

function drawChart() {
  // use the data from controller
  var chartData = new google.visualization.arrayToDataTable(data);
  // Set chart options
  var options = {
    title: 'WorldWide Browser Usage',
	titleTextStyle: {
	    fontName: 'Roboto',
		fontSize: 24
	},
	vAxis: {
	    title: 'Browser',
	    titleTextStyle: {
	      fontName: 'Roboto',
	      fontSize: 18
	    },
	    textStyle: {
	      fontName: 'Roboto',
	      fontSize: 16
	    }
	  },	
    width: 1000,
    height: 550,
    backgroundColor: '#E4E9F7',
    is3D: true,
  }

  // Instantiate and draw the chart in the ZK placeholder
  // var chart = new google.visualization.PieChart(zk.Widget.$("$piechart_3d").$n());
  var chart = new google.visualization.BarChart(zk.Widget.$("$barchart_3d").$n());
  chart.draw(chartData, options);			
}

// call drawChart function to draws chart when Google Chart library get loaded completely
google.charts.setOnLoadCallback(drawChart);
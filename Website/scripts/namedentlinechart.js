//AUTOR: Timo Eisert (7470259)

// Set new default font family and font color to mimic Bootstrap's default styling
Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#858796';

function number_format(number, decimals, dec_point, thousands_sep) {
  // *     example: number_format(1234.56, 2, ',', ' ');
  // *     return: '1 234,56'
  number = (number + '').replace(',', '').replace(' ', '');
  var n = !isFinite(+number) ? 0 : +number,
    prec = !isFinite(+decimals) ? 0 : Math.abs(decimals),
    sep = (typeof thousands_sep === 'undefined') ? ',' : thousands_sep,
    dec = (typeof dec_point === 'undefined') ? '.' : dec_point,
    s = '',
    toFixedFix = function(n, prec) {
      var k = Math.pow(10, prec);
      return '' + Math.round(n * k) / k;
    };
  // Fix for IE parseFloat(0.55).toFixed(0) = 0;
  s = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.');
  if (s[0].length > 3) {
    s[0] = s[0].replace(/\B(?=(?:\d{3})+(?!\d))/g, sep);
  }
  if ((s[1] || '').length < prec) {
    s[1] = s[1] || '';
    s[1] += new Array(prec - s[1].length + 1).join('0');
  }
  return s.join(dec);
}




//##################################################

// NamedEnt Line Chart
var ctx4 = document.getElementById("namedent_line_chart");
var namedentlinechart = new Chart(ctx4, {
type: 'line',
data: {
    
    labels: [...Array(50).keys()],
    datasets: [{
        
        label: "Orte ",
        hoverBackgroundColor: "#2e59d9",
        pointBackgroundColor: "#3a3b45",
        borderColor: "#4e73df",
        data: [],
        custom_labels: [],
        order: 1,
        fill: false,

       
    },
    
    {   
        label: "Organisationen ",
        hoverBackgroundColor: "#17a673",
        pointBackgroundColor: "#3a3b45",
        borderColor: "#1cc88a",
        data: [],
        custom_labels: [],
        order: 2,
        fill: false,
       

    },

    {   
        label: "Personen ",
        hoverBackgroundColor: "#b3428d",
        pointBackgroundColor: "#3a3b45",
        borderColor: "#f759c3",
        data: [],
        custom_labels: [],
        order: 3,
        fill: false,
       

    }

    ],
  },
  options: {
    responsive: true,
    maintainAspectRatio: false,
    layout: {
      padding: {
        left: 10,
        right: 25,
        top: 25,
        bottom: 0
      }
    },
    scales: {
      xAxes: [{
        /*
        time: {
          unit: 'Anzahl'
        },
        */
        gridLines: {
          display: true,
          drawBorder: true
        },
        ticks: {
          
        },
        maxBarThickness: 25,
      }],
      yAxes: [{
        ticks: {
          min: 0,
          
          
          padding: 10,
          
        },
        gridLines: {
          color: "rgb(234, 236, 244)",
          zeroLineColor: "rgb(234, 236, 244)",
          drawBorder: true,
          borderDash: [2],
          zeroLineBorderDash: [2]
        }
      }],
    },
    legend: {
      display: true
    },
    tooltips: {
      titleMarginBottom: 10,
      titleFontColor: '#6e707e',
      titleFontSize: 14,
      backgroundColor: "rgb(255,255,255)",
      bodyFontColor: "#858796",
      borderColor: '#dddfeb',
      borderWidth: 1,
      xPadding: 15,
      yPadding: 15,
      displayColors: false,
      caretPadding: 10,
      callbacks: {
        
        title: function(tooltipItem, chart) {
          var set_index = tooltipItem[0].datasetIndex;
          var item_index = tooltipItem[0].index;
          
          var curr_name = chart.datasets[set_index].custom_labels[item_index]
          return curr_name;
        },
        
        label: function(tooltipItem, chart) {
          var datasetLabel = chart.datasets[tooltipItem.datasetIndex].label || '';
          
          return "Anzahl: " + number_format(tooltipItem.yLabel);
        },

        labelTextColor: function(tooltipItem, chart) {
          var set_index = tooltipItem.datasetIndex;
          var new_color = chart.data.datasets[set_index].borderColor;
          return new_color;
        }
      
      }
    },
  }
});


function updateNamedEntLineChart(newlabels, newdata){
  
  namedentlinechart.data.datasets[0].custom_labels = newlabels[0];
  namedentlinechart.data.datasets[1].custom_labels = newlabels[1];
  namedentlinechart.data.datasets[2].custom_labels = newlabels[2];


  namedentlinechart.data.datasets[0].data = newdata[0];
  namedentlinechart.data.datasets[1].data = newdata[1];
  namedentlinechart.data.datasets[2].data = newdata[2];
  namedentlinechart.update();
  
}
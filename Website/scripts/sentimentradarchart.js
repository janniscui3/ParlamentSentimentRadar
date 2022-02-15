//AUTOR: Timo Eisert (7470259)

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

// Sentiment Radar Chart
var ctx3 = document.getElementById("sentiment_radar_chart");
var sentimentradarchart = new Chart(ctx3, {
  type: 'radar',
  data: {
      labels: [
          'Positiv',
          'Neutral',
          'Negativ'
        ],
      datasets: [{

          label: 'My Dataset',
          data: [],
          fill: true,
          backgroundColor: 'rgba(54, 162, 235, 0.2)',
          borderColor: 'rgb(54, 162, 235)',
          pointBackgroundColor: 'rgb(54, 162, 235)',
          pointBorderColor: '#fff',
          pointHoverBackgroundColor: '#fff',
          pointHoverBorderColor: 'rgb(54, 162, 235)'
      }],
      


  },
  options: {
    maintainAspectRatio: false,
    
  
    layout: {
        padding: {
        left: 10,
        right: 10,
        top: 0,
        bottom: -70
        }
    },

    legend: {
      display: false
    },

    elements: {
      line: {
        borderWidth: 3
      }
    },

    scale:{
      ticks: {
        beginAtZero : true
      },
      pointLabels: {
        fontSize: 15,
        fontColor: ["#1cc88a","#4e73df","#f759c3"]
      }
      
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
          var labelid =  chart.datasets[0].data.indexOf(tooltipItem[0].yLabel);
          return chart.labels[labelid];
        },
        label: function(tooltipItem, chart) {
          var datasetLabel = chart.datasets[tooltipItem.datasetIndex].label || '';
          return "Anzahl: " + number_format(tooltipItem.yLabel);
        }
      }
    }  
  }
    
  
});

function updateSentimentRadarChart(newdata){
  sentimentradarchart.data.datasets[0].data = newdata;
  sentimentradarchart.update();
}

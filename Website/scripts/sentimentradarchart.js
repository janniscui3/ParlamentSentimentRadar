//AUTOR: Timo Eisert (7470259)



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

          label: 'My Second Dataset',
          data: [28, 48, 40],
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
          bottom: -40
          }
      },

      legend: {
        display: false
      },

      elements: {
        line: {
          borderWidth: 3
        }
      }
  },
    
  
});


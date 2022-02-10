//AUTOR: Timo Eisert (7470259)

//initializes graphs at the start
function initiatePage(){ 
    
    updateTokenLineChart(["Peter","Olaf","Martin"],[13,23,400])
    updatePosBarChart(["Peter","Olaf","Martin"],[13,23,400])
    updateSentimentRadarChart([275,215,109])
    updateNamedEntLineChart(["1","2","3","4","5"],[[274,253,192,115],[223,210,130,75],[299,221,132,15,5]]);
    updateSpeakerBarChart(["Peter","Olaf","Martin"],[13,23,400]);
}
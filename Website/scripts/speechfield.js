//AUTOR: Timo Eisert (7470259)

// This js file handles the speech viewer at the bottom of the page

// This function inserts the protocols into the first selection box
function createProtocolSelect(protocollist){
    
    var select1 = document.getElementById("select1");

    for (var i = 0; i < protocollist.length; i++) {
        var opt = document.createElement('option');
        
        var link = 'javascript:getAgendaitems(' + '"' + protocollist[i] + '"' + ');';
        
        opt.value = link;
        
        opt.innerHTML = protocollist[i];
        select1.appendChild(opt);
             
    }

}

// This function inserts the agendaitems into the second selection box
function createAgendaSelect(agendaitems, protocolid){
    
    var select2 = document.getElementById("select2");
    for (var i = 0; i < agendaitems.length; i++) {
        var opt = document.createElement('option');
        
        var link = 'javascript:getSpeeches(' + '"' + agendaitems[i] + '"' +"," + '"'+   protocolid + '"' +  ');';
        
        opt.value = link;
        
        opt.innerHTML = agendaitems[i];
        select2.appendChild(opt);
              
    }

}

// This function inserts the speeches into the third selection box
function createSpeechSelect(speeches){
    
    var select3 = document.getElementById("select3");
    for (var i = 0; i < speeches.length; i++) {
        var opt = document.createElement('option');
        
        var link = 'javascript:getSpeechText(' + '"' + speeches[i] + '"' +  ');';
        
        opt.value = link;
        
        opt.innerHTML = speeches[i];
        select3.appendChild(opt);
        
        
    }
}

// This function inserts the speech text into the text field
function insertSpeech(text){
    var textfield = document.getElementById("color-text");
    textfield.innerHTML = text;
  
}
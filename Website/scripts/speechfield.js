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

function createSpeechSelect(speeches){
    console.log(speeches)
    var select3 = document.getElementById("select3");
    for (var i = 0; i < speeches.length; i++) {
        var opt = document.createElement('option');
        
        var link = 'javascript:insertSpeech(' + '"' + speeches[i] + '"' +  ');';
        
        opt.value = link;
        
        opt.innerHTML = speeches[i];
        select3.appendChild(opt);
        
        
    }
}

function insertSpeech(speechid){
    alert(speechid)
}
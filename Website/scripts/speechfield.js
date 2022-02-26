function createProtocolSelect(protocollist){
    
    var select1 = document.getElementById("select1");
    
    for (var i = 0; i < protocollist.length; i++) {
        var opt = document.createElement('option');
        opt.value = protocollist[i];
        opt.innerHTML = protocollist[i];
        select1.appendChild(opt);
        
        
    }
    
    

}
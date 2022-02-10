//AUTOR: Timo Eisert (7470259)

//initializes graphs at the start
function initiatePage(){ 
    
    getTokens();
    //updateTokenLineChart(["Peter","Olaf","Martin"],[13,23,400])
    updatePosBarChart(["Peter","Olaf","Martin"],[13,23,400])
    updateSentimentRadarChart([275,215,109])
    updateNamedEntLineChart(["1","2","3","4","5"],[[274,253,192,115],[223,210,130,75],[299,221,132,15,5]]);
    updateSpeakerBarChart(["Peter","Olaf","Martin"],[13,23,400]);
}

function getTokens(){

    $(function(){
		$.ajax({
			method: "GET",
            dataType: "json",
			url: "http://localhost:4567/tokens",
            contentType: "text/plain",
            			
            success: function(data){
                






                
                var token_list = data["result"];
                var token_name = [];
                var token_amount = [];
                
                    
                for(var i = 0; i < 50; i++){
                    token_name.push(token_list[i]["token"]);
                    token_amount.push(token_list[i]["count"]);
                }

                updateTokenLineChart(token_name, token_amount);
                

                /*
                if (data["success"] == true){
                    var token_list = data["result"];
                    var token_name = [];
                    var token_amount = [];
                    
                    for(var i = 0; i < 50; i++){
                        token_name.push(token_list[i]["token"]);
                        token_amount.push(token_list[i]["count"]);
                    }
                    
                    updateBarChart(token_name,token_amount,chartnumber,mode,newbuttonname);
                    


                  
                }
                else{
                    alert("ERROR");
                }
                */
            },
            error: function (xhr, ajaxOptions, thrownError){
                alert(xhr.status)
                alert(thrownError);
            }
        });			
		
    });        

}

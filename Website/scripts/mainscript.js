//AUTOR: Timo Eisert (7470259)

//initializes graphs at the start
function initiatePage(){ 
    
    getProgress();
    getTokens();
    getPOS();
    getSentiment();
    //updateTokenLineChart(["Peter","Olaf","Martin"],[13,23,400])
    
    
    updateNamedEntLineChart(["1","2","3","4","5"],[[274,253,192,115],[223,210,130,75],[299,221,132,15,5]]);
    updateSpeakerBarChart(["Peter","Olaf","Martin"],[13,23,400]);
}

function getProgress(){
    $(function(){
		$.ajax({
			method: "GET",
            dataType: "json",
			url: "http://localhost:4567/progress",
                      			
            success: function(data){
                if (data["success"] == "true"){
                    var already_processed = parseInt(data["current"]);
                    var total = parseInt(data["total"]);

                    var percentage = Math.floor(((already_processed / total) * 100))
                    
                    var progressbar = document.getElementById("progressbar1");
                    
                    progressbar.innerHTML = percentage + "%"
                    progressbar.style = "width: " + percentage + "%"

                }
                else{
                    alert("ERROR");
                }           
                
            },
            error: function (xhr, ajaxOptions, thrownError){
                alert("ERROR (" + xhr.status + ")");
                
            }
        });					
    });        

}

function getTokens(){

    $(function(){
		$.ajax({
			method: "GET",
            dataType: "json",
			url: "http://localhost:4567/tokens",
                      			
            success: function(data){
                if (data["success"] == "true"){
                    var token_list = data["result"];
                    var token_name = [];
                    var token_amount = [];
                                           
                    for(var i = 0; i < 50; i++){
                        token_name.push(token_list[i]["token"]);
                        token_amount.push(token_list[i]["count"]);
                    }

                    updateTokenLineChart(token_name, token_amount);
                  
                }
                else{
                    alert("ERROR");
                }           
                
            },
            error: function (xhr, ajaxOptions, thrownError){
                alert("ERROR (" + xhr.status + ")");
                
            }
        });					
    });        
}

function getPOS(){

    $(function(){
		$.ajax({
			method: "GET",
            dataType: "json",
			url: "http://localhost:4567/pos",
                     			
            success: function(data){
                if (data["success"] == "true"){
                    var pos_list = data["result"];
                    var pos_names = [];
                    var pos_values = [];
                    
                        
                    for(var i = 0; i < pos_list.length; i++){
                        pos_names.push(pos_list[i]["POS"]);
                        pos_values.push(pos_list[i]["count"]);
                    }

                    updatePosBarChart(pos_names, pos_values);
                  
                }
                else{
                    alert("ERROR");
                }           
                
            },
            error: function (xhr, ajaxOptions, thrownError){
                alert("ERROR (" + xhr.status + ")");
                
            }
        });					
    });        
}

function getSentiment(){
    $(function(){
		$.ajax({
			method: "GET",
            dataType: "json",
			url: "http://localhost:4567/sentiment",	

            success: function(data){
                
                if (data["success"] == "true"){
                    var sentimentcounter = [0,0,0];
                    var sentiment_list = data["result"];
                    
                    
                    for(var i = 0; i < sentiment_list.length; i++) {
                        var curr_sentiment = parseFloat(sentiment_list[i]["sentiment"]);
                        var curr_count = sentiment_list[i]["count"];
                        

                        if(curr_sentiment > 0){
                            sentimentcounter[0] += curr_count;
                        }
                        else if(curr_sentiment === 0){
                            
                            sentimentcounter[1] += curr_count;    
                        }
                        else{
                            sentimentcounter[2] += curr_count;   
                        }
                    }
                    
                    updateSentimentRadarChart(sentimentcounter);
                  
                }
                else{
                    alert("ERROR");
                }
            },
            error: function (error){
                alert("ERROR");
            }
        });			
    });    
}
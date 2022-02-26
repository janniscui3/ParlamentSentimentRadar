//AUTOR: Timo Eisert (7470259)

//initializes graphs at the start
function initiatePage(){ 
    
    getProgress();
    getTokens();
    getPOS();
    getSentiment();
    getNamedentities();
    getSpeakers();
    //updateTokenLineChart(["Peter","Olaf","Martin"],[13,23,400])
    
    
   
    
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
                    console.log("ERROR");
                }           
                
            },
            error: function (xhr, ajaxOptions, thrownError){
                console.log("ERROR (" + xhr.status + ")");
                
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
                    console.log("ERROR");
                }           
                
            },
            error: function (xhr, ajaxOptions, thrownError){
                console.log("ERROR (" + xhr.status + ")");
                
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
                    console.log("ERROR");
                }           
                
            },
            error: function (xhr, ajaxOptions, thrownError){
                console.log("ERROR (" + xhr.status + ")");
                
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
                    console.log("ERROR");
                }
            },
            error: function (error){
                console.log("ERROR");
            }
        });			
    });    
}

function getNamedentities(){
    $(function(){
		$.ajax({
			method: "GET",
            dataType: "json",
			url: "http://localhost:4567/namedEntities",	

            success: function(data){
                
                if (data["success"] == "true"){
                    var namedentitylist = data["result"];

                    var person_list = namedentitylist[0]["persons"];
                    var organisation_list = namedentitylist[1]["organisations"];
                    var location_list = namedentitylist[2]["locations"];
                    
                    

                    var label_list = [[],[],[]]
                    var count_list = [[],[],[]]

                    for(var i = 0; i < 50; i++){
                        label_list[0].push(location_list[i]["element"]);
                        label_list[1].push(organisation_list[i]["element"]); 
                        label_list[2].push(person_list[i]["element"]);

                        count_list[0].push(location_list[i]["count"]);                       
                        count_list[1].push(organisation_list[i]["count"]);
                        count_list[2].push(person_list[i]["count"]);
                    }

                    
                    updateNamedEntLineChart(label_list,count_list);

                    
                    
                    
                    /*
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
                    
                    */
                }
                else{
                    console.log("ERROR");
                }
            },
            error: function (error){
                console.log("ERROR");
            }
        });			
    });    
}

function getSpeakers(){
    $(function(){
		$.ajax({
			method: "GET",
            dataType: "json",
			url: "http://localhost:4567/speaker",	

            success: function(data){

                if (data["success"] == "true"){
                    var speakerlist = data["result"];
                    
                    var idlist = []
                    var namelist = []
                    var urllist = []
                    var speechcount = []

                    speakerlist.sort(function(a,b){return (b["rede"].length) - (a["rede"].length)})

                    for(var i = 0; i < 50; i++){
                        idlist.push(speakerlist[i]["_id"]);
                        namelist.push(speakerlist[i]["vorname"]+" "+speakerlist[i]["nachname"]);
                        urllist.push(speakerlist[i]["linkzubild"]);
                        speechcount.push((speakerlist[i]["rede"]).length);
                    }
                                     
                    
                    updateSpeakerBarChart(idlist, namelist, speechcount, urllist);

                    
                    
                    
                    /*
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
                    
                    */
                }
                else{
                    console.log("ERROR");
                }

                
            },
            error: function (error){
                console.log("ERROR");
            }
        });			
    });    

}
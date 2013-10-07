package io.github.abujaki.minestock;

//Stock Object holds basic Stock information

public class Stock {
	private String code, fn, pl;
	//Each stock has a stock code, a friendly name, and a player who is responsible for its creation
	Stock(String stockCode, String friendlyName, String player){
		code = stockCode; fn = friendlyName; pl = player;
	}
	String getCode(){return code;}
	String getPlayer(){return pl;}
	String getFriendlyName(){return fn;}
}

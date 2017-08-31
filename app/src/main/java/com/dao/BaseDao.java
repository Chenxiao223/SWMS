package com.dao;

public class BaseDao {

	public static String exChange(String str){
	    StringBuffer sb = new StringBuffer();  
	    if(str!=null){  
	        for(int i=0;i<str.length();i++){  
	            char c = str.charAt(i); 
	             if(Character.isLowerCase(c)){  
	                sb.append(Character.toUpperCase(c));   
	             }else{
	            	sb.append(c);
	             }  
	        }  
	    }  
	      
	    return sb.toString();  
	}  
	
	
	
	
}

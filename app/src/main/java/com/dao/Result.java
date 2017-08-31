package com.dao;

public class Result {

	boolean success;
	String readInfo;
	String reportInfo;
	String SingulationCriteriaInfo;
	
	public Result() {
		
		super();
		this.readInfo="";
		this.reportInfo="";
		this.SingulationCriteriaInfo="";
		this.success =false;
		// TODO Auto-generated constructor stub
	}
	public String getReadInfo() {
		return readInfo;
	}
	public void setReadInfo(String readInfo) {
		this.readInfo = readInfo;
	}
	public String getReportInfo() {
		return reportInfo;
	}
	public void setReportInfo(String reportInfo) {
		this.reportInfo = reportInfo;
	}
	public String getSingulationCriteriaInfo() {
		return SingulationCriteriaInfo;
	}
	public void setSingulationCriteriaInfo(String singulationCriteriaInfo) {
		SingulationCriteriaInfo = singulationCriteriaInfo;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}

package com.dao;

import com.google.gson.JsonArray;

public class JsonBean {


	private String FullCode;

	private Product   产品;

	private Enterprise 企业;

	private Interaction  互动;

	private JsonArray 足迹;

	public String toString()
	{

		return "{FullCode=" + FullCode + ", 互动=" + 互动
				+ ", 产品=" + 产品 + ", 企业=" + 企业  + ", 足迹=" + 足迹+ "}";
	}

	public String getFullCode() {
		return FullCode;
	}

	public void setFullCode(String fullCode) {
		FullCode = fullCode;
	}

	public Product get产品() {
		return 产品;
	}

	public void set产品(Product 产品) {
		this.产品 = 产品;
	}

	public Enterprise get企业() {
		return 企业;
	}

	public void set企业(Enterprise 企业) {
		this.企业 = 企业;
	}

	public Interaction get互动() {
		return 互动;
	}

	public void set互动(Interaction 互动) {
		this.互动 = 互动;
	}

	public JsonArray get足迹() {
		return 足迹;
	}

	public void set足迹(JsonArray 足迹) {
		this.足迹 = 足迹;
	}

	
	
	
	
	
}

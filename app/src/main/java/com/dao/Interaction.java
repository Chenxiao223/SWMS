package com.dao;

public class Interaction {

	private String 产品名称;
	private String 评价;
	private String 咨询;
	private String 投诉;
	private String 抽奖;
	private String 点单;
	private String 处罚;
	private int order;

	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String get产品名称() {
		return 产品名称;
	}
	public void set产品名称(String 产品名称) {
		this.产品名称 = 产品名称;
	}
	public String get咨询() {
		return 咨询;
	}
	public void set咨询(String 咨询) {
		this.咨询 = 咨询;
	}
	public String get处罚() {
		return 处罚;
	}
	public void set处罚(String 处罚) {
		this.处罚 = 处罚;
	}
	public String get投诉() {
		return 投诉;
	}
	public void set投诉(String 投诉) {
		this.投诉 = 投诉;
	}
	public String get抽奖() {
		return 抽奖;
	}
	public void set抽奖(String 抽奖) {
		this.抽奖 = 抽奖;
	}
	public String get点单() {
		return 点单;
	}
	public void set点单(String 点单) {
		this.点单 = 点单;
	}
	public String get评价() {
		return 评价;
	}
	public void set评价(String 评价) {
		this.评价 = 评价;
	}
	
}

package com.dao;


import android.content.Context;
import android.widget.Toast;

import com.hiklife.rfidapi.AntennaPortConfiguration;
import com.hiklife.rfidapi.MemoryBank;
import com.hiklife.rfidapi.RadioCtrl;
import com.hiklife.rfidapi.ReadParms;
import com.hiklife.rfidapi.ReadResult;
import com.hiklife.rfidapi.Session;
import com.hiklife.rfidapi.SingulationAlgorithm;
import com.hiklife.rfidapi.SingulationAlgorithmParms;
import com.hiklife.rfidapi.ctrlOperateResult;
import com.hiklife.rfidapi.radioBusyException;
import com.hiklife.rfidapi.radioFailException;
import com.hiklife.rfidapi.tagMemoryOpResult;
import com.zjfd.chenxiao.DHL.R;

import java.io.Serializable;
import java.util.List;

public class UhfReader implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2307601596139788426L;
	
	public static RadioCtrl myRadio;
	public Context context;
	public static AntennaPortConfiguration config = new AntennaPortConfiguration();

	public UhfReader(Context context,RadioCtrl myRadio) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.myRadio = myRadio;
	}
		
	/**
	 * 杩炴帴RFID妯″潡  ,0杩炴帴鎴愬姛锛�-2杩炴帴鐨勮缃け璐ワ紝-1杩炴帴澶辫触
	 */
	public int connectRadio() {
		int returnValue = -3;
		try {
			if (myRadio.ConnectRadio("/dev/ttyMSM2"/* "/dev/ttyS2" */, 115200) == ctrlOperateResult.OK) {
				// 閰嶇疆澶╃嚎鍙傛暟
				
				config.dwellTime = 200;
				config.numberInventoryCycles = 8192;							
			//	config.powerLevel = 300;

				// 閰嶇疆鍗曞寲绠楁硶涓哄姩鎬丵鍊硷紝浠ュ強閫氳瘽閫夋嫨涓篠ession1锛岀炕杞叧闂噺灏戞爣绛剧殑涓婃姤娆℃暟
				SingulationAlgorithmParms parm = new SingulationAlgorithmParms();
				parm.toggleTarget = 0;
				parm.maxQValue = 15;
				parm.minQValue = 0;
				parm.qValue = 7;
				parm.startQValue = 7;
				parm.repeatUntilNoTags = 0;
				parm.thresholdMultiplier = 4;
				parm.retryCount = 0;
				parm.singulationAlgorithmType = SingulationAlgorithm.DYNAMICQ;
				try {
					// 涓轰簡鍏煎鏃у浐浠舵ā鍧楋紝鍦ㄨ繘琛孲etCurrentLinkProfile鎿嶄綔涔嬪墠锛屽厛杩涜涓�涓嬪ぉ绾块┗娉㈡瘮鐨勮幏鍙�
					try {
						myRadio.GetAntennaSWR(0, config.powerLevel);
					} catch (radioFailException e) {
						// 璇ュ姛鑳戒笉鏄墍鏈夋ā鍧楅兘鏀寔锛屽洜姝や笉鐢ㄥ鐞嗗け璐�
					}

					if (myRadio.SetAntennaPortConfiguration(0, config) == ctrlOperateResult.OK
							&& myRadio.SetCurrentLinkProfile(1) == ctrlOperateResult.OK
							&& myRadio.SetTagGroupSession(Session.S1) == ctrlOperateResult.OK
							&& myRadio.SetCurrentSingulationAlgorithm(parm) == ctrlOperateResult.OK) {
						
						returnValue = 0;
						
					} else {
						returnValue = -2;
					}
					
					// 鍏抽棴鑾峰彇椹绘尝鑰屾墦寮�鐨勮浇娉�
					myRadio.WaveCtrlOff(0);
					
				} catch (radioBusyException e) {
					returnValue = -2;
				} catch (radioFailException e) {
					returnValue = -2;
				}
			} else {
				returnValue = -1;
			}
			
		} catch (radioBusyException e) {
			e.printStackTrace();
			returnValue = -2;
		}catch (Exception e)
		{
			e.printStackTrace();
			returnValue = -2;
		}
		
		return returnValue;
	}
	
	/**	
	 * 鏂紑RFID妯″潡鐨勮繛鎺�
	 */
	public int DisconnectRadio() {
		int value=-1;
		try {
			if (myRadio.DisconnectRadio() == ctrlOperateResult.OK) {
				Toast.makeText(context,
						context.getText(R.string.disconnect_success),
						Toast.LENGTH_SHORT).show();
				value = 1;
			} else {
				Toast.makeText(context,
						context.getText(R.string.disconnect_fail),
						Toast.LENGTH_SHORT).show();
				value = 0;
			}
		} catch (radioBusyException e) {
			e.printStackTrace();
			value=-1;
		}
		return value;
	}

	/**
	 * 寮�鍚洏鐐规搷浣�
	 */
	public int startInv() {
		int value=-1;
		try {
	
			// 瀵逛簬鎵嬫寔鏈哄彧鑳戒娇鐢ㄥ懆鏈熸�х洏鐐癸紝涓嶇劧cpu璧勬簮灏嗚鑰楀敖
			if (myRadio.StartInventory(0, 200) == ctrlOperateResult.OK) {
				Toast.makeText(context,
						context.getText(R.string.start_inventory_success), Toast.LENGTH_SHORT)
						.show();
				value = 1;
			} else {
				Toast.makeText(context,
						context.getText(R.string.start_inventory_fail), Toast.LENGTH_SHORT)
						.show();
				value = 0;
			}
		} catch (radioBusyException e) {
			e.printStackTrace();
			value = -1;
		}
		return value;
	}
	
	/**
	 * 鍋滄鐩樼偣鎿嶄綔
	 */
	public int stopInv() {
		
		if (myRadio.StopInventory() == ctrlOperateResult.OK) {
			Toast.makeText(context,
					context.getText(R.string.stop_inventory_success), Toast.LENGTH_SHORT)
					.show();
			return 1;
		} else {
			Toast.makeText(context,
					context.getText(R.string.stop_inventory_fail), Toast.LENGTH_SHORT)
					.show();
			return 0;
		}
	}
	
	
	//设置天线功率,最大300（30*10）,若设置范围超出范围，则默认设置为300
	public  void setAntennaPower(int power)
	{
		if(power>300 ||power < 0)
		{
			config.powerLevel = 300;
			
		}else{
			
		    config.powerLevel = power;
		
		}
	}		
	
	
	
	//全部转换为大写
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
	
	
	//检查是否为16进制字符串
	public static boolean checkStrIs16(String str)
	{
		String regStr ="[a-f0-9A-F]{"+str.length()+"}";
		if(str.matches(regStr))
		{
			return true;
			
		}else{
			
			return false;
		}	
	}		
	
	
	
	//读EPC区
	public static Result readUnGivenEpc(short offset,short length)
	{		Result result = new Result();
	
			//选择操作区域user区
			try {
			ReadParms parms = new ReadParms();
			parms.memBank = MemoryBank.EPC;
			parms.offset = offset;
			parms.length = length;
			parms.accesspassword = 0;
			List<ReadResult> tagInfos = myRadio.TagInfoRead(parms);
	
			if (tagInfos.size() > 0) {
					// 取一个进行显示
					if (tagInfos.get(tagInfos.size() - 1).result == tagMemoryOpResult.Ok) {
						if (tagInfos.get(tagInfos.size() - 1).readData != null) {						
							for (int i = 0; i < tagInfos.get(tagInfos.size() - 1).readData.length; i++) {
								
								result.readInfo += Integer.toHexString(((tagInfos.get(tagInfos.size() - 1).readData[i] >> 8) & 0x000000FF) | 0xFFFFFF00).substring(6) + Integer.toHexString((tagInfos.get(tagInfos.size() - 1).readData[i] & 0x000000FF) | 0xFFFFFF00).substring(6);
								
							}
							result.readInfo = exChange(result.readInfo);
							result.success = true;
						}
					} else {
						
						result.reportInfo ="标签Epc区读取出错";
						result.success = false;
					}
				} else {
					
					result.reportInfo ="未发现任何标签";
					result.success = false;
				}
			} catch (radioBusyException e) {
				
				result.reportInfo ="模块正在处理其他事务(如盘点),请稍后重试";
				result.success = false;
				
			} catch (radioFailException e) {
				
				result.reportInfo ="操作失败";
				result.success = false;
				
			} catch (Exception e) {
				
				result.reportInfo ="操作失败";
				result.success = false;
				
			}
			
			return result;	
			
	}
						
	//读tid区
	public static Result readUnGivenTid(short offset,short length)
			{
				Result result = new Result();
				
				//选择操作区域user区
				try {
				ReadParms parms = new ReadParms();
				parms.memBank = MemoryBank.TID;
				parms.offset = offset;
				parms.length = length;
				parms.accesspassword = 0;
				List<ReadResult> tagInfos = myRadio.TagInfoRead(parms);
				
				if (tagInfos.size() > 0) {
						// 取一个进行显示
						if (tagInfos.get(tagInfos.size() - 1).result == tagMemoryOpResult.Ok) {
							if (tagInfos.get(tagInfos.size() - 1).readData != null) {						
								for (int i = 0; i < tagInfos.get(tagInfos.size() - 1).readData.length; i++) {
									
									result.readInfo += Integer.toHexString(((tagInfos.get(tagInfos.size() - 1).readData[i] >> 8) & 0x000000FF) | 0xFFFFFF00).substring(6) + Integer.toHexString((tagInfos.get(tagInfos.size() - 1).readData[i] & 0x000000FF) | 0xFFFFFF00).substring(6);
									
								}
								result.readInfo = exChange(result.readInfo);
								result.success = true;
							}
						} else {
							
							result.reportInfo ="标签tid区读取出错";
							result.success = false;
						}
					} else {
						
						result.reportInfo ="未发现任何标签";
						result.success = false;
					}
				} catch (radioBusyException e) {
					
					result.reportInfo ="模块正在处理其他事务(如盘点),请稍后重试";
					result.success = false;
					
				} catch (radioFailException e) {
					
					result.reportInfo ="操作失败";
					result.success = false;
					
				} catch (Exception e) {
					
					result.reportInfo ="操作失败";
					result.success = false;
					
				}
				
				return result;	
						
			}		
	
	
}

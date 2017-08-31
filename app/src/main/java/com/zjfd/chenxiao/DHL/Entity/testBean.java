package com.zjfd.chenxiao.DHL.Entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/5/13.
 */
public class testBean implements Serializable {

    private static final long serialVersionUID = -8664247117405793525L;
    /**
     * data : {"object":[{"GsName":"123","attach":"附加项","cjDate":"采集时间:20170426-112129836","epc":"EPC:AAAABBBBCCCCEEEE00000018","gsAbbre":"简称:default","scDate":"上传时间:20170426-112129","tid":"TID:A08011052000648B0EC9F800"},{"GsName":"123","attach":"附加项","cjDate":"采集时间:20170426-112129836","epc":"EPC:AAAABBBBCCCCEEEE00000018","gsAbbre":"简称:default","scDate":"上传时间:20170426-112129","tid":"TID:A08011052000648B0EC9F800"},{"GsName":"123","attach":"附加项","cjDate":"采集时间:20170426-112129836","epc":"EPC:AAAABBBBCCCCEEEE00000018","gsAbbre":"简称:default","scDate":"上传时间:20170426-112129","tid":"TID:A08011052000648B0EC9F800"}]}
     */

    public DataBean data;

    public static class DataBean implements Serializable{
        private static final long serialVersionUID = -666846031836771791L;
        public List<ObjectBean> object;

        public static class ObjectBean implements Serializable{
            private static final long serialVersionUID = 2746002999891177425L;
            /**
             * GsName : 123
             * attach : 附加项
             * cjDate : 采集时间:20170426-112129836
             * epc : EPC:AAAABBBBCCCCEEEE00000018
             * gsAbbre : 简称:default
             * scDate : 上传时间:20170426-112129
             * tid : TID:A08011052000648B0EC9F800
             */

            public String GsName;
            public String attach;
            public String cjDate;
            public String epc;
            public String gsAbbre;
            public String scDate;
            public String tid;
        }
    }
}

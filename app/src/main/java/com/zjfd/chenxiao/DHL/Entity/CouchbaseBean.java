package com.zjfd.chenxiao.DHL.Entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/5/9.
 */
public class CouchbaseBean implements Serializable {

    private static final long serialVersionUID = -3418325794666903535L;
    /**
     * data : {"object":[{"attach":"附加项:","cjDate":"采集时间:20170426-112129836","epc":"EPC:AAAABBBBCCCCEEEE00000018","gsAbbre":"简称:default","gsName":"公司名称:中京复电（上海）电子技术有限公司","scDate":"上传时间:20170426-112129","tid":"TID:A08011052000648B0EC9F800"},{"attach":"附加项:","cjDate":"采集时间:20170426-112129836","epc":"EPC:AAAABBBBCCCCEEEE00000018","gsAbbre":"简称:default","gsName":"公司名称:中京复电（上海）电子技术有限公司","scDate":"上传时间:20170426-112129","tid":"TID:A08011052000648B0EC9F800"},{"attach":"附加项:","cjDate":"采集时间:20170426-112129836","epc":"EPC:AAAABBBBCCCCEEEE00000018","gsAbbre":"简称:zjfd","gsName":"公司名称:中京复电","scDate":"上传时间:20170426-112129","tid":"TID:A08011052000648B0EC9F800"}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        private static final long serialVersionUID = 5301468622932146909L;
        private List<ObjectBean> object;

        public List<ObjectBean> getObject() {
            return object;
        }

        public void setObject(List<ObjectBean> object) {
            this.object = object;
        }

        public static class ObjectBean  implements  Serializable{
            private static final long serialVersionUID = 7692064732191343846L;
            /**
             * attach : 附加项:
             * cjDate : 采集时间:20170426-112129836
             * epc : EPC:AAAABBBBCCCCEEEE00000018
             * gsAbbre : 简称:default
             * gsName : 公司名称:中京复电（上海）电子技术有限公司
             * scDate : 上传时间:20170426-112129
             * tid : TID:A08011052000648B0EC9F800
             */

            private String attach;
            private String cjDate;
            private String epc;
            private String gsAbbre;
            private String gsName;
            private String scDate;
            private String tid;

            public String getAttach() {
                return attach;
            }

            public void setAttach(String attach) {
                this.attach = attach;
            }

            public String getCjDate() {
                return cjDate;
            }

            public void setCjDate(String cjDate) {
                this.cjDate = cjDate;
            }

            public String getEpc() {
                return epc;
            }

            public void setEpc(String epc) {
                this.epc = epc;
            }

            public String getGsAbbre() {
                return gsAbbre;
            }

            public void setGsAbbre(String gsAbbre) {
                this.gsAbbre = gsAbbre;
            }

            public String getGsName() {
                return gsName;
            }

            public void setGsName(String gsName) {
                this.gsName = gsName;
            }

            public String getScDate() {
                return scDate;
            }

            public void setScDate(String scDate) {
                this.scDate = scDate;
            }

            public String getTid() {
                return tid;
            }

            public void setTid(String tid) {
                this.tid = tid;
            }
        }
    }
}

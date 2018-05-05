package com.doubeye.record.recognition.suspicious.bunch;

import com.doubeye.commons.utils.elasticsearch.ClientHelper;
import com.doubeye.commons.utils.elasticsearch.CustomSearchHelper;

/**
 * @author doubeye
 * 批量识别对话是否为空
 */
public class BunchBlackConversation {
    private ClientHelper clientHelper = new ClientHelper();
    private CustomSearchHelper searchHelper = new CustomSearchHelper();

    public void setClientHelper(ClientHelper clientHelper) {
        this.clientHelper = clientHelper;
    }

    public void setSearchHelper(CustomSearchHelper searchHelper) {
        this.searchHelper = searchHelper;
    }

    public void init(String clusterName, String node) {
        clientHelper.setClusterName(clusterName);
        clientHelper.addNode(node);
    }

    public void doProcess() {

    }
}

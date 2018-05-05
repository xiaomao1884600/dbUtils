package com.doubeye.commons.utils.elasticsearch;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author doubeye
 * ElasticSearch Client 助手
 */
public class ClientHelper {
    /**
     * ElasticSearch Cluster 名称
     */
    private String clusterName;
    /**
     * 是否自动嗅探整个集群，默认为true
     */
    private boolean clientTransportSniff = true;
    /**
     * 节点列表，格式为IP地址加端口的形式，例如127.0.0.1:9300
     */
    private List<String> nodes = new ArrayList<>();

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public void setClientTransportSniff(boolean clientTransportSniff) {
        this.clientTransportSniff = clientTransportSniff;
    }

    public void addNode(String node) {
        if (!node.contains(":")) {
            throw new RuntimeException("添加的节点不符合IP地址规范，期望的格式为IP:PORT");
        }
        nodes.add(node);
    }

    public TransportClient getClient() throws UnknownHostException {
        Settings settings = Settings.builder().put("cluster.name", clusterName).put("client.transport.sniff", clientTransportSniff).build();
        TransportClient client = new PreBuiltTransportClient(settings);
        for (String node : nodes) {
            client.addTransportAddress(getTransportAddress(node));
        }
        return client;
    }

    private TransportAddress getTransportAddress(String address) throws UnknownHostException {
        String host = StringUtils.substringBefore(address, ":");
        String port = StringUtils.substringAfter(address, ":");
        return new InetSocketTransportAddress(InetAddress.getByName(host), Integer.parseInt(port));
    }
}

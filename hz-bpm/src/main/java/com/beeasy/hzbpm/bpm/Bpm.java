package com.beeasy.hzbpm.bpm;

import cn.hutool.core.util.XmlUtil;
import com.beeasy.hzbpm.entity.NodeExt;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

import static com.beeasy.hzbpm.service.MongoService.db;

public class Bpm {

    public void start(String name){
        MongoCollection<Document> collection = db.getCollection("model");

    }

    public long[] getUsersFromNode(Node node, List<NodeExt> docs){
        NodeExt item = getExtItem(node, docs);
        if (item == null) {
            return new long[0];
        }

        return null;
    }

    public String getNodeAttribute(Node node, String key){
        NamedNodeMap attrs = node.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node item = attrs.item(i);
            if(item.getNodeName().equalsIgnoreCase(key)){
                return item.getNodeValue();
            }
        }
        return "";
    }

    public NodeExt getExtItem(Node node, List<NodeExt> documents){
        String id = getNodeAttribute(node, "id");
        for (NodeExt document : documents) {
            if(id.equals(document.id)) {
                return document;
            }
        }
        return null;
    }

    public Node getStartNode(String xmlStr){
        org.w3c.dom.Document xml = XmlUtil.readXML(xmlStr);
        NodeList processes = xml.getElementsByTagName("process");
        if (processes.getLength() == 0) {
            return null;
        }
        NodeList nodes = processes.item(0).getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node item = nodes.item(i);
            if(item.getNodeName().equals("startEvent")){
                return item;
            }
        }
        return null;
    }

}

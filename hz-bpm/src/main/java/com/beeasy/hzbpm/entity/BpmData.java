package com.beeasy.hzbpm.entity;

import java.util.List;
import java.util.Map;

public class BpmData {

    public Map<String,Node> nodes;
    public static class Node{
        public Permission permission;
    }

    public static class Permission{
        public List<String> dids;
        public List<String> rids;
        public List<String> uids;
    }
}



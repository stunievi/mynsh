package com.beeasy.zed;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.javafx.sg.prism.NodePath;
import org.beetl.sql.core.SQLManager;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.ws.ServiceMode;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static javafx.scene.input.KeyCode.T;

@Service
@Transactional
public class ZedService {

    @Autowired
    SQLManager sqlManager;

    private static final String FIELD_LINK = "link";
    private static final Pattern reg = Pattern.compile("\\.|\\s*->\\s*|\\.");

    /**
     * @param params
     */
    public JSONObject select(JSONObject params) {
        Map<String, LinkNode> links = parseLink(params.get(FIELD_LINK));
        params.remove(FIELD_LINK);

        JSONObject result = new JSONObject();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            selectSingle(entry.getKey(), (JSONObject) entry.getValue(), null, links, new ArrayList<>(), result);
        }

        return result;
    }

    private void selectSingle(String sourceTable, JSONObject value, String linkTable, Map<String, LinkNode> links, List<JSONObject> result, JSONObject finalResult) {
        String targetTable = sourceTable.toLowerCase();

        //数组查询
        int idex = sourceTable.indexOf("[]");
        boolean multipul = false;
        if (idex > -1) {
            targetTable = sourceTable.replace("[]", "").toLowerCase();
            multipul = true;
        }

        JSONObject beetlParams = new JSONObject();
        JSONObject externals = new JSONObject();
        //拼装条件
        String where = buildWhere(value, beetlParams, externals);

        //主关联
        String selectFields = "*";
        String table = targetTable;
        String join = "";
        String tfs[] = new String[0];
        do{
            if (linkTable == null) {
                break;
            }
            LinkNode node = links.get(linkTable);
            if (node == null) {
                break;
            }
            if(result.size() == 0){
                break;
            }
            List<LinkPath> paths = findLink(links, links.get(linkTable), targetTable, null)
                    .stream()
                    .sorted(new Comparator<LinkPath>() {
                        @Override
                        public int compare(LinkPath o1, LinkPath o2) {
                            return Integer.compare(o1.stack.size(), o2.stack.size());
                        }
                    })
                    .collect(Collectors.toList());
            //查询关联
            int c = 1;
            //如果有，那么只取第一个最小的
            if(paths.size() == 0){
                break;
            }
            // TODO: 2019/3/27 根据单双进行关联
            LinkPath path;
            boolean flag = paths.size() > 2 && paths.get(0).stack.size() == 1 && paths.get(1).stack.size() == 1;
            if(flag){
                if(multipul){
                    path = paths.get(0);
                } else {
                    path = paths.get(1);
                }
            } else {
                path = paths.get(0);
            }
            join = path.linkStrs.stream()
                    .collect(Collectors.joining(" "));
            final String[] finalTfs1 = tfs = path.stack.get(0).split("\\|");
            if(flag){
                if(!multipul){
                    String tmp = tfs[1];
                    tfs[1] = tfs[2];
                    tfs[2] = tmp;
                }
            }
            where += S.fmt(" and t0.%s in (#join(keyx)#)", tfs[1]);
            beetlParams.put(
                    "keyx",
                    result.stream()
                        .map(i -> i.getString(finalTfs1[1]))
                        .collect(Collectors.toSet())
            );
            selectFields = S.fmt("t%d.*, t0.%s as zed_id", path.linkStrs.size(), tfs[1]);
            table = linkTable;

        }while(false);

        String sql = S.fmt("select %s from %s t0 %s %s", selectFields, table, join , where);

        List<JSONObject> ret = sqlManager.execute(sql, JSONObject.class, beetlParams);
        if (linkTable != null && tfs.length > 0) {
            String[] finalTfs = tfs;
            for (JSONObject object : result) {
                List<JSONObject> items = ret.stream()
                        .filter(i -> S.eq(i.getString("zedId"), object.getString(finalTfs[1])))
                        .collect(Collectors.toList());
                if(multipul){
                    object.put(sourceTable,items);
                } else if(items.size() > 0){
                    object.put(sourceTable, items.get(0));
                } else {
                    object.put(sourceTable, new JSONObject());
                }
            }
        }
        //构造附加表
        for (Map.Entry<String, Object> entry : externals.entrySet()) {
            selectSingle(entry.getKey(), (JSONObject) entry.getValue(), targetTable, links, ret, null);
        }

        if (multipul) {
            if (finalResult != null) {
                finalResult.put(sourceTable, ret);
            }
        } else {
            if (finalResult != null && ret.size() > 0) {
                finalResult.put(sourceTable, ret.get(0));
            }
        }
    }

    private List<LinkPath> findLink(Map<String, LinkNode> links, LinkNode fromNode, String to, LinkPath path){
        List<LinkPath> result = new ArrayList<>();
        if (fromNode == null) {
            return result;
        }
        if (path == null) {
            path = new LinkPath();
        }
        //如果有更大的，放弃
        if(result.size() > 0){
            if(path.stack.size() >= result.get(0).stack.size()){
                return result;
            }
        }
        for (Map.Entry<String, List<LinkNode>> entry : fromNode.links.entrySet()) {
            String key = S.fmt("%s|%s",fromNode.name, entry.getKey());
            String[] fields = entry.getKey().split("\\|");
            if(path.stack.contains(key)){
                continue;
            }
            for (LinkNode linkNode : entry.getValue()) {
                LinkPath cp = $.deepCopy(path).to(new LinkPath());
                cp.stack.add(key);
                cp.linkStrs.add(
                        S.fmt(" join %s t%d on t%d.%s = t%d.%s", linkNode.name, path.linkStrs.size() + 1, path.linkStrs.size(), fields[0], path.linkStrs.size() + 1, fields[1])
                );

                if(S.eq(linkNode.name, to)){
                    result.add(cp);
                } else {
                    result.addAll(
                            findLink(links, linkNode, to, cp)
                    );
                }
            }
        }

        return result;
    }


    private String buildWhere(JSONObject params, JSONObject beetlParams, JSONObject externals) {
        int idex = 0;
        List<String> list = new ArrayList<>();
        list.add(" where 1 = 1");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            switch (entry.getKey()) {
                case "page":
                    break;

                case "size":
                    break;

                default:
                    //首字母大写，视为附加表
                    if (isUpper(entry.getKey().charAt(0))) {
                        externals.put(entry.getKey(), entry.getValue());
                        break;
                    }
                    String key = "key" + (idex++);
                    list.add(
                            S.fmt("and %s = #%s#", entry.getKey(), key)
                    );
                    beetlParams.put(key, entry.getValue());
                    break;
            }
        }
        return list.stream()
                .collect(Collectors.joining(" "));
    }

    /**
     * @param links
     * @return
     */
    private Map<String, LinkNode> parseLink(Object links) {
        Map<String, LinkNode> map = C.newMap();
        if (links == null) {
            return map;
        }
        if (!(links instanceof JSONArray)) {
            return map;
        }
        JSONArray arr = (JSONArray) links;
        for (short i = 0; i < arr.size(); i++) {
            String link = arr.getString(i).toLowerCase();
            String[] sides = reg.split(link);
            if(sides.length != 4){
                continue;
            }
            LinkNode left = getOrCreateLinkNode(map, sides[0]);
            LinkNode right = getOrCreateLinkNode(map, sides[2]);
            String leftKey = sides[1] + "|" + sides[3];
            String rightKey = sides[3] + "|" + sides[1];
            List<LinkNode> leftLinks = getOrCreateLinkNodeList(left.links, leftKey);
            List<LinkNode> rightLinks = getOrCreateLinkNodeList(right.links, rightKey);
            leftLinks.add(right);
            rightLinks.add(left);
//            String[] sides = link.split("\\s*->\\s*");
//            String[] tableFieldLeft = sides[0].split("\\.");
//            String[] tableFieldRight = sides[1].split("\\.");
//
//            LinkTable leftTable = getOrCreateLinkTable(map, tableFieldLeft[0]);
//            LinkTable rightTable = getOrCreateLinkTable(map, tableFieldLeft[0]);
//            LinkField leftLinkField = getOrCreateLinkField(leftTable, tableFieldLeft[1]);new LinkField();
//            leftLinkField.table = leftTable;
//
//
//            LinkNode linkNodeLeft = getOrCreateLinkNode(map, tableFieldLeft[0]);
//            linkNodeLeft.table = tableFieldLeft[0];
//            LinkNode linkNodeRight = getOrCreateLinkNode(map, tableFieldRight[0]);
//            linkNodeRight.table = tableFieldRight[0];
//            List<LinkNode> listLeft = getOrCreateList(linkNodeLeft.links, tableFieldLeft[1]);
//            listLeft.add(linkNodeRight);
//            List<LinkNode> listRight = getOrCreateList(linkNodeRight.links, tableFieldRight[1]);
//            listRight.add(linkNodeLeft);

        }
        return map;
    }

    /**
     *
     * @param map
     * @param key
     * @return
     */
    private LinkField getOrCreateLinkField(Map<String,LinkField> map, String key) {
        LinkField list = map.get(key);
        if (list == null) {
            list = new LinkField();
            list.name = key;
            map.put(key, list);
        }
        return list;
    }

    /**
     *
     * @param map
     * @param key
     * @return
     */
    private LinkTable getOrCreateLinkTable(Map<String, LinkTable> map, String key) {
        LinkTable list = map.get(key);
        if (list == null) {
            list = new LinkTable();
            list.name = key;
            map.put(key, list);
        }
        return list;
    }

    private LinkNode getOrCreateLinkNode(Map<String,LinkNode> map, String key){
        LinkNode node = map.get(key);
        if (node == null) {
            node = new LinkNode();
            node.name = key;
            map.put(key, node);
        }
        return node;
    }

    private List<LinkNode> getOrCreateLinkNodeList(Map map, String key){
        List list = (List) map.get(key);
        if (list == null) {
            list = new ArrayList();
            map.put(key, list);
        }
        return list;
    }

    /**
     *
     * @param s
     * @return
     */
    private boolean isUpper(String s) {
        return S.eq(s.toUpperCase(), s);
    }

    /**
     *
     * @param c
     * @return
     */
    private boolean isUpper(char c) {
        return isUpper(String.valueOf(c));
    }

    public static class LinkNode {
        public String name;
        public Map<String, List<LinkNode>> links = new HashMap<>();
    }

    public static class LinkTable {
        public String name;
        public Map<String,LinkField> fields = new HashMap<>();

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof LinkTable && S.eq(((LinkTable) obj).name, name);
        }
    }

    public static class LinkField{
        public String name;
        public LinkTable table;
        public LinkedHashSet<LinkField> links = new LinkedHashSet<>();

        @Override
        public int hashCode() {
            return name.hashCode() + table.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return
                    obj instanceof LinkField && S.eq(((LinkField) obj).name, name) && Objects.equals(((LinkField) obj).table, table);

        }
    }

    public static class LinkPath {
        public List<String> linkStrs = new ArrayList<>();
        public List<String> stack = new ArrayList<>();
    }
}

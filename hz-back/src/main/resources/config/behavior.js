function go(nodeName) {
    return tools.go(nodeName);
}

/**
 * 按照调用顺序向上查找最邻近的节点
 */
function getLastNode(nodeName){
    var obj = tools.getLastNode(nodeName);
    return {
        get : function (key) {
            if(obj == null) return null;
            return obj[key];
        }
    }
}
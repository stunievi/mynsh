function go(nodeName) {
    return tools.go(nodeName);
}

function start_next_task(modelName, dealerId, data) {
    if (!data) {
        data = {};
    }
    return tools.start_next_task(modelName, dealerId, data);
}

function get_task_dealer_id() {
    return tools.get_task_dealer_id();
}

function get_task_innate_data() {
    return tools.get_task_innate_data();
}

/**
 * 按照调用顺序向上查找最邻近的节点
 */
function getLastNode(nodeName) {
    var obj = tools.getLastNode(nodeName);
    return {
        get: function (key) {
            if (obj == null) return null;
            return obj[key];
        }
    }
}
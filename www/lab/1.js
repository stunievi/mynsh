

/** 缃戦〉褰撳墠鐘舵€佸垽鏂� (瑙ｅ喅娌″竷灞€瀹屽氨鍒囨崲椤甸潰閫犳垚鐐硅仛闆嗗湪涓€璧�)*/
var hidden, state, visibilityChange;
if (typeof document.hidden !== "undefined") {
    hidden = "hidden";
    visibilityChange = "visibilitychange";
    state = "visibilityState";
} else if (typeof document.mozHidden !== "undefined") {
    hidden = "mozHidden";
    visibilityChange = "mozvisibilitychange";
    state = "mozVisibilityState";
} else if (typeof document.msHidden !== "undefined") {
    hidden = "msHidden";
    visibilityChange = "msvisibilitychange";
    state = "msVisibilityState";
} else if (typeof document.webkitHidden !== "undefined") {
    hidden = "webkitHidden";
    visibilityChange = "webkitvisibilitychange";
    state = "webkitVisibilityState";
}
// 娣诲姞鐩戝惉鍣紝鍦╰itle閲屾樉绀虹姸鎬佸彉鍖�

/** 瑙ｅ喅娴忚鍣ㄦ爣绛惧垏鎹㈡帓鍒楅棶棰� */
var _isNeedReload = false;
var _isGraphLoaded = false;
document.addEventListener(visibilityChange, function() {

    if(document[state] == 'visible'){
        if(_isNeedReload){
            $("#MainCy").html('');
            $('#TrTxt').removeClass('active');
            getData(_currentKeyNo);
        }
        //document.title = 'hidden-not-loaded'
    } else {
        if(!_isGraphLoaded){
            _isNeedReload = true;
        }
    }
}, false);
/** end 瑙ｅ喅娴忚鍣ㄦ爣绛惧垏鎹㈡帓鍒楅棶棰� */


/** end 缃戦〉褰撳墠鐘舵€佸垽鏂� */

    //

var cy;
var id;
var activeNode;

var _rootData,_rootNode;

var _COLOR = {
    //node :   {person: '#09ACB2',company:'#128BED',current:'#FD485E'},
    //node :   {person: '#20BDBF',company:'#4EA2F0',current:'#FD485E'},
    node :   {person: '#FD485E',company:'#4ea2f0',current:'#ff9e00'},
    //node :   {person: '#a177bf',company:'#4ea2f0',current:'#FD485E'},
    //node :   {person: '#f2af00',company:'#0085c3',current:'#7ab800'},
    //border : {person: '#09ACB2',company:'#128BED',current:'#FD485E'},
    //border : {person: '#57A6A8',company:'#128BED',current:'#FD485E'},
    border : {person: '#FD485E',company:'#128BED',current:'#EF941B'},
    //border : {person: '#7F5AB8',company:'#128BED',current:'#FD485E'},
    //border : {person: '#f2af00',company:'#0085c3',current:'#7ab800'},
    //line:    {invest:'#128BED',employ:'#FD485E',legal:'#09ACB2'},
    //line:    {invest:'#4EA2F0',employ:'#20BDBF',legal:'#D969FF'}
    line:    {invest:'#fd485e',employ:'#4ea2f0',legal:'#4ea2f0'}
    //line:    {invest:'#e43055',employ:'#a177bf',legal:'#4ea2f0'}
};
var _currentKeyNo,_companyRadius = 35,_personRadius = 15,_circleMargin = 10,_circleBorder = 3,
    _layoutNode = {}, _isFocus = false;
var _maxChildrenLength = 0;


/****** 宸ュ叿 ******/

//鍘婚噸鎿嶄綔,鍏冪礌涓哄璞�
/*array = [
    {a:1,b:2,c:3,d:4},
    {a:11,b:22,c:333,d:44},
    {a:111,b:222,c:333,d:444}
];
var arr = uniqeByKeys(array,['a','b']);*/
function uniqeByKeys(array,keys){
    //灏嗗璞″厓绱犺浆鎹㈡垚瀛楃涓蹭互浣滄瘮杈�
    function obj2key(obj, keys){
        var n = keys.length,
            key = [];
        while(n--){
            key.push(obj[keys[n]]);
        }
        return key.join('|');
    }

    var arr = [];
    var hash = {};
    for (var i = 0, j = array.length; i < j; i++) {
        var k = obj2key(array[i], keys);
        if (!(k in hash)) {
            hash[k] = true;
            arr .push(array[i]);
        }
    }
    return arr ;
};
//鍘婚噸鎿嶄綔,鏅€氬厓绱�
Array.prototype.unique = function(){
    var res = [];
    var json = {};
    for(var i = 0; i < this.length; i++){
        if(!json[this[i]]){
            res.push(this[i]);
            json[this[i]] = 1;
        }
    }
    return res;
};

//娣卞鍒跺璞℃柟娉�
function cloneObj (obj) {
    var newObj = {};
    if (obj instanceof Array) {
        newObj = [];
    }
    for (var key in obj) {
        var val = obj[key];
        //newObj[key] = typeof val === 'object' ? arguments.callee(val) : val; //arguments.callee 鍦ㄥ摢涓€涓嚱鏁颁腑杩愯锛屽畠灏变唬琛ㄥ摢涓嚱鏁�, 涓€鑸敤鍦ㄥ尶鍚嶅嚱鏁颁腑銆�
        newObj[key] = typeof val === 'object' ? cloneObj(val): val;
    }
    return newObj;
};

/****** 鏁版嵁澶勭悊 ******/

// 鏁版嵁澶勭悊锛氬皢鍘熷鏁版嵁杞崲鎴恎raph鏁版嵁
function getRootData(list) {
    var graph = {}
    graph.nodes = [];
    graph.links = [];

    //graph.nodes
    for(var i = 0; i < list.length; i++){
        var nodes = list[i].graph.nodes;
        for(var j = 0; j < nodes.length; j++){
            var node = nodes[j];
            var o = {};
            o.nodeId = node.id;
            o.data = {};
            o.data.obj = node;
            //o.data.showStatus = 'NORMAL'; // NORMAL HIGHLIGHT DULL
            o.data.showStatus = null; // NORMAL HIGHLIGHT DULL
            o.layout = {}
            o.layout.level = null; // 1 褰撳墠鏌ヨ鑺傜偣
            o.layout.singleLinkChildren = []; // 鍙繛鎺ヨ嚜宸辩殑node
            graph.nodes.push(o);

            // 璁剧疆_rootNode
            if (_currentKeyNo == o.data.obj.properties.keyNo){
                _rootNode = o;
            }
        }
    }
    graph.nodes = uniqeByKeys(graph.nodes,['nodeId']);

    //graph.links
    for(var i = 0; i < list.length; i++){
        var relationships = list[i].graph.relationships;

        for(var k = 0; k < relationships.length; k++) {
            var relationship = relationships[k];
            var o = {}
            o.data = {};
            o.data.obj = relationship;
            //o.data.showStatus = 'NORMAL'; // NORMAL HIGHLIGHT DULL
            o.data.showStatus = null; // NORMAL HIGHLIGHT DULL
            o.sourceNode = getGraphNode(relationship.startNode,graph.nodes);
            o.targetNode = getGraphNode(relationship.endNode,graph.nodes);
            o.linkId = relationship.id;
            o.source = getNodesIndex(relationship.startNode,graph.nodes);
            o.target = getNodesIndex(relationship.endNode,graph.nodes);
            graph.links.push(o);
        }
    }
    graph.links = uniqeByKeys(graph.links,['linkId']);


    //emplyRevert(graph.links);
    //mergeLinks(graph.links);
    setLevel(graph.nodes,graph.links);
    setCategoryColor(graph.nodes,graph.links);

    return graph;
}
// 鏁版嵁澶勭悊锛氳懀鐩戦珮绠ご缈昏浆
function emplyRevert(links) {
    links.forEach(function (link,i) {
        if(link.data.obj.type == 'EMPLOY'){
            var tmpObj = link.source;
            var tmpObjNode = link.sourceNode;
            link.source = link.target;
            link.sourceNode = link.targetNode;
            link.target = tmpObj;
            link.targetNode = tmpObjNode;
        }
    });
}
// 鏁版嵁澶勭悊锛氳懀鐩戦珮銆佹硶浜虹嚎鍚堝苟
function mergeLinks(links) {
    links.forEach(function (link,i) {
        if(link.sourceNode.data.obj.labels[0] == 'Person' && link.data.obj.type == 'LEGAL'){
            links.forEach(function (nextLink,j) {
                if(link.linkId != nextLink.linkId &&
                    link.sourceNode.nodeId == nextLink.sourceNode.nodeId &&
                    link.targetNode.nodeId == nextLink.targetNode.nodeId &&
                    nextLink.data.obj.type == 'EMPLOY'){

                    links.splice(j,1);
                }
            });
        }

        if(link.sourceNode.data.obj.labels[0] == 'Person' && link.data.obj.type == 'EMPLOY'){
            links.forEach(function (nextLink,j) {
                if(link.linkId != nextLink.linkId &&
                    link.sourceNode.nodeId == nextLink.sourceNode.nodeId &&
                    link.targetNode.nodeId == nextLink.targetNode.nodeId &&
                    nextLink.data.obj.type == 'LEGAL'){

                    links.splice(j,1);
                }
            });
        }
    });
//        console.log(links);
}
// 鏁版嵁澶勭悊锛氳缃妭鐐瑰眰绾�
function setLevel(svg_nodes,svg_links) {
    function getNextNodes(nodeId,links,parentLevel){
        var nextNodes = [];
        for(var i = 0; i < links.length; i++){
            var link = links[i];
            if(nodeId == link.sourceNode.nodeId && !link.targetNode.layout.level){
                link.targetNode.layout.level = parentLevel;
                nextNodes.push(link.targetNode);
            } else if (nodeId == link.targetNode.nodeId && !link.sourceNode.layout.level) {
                link.sourceNode.layout.level = parentLevel;
                nextNodes.push(link.sourceNode);
            }
        }
        nextNodes = uniqeByKeys(nextNodes,['nodeId']);

        return nextNodes;
    }

    var level = 1;
    var nodes = [];
    nodes.push(_rootNode);
    while(nodes.length){
        var nextNodes = [];
        for(var i = 0; i < nodes.length; i++){
            var node = nodes[i];
            node.layout.level = level;
            nextNodes = nextNodes.concat(getNextNodes(node.nodeId,svg_links,level));
        }
        level++;
        nodes = nextNodes;
    }
}
// 鏁版嵁澶勭悊锛氳缃妭鐐硅鑹�
function setCategoryColor(nodes, links){
    for(var i = 0; i < links.length; i++){
        var sameLink = {}; // 涓ょ偣闂磋繛绾夸俊鎭�
        sameLink.length = 0; // 涓ょ偣闂磋繛绾挎暟閲�
        sameLink.currentIndex = 0; // 褰撳墠绾跨储寮�
        sameLink.isSetedSameLink = false;
        links[i].sameLink = sameLink;
    }

    /*閾炬帴鐩稿悓涓ょ偣鐨勭嚎*/
    for(var i = 0; i < links.length; i++){
        var baseLink = links[i];

        if(baseLink.sameLink.isSetedSameLink == false){
            baseLink.sameLink.isSetedSameLink = true;
            var nodeId1 = baseLink.sourceNode.nodeId;
            var nodeId2 = baseLink.targetNode.nodeId;

            var sameLinks = [];
            sameLinks.push(baseLink);
            for(var j = 0; j < links.length; j++){
                var otherLink = links[j];
                if(baseLink.linkId != otherLink.linkId && !otherLink.sameLink.isSetedSameLink){
                    if((otherLink.sourceNode.nodeId == nodeId1 && otherLink.targetNode.nodeId == nodeId2 ) ||
                        (otherLink.sourceNode.nodeId == nodeId2 && otherLink.targetNode.nodeId == nodeId1 ) ){
                        sameLinks.push(otherLink);
                        otherLink.sameLink.isSetedSameLink = true;
                    }
                }
            }

            for(var k = 0; k < sameLinks.length; k++){
                var oneLink = sameLinks[k];
                oneLink.sameLink.length = sameLinks.length; // 涓ょ偣闂磋繛绾挎暟閲�
                oneLink.sameLink.currentIndex = k; // 褰撳墠绾跨储寮�
            }
        }
    }

    for(var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        if (_currentKeyNo == node.data.obj.properties.keyNo) { // 褰撳墠鑺傜偣
            node.data.color = _COLOR.node.current;
            node.data.strokeColor = _COLOR.border.current;
        } else if (node.data.obj.labels[0] == 'Company') {
            node.data.color = _COLOR.node.company;
            node.data.strokeColor = _COLOR.border.company;
        } else {
            node.data.color = _COLOR.node.person;
            node.data.strokeColor = _COLOR.border.person;
        }
    }
}
// 鏁版嵁澶勭悊锛氳缃敮涓€瀛╁瓙
function setSingleLinkNodes(links){
    function isSingleLink (nodeId,links){
        var hasLinks = 0;
        var isSingle = true;
        for(var i = 0; i < links.length; i++){
            var link = links[i];
            if(link.targetNode.nodeId == nodeId || link.sourceNode.nodeId == nodeId){
                hasLinks++;
            }
            if(hasLinks > 1){
                isSingle = false;
                break;
            }
        }

        return isSingle;
    } // isSingleLink

    links.forEach(function (link,i) {
        if(isSingleLink(link.sourceNode.nodeId,links)){
            link.targetNode.layout.singleLinkChildren.push(link.sourceNode);
        }
        if(isSingleLink(link.targetNode.nodeId,links)){
            link.sourceNode.layout.singleLinkChildren.push(link.targetNode);
        }
    });
}
// 鏁版嵁澶勭悊锛氭牴鎹畁odeId鑾峰彇node 绱㈠紩
function getNodesIndex(nodeId,nodes) {
    var index = 0;
    for(var i = 0; i < nodes.length; i++){
        var node = nodes[i];
        if(nodeId == node.nodeId){
            index = i;
            break;
        }
    }
    return index;
}
// 鏁版嵁澶勭悊锛歯ode鏄惁瀛樺湪
function isNodeExist(nodeId,nodes) {
    var exist = false;
    for(var i = 0; i < nodes.length; i++){
        var node = nodes[i];
        if(nodeId == node.nodeId){
            exist = true;
            break;
        }
    }
    return exist;
}
// 鏁版嵁澶勭悊锛氭牴鎹畁odes杩囨护鍑虹浉搴旇繛绾匡紙娌℃湁鑺傜偣鐨勮繛绾垮垹闄わ級
function filterLinksByNodes(nodes,allLinks) {
    function isExists(nodes,nodeId) {
        var exist = false;
        for(var i = 0; i < nodes.length; i++){
            var node = nodes[i];
            if(node.nodeId == nodeId){
                exist = true;
                break;
            }
        }
        return exist;
    }
    var sel_links = [];
    for(var i = 0; i < allLinks.length; i++){
        var link = allLinks[i];
        if(isExists(nodes,link.sourceNode.nodeId) && isExists(nodes,link.targetNode.nodeId)){
            //link.source = getNodesIndex(link.sourceNode.nodeId,nodes);
            //link.target = getNodesIndex(link.targetNode.nodeId,nodes);
            sel_links.push(link);
        }
    }
    return sel_links;
}
// 鏁版嵁澶勭悊锛氭牴鎹甽inks杩囨护鍑虹浉搴旇妭鐐�(娌℃湁杩炵嚎鐨勮妭鐐瑰垹闄�)
function filterNodesByLinks(nodes,links) {
    function isExists(links,nodeId) {
        var exist = false;
        for(var i = 0; i < links.length; i++){
            var link = links[i];
            if(link.sourceNode.nodeId == nodeId || link.targetNode.nodeId == nodeId){
                exist = true;
                break;
            }
        }
        return exist;
    }
    var sel_nodes = [];
    for(var i = 0; i < nodes.length; i++){
        var node = nodes[i];
        if(isExists(links,node.nodeId)){
            sel_nodes.push(node);
        }
    }
    return sel_nodes;
}
// 鏁版嵁澶勭悊锛氭牴鎹畁odeId鑾峰彇node
function getGraphNode(nodeId,nodes) {
    var node = null;
    for(var i = 0; i < nodes.length; i++){
        if(nodes[i].nodeId == nodeId) {
            node = nodes[i];
            break;
        }
    }
    return node;
}
// 鏁版嵁澶勭悊锛氳幏鍙栧瓙鑺傜偣
function getSubNodes(node,links) {
    var subNodes = [];
    var nodeId = node.nodeId;
    var level = node.layout.level;
    for(var i = 0; i < links.length; i++){
        var link = links[i];
        if(link.sourceNode.nodeId == nodeId && link.targetNode.layout.level == level+1){
            subNodes.push(link.targetNode);
        }
        if(link.targetNode.nodeId == nodeId && link.sourceNode.layout.level == level+1){
            subNodes.push(link.sourceNode);
        }
    }
    subNodes = uniqeByKeys(subNodes,['nodeId']);
    return subNodes;
}

/**绛涢€�*/
// 鏁版嵁澶勭悊锛氭寜鐘舵€佽繃婊�
function filterNodesByLevel(level,nodes){
    var sel_nodes = [];
    nodes.forEach(function (node) {
        if(node.layout.level <= level){
            sel_nodes.push(node);
        }
    })
    return sel_nodes;
}
// 鏁版嵁澶勭悊锛氭寜鐘舵€佽繃婊�
function filterNodesByStatus(status,nodes){
    if(status == 'all'){
        return nodes;
    }

    var sel_nodes = [];
    for(var i = 0; i < nodes.length; i++){
        var node = nodes[i];
        if((node.data.obj.labels == 'Company' && node.data.obj.properties.status == status) || node.nodeId == _rootNode.nodeId){
            sel_nodes.push(node);
        }
    }
    return sel_nodes;
}
// 鏁版嵁澶勭悊锛氭寜鎸佽偂鏁拌繃婊�
function filterNodesByStockNum(num,links){
    var sel_links = [];
    for(var i = 0; i < links.length; i++){
        if(num == links[i].data.obj.properties.stockPercent){
            sel_links.push(links[i]);
        }
    }
    return sel_links;
}
// 鏁版嵁澶勭悊锛氭寜鎶曡祫杩囨护
function filterNodesByInvest(invest,nodes,links){
    /*鑾峰彇鐩存帴鎶曡祫鐨勮妭鐐�*/
    function getInvestNodes(nodeId,links) {
        var investNodes = [];
        for(var i = 0; i < links.length; i++){
            var link = links[i];
            if(link.sourceNode.nodeId == nodeId && link.data.obj.type == 'INVEST'){
                investNodes.push(link.targetNode);
            }
        }

        //investNodes = uniqeByKeys(investNodes,['nodeId']);
        return investNodes;
    }
    /*鑾峰彇鍏徃鑲′笢*/
    function getCompanyStockholder(nodeId,links) {
        var stockholderNodes = [];
        for(var i = 0; i < links.length; i++){
            var link = links[i];
            if(link.targetNode.nodeId == nodeId && link.data.obj.type == 'INVEST'){
                stockholderNodes.push(link.sourceNode);
            }
        }

        //stockholderNodes = uniqeByKeys(stockholderNodes,['nodeId']);
        return stockholderNodes;
    }
    /*鑾峰彇钁ｇ洃楂樻硶*/
    function getPersonStockholder(nodeId,links) {
        var stockholderNodes = [];
        for(var i = 0; i < links.length; i++){
            var link = links[i];
            if(link.targetNode.nodeId == nodeId && link.data.obj.type == 'INVEST' && link.sourceNode.data.obj.labels[0] == 'Person'){
                stockholderNodes.push(link.sourceNode);
            }
        }

        //stockholderNodes = uniqeByKeys(stockholderNodes,['nodeId']);
        return stockholderNodes;
    }

    var sel_nodes = [];

    switch (invest){
        case 'all':
            return nodes;
            break;
        case 'direct': //鐩存帴鎶曡祫
            sel_nodes = getInvestNodes(_rootNode.nodeId,links);
            break;
        case 'stockholder': // 鑲′笢鎶曡祫
            var nextNodes = [];
            var stockholderNodes = getCompanyStockholder(_rootNode.nodeId,links);
            for(var i = 0; i < stockholderNodes.length; i++){
                nextNodes = nextNodes.concat(getInvestNodes(stockholderNodes[i].nodeId,links));
            }
            sel_nodes = stockholderNodes.concat(nextNodes);
            break;
        case 'legal': // 钁ｇ洃楂樻硶鎶曡祫
            var nextNodes = [];
            var stockholderNodes = getPersonStockholder(_rootNode.nodeId,links);
            for(var i = 0; i < stockholderNodes.length; i++){
                nextNodes = nextNodes.concat(getInvestNodes(stockholderNodes[i].nodeId,links));
            }
            sel_nodes = stockholderNodes.concat(nextNodes);
            break;
    }


    sel_nodes = sel_nodes.concat(_rootNode);
    sel_nodes = uniqeByKeys(sel_nodes,['nodeId']);
    return sel_nodes;
}
// 鏁版嵁澶勭悊锛氭牴鎹墍鏈夋潯浠惰繃婊�
function filter(rootData){
    function isParentExist(node,nodes,links) {
        var isExist = false;
        var parentLevel = node.layout.level - 1;

        if(parentLevel < 2){
            return true;
        }

        for(var i = 0; i < links.length; i++){
            var link = links[i];
            if((link.sourceNode.nodeId == node.nodeId && link.targetNode.layout.level == parentLevel) && isNodeExist(link.targetNode.nodeId,nodes) ){
                isExist = true;
                break;
            }
            if((link.targetNode.nodeId == node.nodeId && link.sourceNode.layout.level == parentLevel) && isNodeExist(link.sourceNode.nodeId,nodes) ){
                isExist = true;
                break;
            }
        }

        return isExist;
    }
    function getFilterData(rootData) {
        //
        var sel_nodes = [];
        for(var i = 0; i < rootData.nodes.length; i++){
            sel_nodes.push(rootData.nodes[i]);
        }
        var sel_links = [];
        for(var i = 0; i < rootData.links.length; i++){
            sel_links.push(rootData.links[i]);
        }

        var level = $('#SelPanel').attr('param-level');
        var status = $('#SelPanel').attr('param-status');
        var num = $('#SelPanel').attr('param-num');
        var invest = $('#SelPanel').attr('param-invest');

        //console.log('status:' + status + ' num:' + num + ' invest:' + invest);

        // 灞傜骇
        level = parseInt(level) + 1;
        sel_nodes = filterNodesByLevel(level,sel_nodes);

        // 鐘舵€�
        if(status){
            sel_nodes = filterNodesByStatus(status,sel_nodes);
        }

        // 鎸佽偂
        var stock_nodes = [];
        if(num && num != 0){
            sel_links = filterLinksByNodes(sel_nodes,sel_links);
            sel_links = filterNodesByStockNum(num,sel_links);
            for(var i = 0; i < sel_links.length;i++){
                stock_nodes.push(sel_links[i].sourceNode);
                stock_nodes.push(sel_links[i].targetNode);
            }
            sel_nodes = uniqeByKeys(stock_nodes,['nodeId']);
        }

        // 鎶曡祫
        if(invest){
            sel_nodes = filterNodesByInvest(invest,sel_nodes,sel_links);
        }

        //鐖惰妭鐐逛笉瀛樺湪鍒欏垹闄�
        var sel_nodes2 = [];
        sel_nodes.forEach(function (node,i) {
            if(isParentExist(node,sel_nodes,sel_links)){
                sel_nodes2.push(node);
            }
        })
        sel_links = filterLinksByNodes(sel_nodes2,sel_links);

        return {links:sel_links,nodes:sel_nodes2};
    }

    var nodesIds = [];
    var selGraph= getFilterData(rootData)
    selGraph.nodes.forEach(function (node) {
        nodesIds.push(node.nodeId);
    })
    highLightFilter(nodesIds,cy);

    /*//
    //$("#load_data").show();

    // 淇濊瘉濮嬬粓瀛樺湪褰撳墠鑺傜偣
    /!*if(sel_nodes2.length == 0){
        sel_nodes2.push(_rootNode);
    }*!/

    // 鏇存柊鍥捐氨
    /!*$("#TrTxt").removeClass('active');
    domUpdate(getFilterData(rootData));*!/

    /!*setTimeout(function () {
        domUpdate({links:sel_links,nodes:sel_nodes2});
    },5000)*!/;*/
}
//
function filterReset(){
    $('#SelPanel').attr('param-level','2');
    $('#SelPanel').attr('param-status','');
    $('#SelPanel').attr('param-num','');
    $('#SelPanel').attr('param-invest','');

    $('#ShowLevel a').removeClass('active');
    $('#ShowLevel a').eq(1).addClass('active');
    $('#ShowStatus a').removeClass('active');
    $('#ShowStatus a').eq(0).addClass('active');
    $('#ShowInvest a').removeClass('active');
    $('#ShowInvest a').eq(0).addClass('active');
    $('#inputRange').val(0);
    $('#inputRange').css({'backgroundSize':'0% 100%'});
}

/****** Html 鐩稿叧 ******/
// 姘村嵃灞呬腑
function printLogoFixed() {
    var bodyH = $('body').height();
    var imgH = $('.printLogo img').height();
    var top = (parseFloat(bodyH) - parseFloat(imgH)) / 2;
    $('.printLogo img').css({'margin-top' : top + 'px'});
}
//绛涢€夐潰鏉匡細鏄剧ず
function selPanelShow() {
    $('.tp-sel').fadeIn();
    //$('.tp-sel').addClass('zoomIn');
    $('#TrSel').addClass('active');
}
//绛涢€夐潰鏉匡細闅愯棌
function selPanelHide() {
    $('.tp-sel').fadeOut();
    $('#TrSel').removeClass('active');
}
//绛涢€夐潰鏉匡細鍒楄〃鏇存柊
function selPanelUpdateList(nodes,links,isShowCheckbox) {
    $('.tp-list').html('');
    for(var i = 0; i < nodes.length; i++){
        var node = nodes[i];
        var index = i + 1;
        var name = node.data.obj.properties.name;
        var keyNo = node.data.obj.properties.keyNo;
        var str = '';
        if(isShowCheckbox){
            str = '<div class="checkbox" node_id="'+ node.nodeId +'" keyno="'+ keyNo +'"> <input checked type="checkbox"><label> ' + index + '.' + name + '</label> </div>';
//            var str = '<div class="checkbox" node_id="'+ node.nodeId +'" keyno="'+ keyNo +'"> <label> ' + index + '.' + name + '</label> </div>';
        } else {
            str = '<div class="checkbox" node_id="'+ node.nodeId +'" keyno="'+ keyNo +'"><label> ' + index + '.' + name + '</label> </div>';
        }

        $('.tp-list').append(str);
    }

    $('.tp-list > div > label').click(function () {
        var _parent = $(this).parent();
        var nodeId = _parent.attr('node_id');

        focusReady(getGraphNode(nodeId,nodes));
    });

    $('.tp-list > div > input').click(function () {
        /*var _this = $(this);
        var _parent = $(this).parent();
        var nodeId = _parent.attr('node_id');
        var checkedNodeIds = $('.tp-list').attr('node_ids');
        if(checkedNodeIds){
            checkedNodeIds = checkedNodeIds.split(',');
        }*/

        var checkedNodeIds = [];
        $('.tp-list input:checked').each(function () {
            var _parent = $(this).parent();
            var nodeId = _parent.attr('node_id');
            checkedNodeIds.push(nodeId);
        });

        /*if(_this.is(':checked')){
            checkedNodeIds.push(nodeId);
            nodes.splice(1,1);
            console.log('checked');
        } else {
            console.log('un checked');
            var sub_nodes = []
            sub_nodes = nodes.splice(0,1);
            console.log(nodes);
            console.log(sub_nodes);
            graphInit(nodes, links);
        }*/
        highLight(checkedNodeIds,cy);
        /*// 闇€瑕侀殣钘忕殑鑺傜偣鍙婂瓙鑺傜偣
        var choosedNode = getGraphNode(nodeId,nodes);
        var subNodes = getSubNodes(choosedNode,links);
        subNodes.push(choosedNode);

        // 鍓╀笅鐨勮妭鐐�
        var lastNodes = [];
        for(var i = 0; i < nodes.length; i++){
            var node = nodes[i];
            if(!getGraphNode(node.nodeId,subNodes)){
                lastNodes.push(node);
            }
        }

        // 鍓╀笅鐨勮繛绾�
        var lastLinks = filterLinksByNodes(lastNodes,links);

        graphInit(lastNodes, lastLinks);
        if(_this.is(':checked')){
            nodes.splice(1,1);
            console.log('checked');
        } else {
            console.log('un checked');
            var sub_nodes = []
            sub_nodes = nodes.splice(0,1);
            console.log(nodes);
            console.log(sub_nodes);
            graphInit(nodes, links);
        }
        console.log(nodeId);*/
    });
}
//绛涢€夐潰鏉匡細鑱氱劍鍑嗗
function focusReady(node) {
    filterReset();
    $('#FocusInput').val(node.data.obj.properties.name);
    $('#FocusInput').attr('node_id',node.nodeId);
    $('#FocusBt').text('鑱氱劍');
    $('#FocusBt').removeClass('focusDisable');
    $('#ClearInput').show();
}
//绛涢€夐潰鏉匡細鍙栨秷鑱氱劍
function focusCancel() {
    $('#ClearInput').hide();
    $('#FocusBt').text('鑱氱劍');
    $('#FocusBt').addClass('focusDisable');
    $('#FocusInput').val('');
    $('#FocusInput').attr('node_id','');
    selPanelUpdateList(_rootData.nodes,_rootData.links,true);
    cancelHighLight();
}

function maoScale(type){

    /*var c=$('canvas').eq(2).attr('id','myCanvas');
    var c=document.getElementById("myCanvas");
    console.log(c);
    var ctx = c.getContext("2d");
    ctx.font = "5px Arial";
    ctx.fillText("涓婃捣", 1, 10);

    return;*/

    //
    var rate = 0.2;
    var scale = cy.zoom();
    if(type==1){
        scale += rate;
    }else if(type==2){
        scale -= rate;
    }

    cy.zoom({
        level: scale, // the zoom level
    });
}

function resizeScreen(){
    if(isFullScreen()){
        $('#TrFullScreen').addClass('active');
        $('#TrFullScreen').html('<span class="screen2ed"></span>閫€鍑�');
    } else {
        $('#TrFullScreen').removeClass('active');
        $('#TrFullScreen').html('<span class="screen2"></span>鍏ㄥ睆');
    }

    //cy.pan();
    /*if(document.body.clientHeight>700){
        $('#Main').height(document.body.clientHeight-66);
        console.log(document.body.clientHeight);
    }else{
        $('#Main').height(640);
    }*/
}

function isFullScreen(){
    if(document.fullscreen){
        return true;
    }else if(document.mozFullScreen){
        return true;
    }else if(document.webkitIsFullScreen){
        return true;
    }else if(document.msFullscreenElement){
        return true;
    }else{
        return false;
    }
}
function launchFullScreen(element) {


    if(element.requestFullscreen) {
        element.requestFullscreen();
    }else if(element.mozRequestFullScreen) {
        element.mozRequestFullScreen();
    }else if(element.webkitRequestFullscreen) {
        element.webkitRequestFullscreen();
    } else if(element.msRequestFullscreen) {
        element.msRequestFullscreen();
    }
}
function exitFullScreen(){


    if(document.exitFullscreen){
        document.exitFullscreen();
    }
    else if(document.mozCancelFullScreen){
        document.mozCancelFullScreen();
    }
    else if(document.msExitFullscreen){
        document.msExitFullscreen();
    }
    else if(document.webkitCancelFullScreen){
        document.webkitCancelFullScreen();
    }
}

function toggleText() {
    if($("#TrTxt").hasClass('active')){
        $("#TrTxt").removeClass('active');
        cy.collection("edge").removeClass("edgeShowText");
    } else {
        $("#TrTxt").addClass('active');
        cy.collection("edge").addClass("edgeShowText");
    }
}

/****** 鍥捐氨 鐩稿叧 ******/
/*function highlight( node ){
    var oldNhood = lastHighlighted;

    var nhood = lastHighlighted = node.closedNeighborhood();
    var others = lastUnhighlighted = cy.elements().not( nhood );

    var reset = function(){
        cy.batch(function(){
            others.addClass('hidden');
            nhood.removeClass('hidden');

            allEles.removeClass('faded highlighted');

            nhood.addClass('highlighted');

            others.nodes().forEach(function(n){
                var p = n.data('orgPos');

                n.position({ x: p.x, y: p.y });
            });
        });

        return Promise.resolve().then(function(){
            if( isDirty() ){
                return fit();
            } else {
                return Promise.resolve();
            };
        }).then(function(){
            return Promise.delay( aniDur );
        });
    };

    var runLayout = function(){
        var p = node.data('orgPos');

        var l = nhood.filter(':visible').makeLayout({
            name: 'concentric',
            fit: false,
            animate: true,
            animationDuration: aniDur,
            animationEasing: easing,
            boundingBox: {
                x1: p.x - 1,
                x2: p.x + 1,
                y1: p.y - 1,
                y2: p.y + 1
            },
            avoidOverlap: true,
            concentric: function( ele ){
                if( ele.same( node ) ){
                    return 2;
                } else {
                    return 1;
                }
            },
            levelWidth: function(){ return 1; },
            padding: layoutPadding
        });

        var promise = cy.promiseOn('layoutstop');

        l.run();

        return promise;
    };

    var fit = function(){
        return cy.animation({
            fit: {
                eles: nhood.filter(':visible'),
                padding: layoutPadding
            },
            easing: easing,
            duration: aniDur
        }).play().promise();
    };

    var showOthersFaded = function(){
        return Promise.delay( 250 ).then(function(){
            cy.batch(function(){
                others.removeClass('hidden').addClass('faded');
            });
        });
    };

    return Promise.resolve()
        .then( reset )
        .then( runLayout )
        .then( fit )
        .then( showOthersFaded )
        ;

}//hilight*/
function drawGraph(elements) {
    _currentKeyNo,_companyRadius = 35,_personRadius = 15,_circleMargin = 10,_circleBorder = 3;
    cy = cytoscape({
        container: document.getElementById('MainCy'),
        motionBlur: false,
        textureOnViewport:false,
        wheelSensitivity:0.1,
        elements:elements,
        minZoom:0.4,
        maxZoom:2.5,
        layout: {
            name: 'preset',
            componentSpacing: 40,
            nestingFactor:12,
            padding: 10,
            edgeElasticity:800,
            stop:function (e) {

                //瑙ｅ喅娴忚鍣ㄦ爣绛惧垏鎹㈡帓鍒楅棶棰�
                if(document[state] == 'hidden'){
                    _isNeedReload = true;
//                        console.log('stop _isNeedReload=true');
                } else {
                    _isNeedReload = false;
                }
                setTimeout(function () {
                    if(document[state] == 'hidden'){
                        _isGraphLoaded = false;
                        console.log('stop _isGraphLoaded=false');
                    } else {
                        _isGraphLoaded = true;
                    }
                },1000);
            }
        },
        style: [
            {
                selector: 'node',
                style: {
                    shape: 'ellipse',
                    width: function (ele) {
                        //褰撳墠鑺傜偣鏈夊浘鐗�
                        if(ele.data("type") == 'Person' && _currentKeyNo == ele.data('keyNo') && ele.data('hasImage')){
                            return 80;
                        }
                        //鏈夊浘鐗�
                        if(ele.data('hasImage') && ele.data('type') == 'Person'){
                            return 60;
                        }
                        //鏅€�
                        if(ele.data("type") == 'Company'){
                            return 60;
                        }
                        return 45;
                    },
                    height: function (ele) {
                        //褰撳墠鑺傜偣鏈夊浘鐗�
                        if(ele.data("type") == 'Person' && _currentKeyNo == ele.data('keyNo') && ele.data('hasImage')){
                            return 80;
                        }
                        //鏈夊浘鐗�
                        if(ele.data('hasImage') && ele.data('type') == 'Person'){
                            return 60;
                        }
                        //鏅€�
                        if(ele.data("type") == 'Company'){
                            return 60;
                        }
                        return 45;
                    },
                    'background-color': function (ele) {
                        return ele.data('color');
                    },
                    'background-fit': 'cover',
                    'background-image': function (ele) {
                        var hasImage = ele.data('hasImage');
                        var keyNo = ele.data('keyNo');
                        var type = ele.data('type');
                        if(hasImage && type == 'Person'){
                            return 'https://co-image.qichacha.com/PersonImage/'+ keyNo+'.jpg';
                        } else {
                            return 'none';
                        }
                    },
                    // 'background-image-crossorigin': 'use-credentials',
                    'border-color': function (ele) {
                        return ele.data("borderColor");
                    },
                    'border-width': function (ele) {
                        if(ele.data('hasImage') && ele.data('type') == 'Person'){
                            return 3;
                        } else {
                            return 1;
                        }
                    },
                    'border-opacity': 1,
                    label: function (ele) {
                        var label = ele.data("name");
                        var length = label.length;

                        if(length <=5){ // 4 5 4鎺掑垪
                            return label;
                        } else if(length >=5 && length <= 9) {
                            return label.substring(0,length - 5) + '\n' + label.substring(length - 5,length);
                        } else if(length >= 9 && length <= 13){
                            return label.substring(0,4) + '\n' + label.substring(4,9) + '\n' + label.substring(9,13);
                        } else {
                            return label.substring(0,4) + '\n' + label.substring(4,9) + '\n' + label.substring(9,12) + '..';
                        }
                    },
                    'z-index-compare':'manual',
                    'z-index':20,
                    color:"#fff",
                    //'padding-top':0,
                    'padding':function (ele) {
                        if(ele.data("type") == 'Company'){
                            return 3;
                        }
                        return 0;
                    },
                    'font-size':12,
                    //'min-height':'400px',
                    //'ghost':'yes',
                    //'ghost-offset-x':300,
                    //'font-weight':800,
                    //'min-zoomed-font-size':6,
                    'font-family':'microsoft yahei',
                    'text-wrap':'wrap',
                    'text-max-width':60,
                    'text-halign':'center',
                    'text-valign':'center',
                    'overlay-color':'#fff',
                    'overlay-opacity':0,
                    'background-opacity':1,
                    'text-background-color':'#000',
                    'text-background-shape':'roundrectangle',
                    'text-background-opacity':function (ele) {
                        if(ele.data('hasImage') && ele.data('type') == 'Person'){
                            return 0.3;
                        } else {
                            return 0
                        }
                    },
                    'text-background-padding':0,
                    'text-margin-y': function (ele) {
                        //褰撳墠鑺傜偣鏈夊浘鐗�
                        if(ele.data("type") == 'Person' && _currentKeyNo == ele.data('keyNo') && ele.data('hasImage')){
                            return 23;
                        }
                        // 鏈夊浘鐗�
                        if(ele.data('hasImage') && ele.data('type') == 'Person'){
                            return 16;
                        }
                        //
                        if(ele.data("type") == 'Company'){
                            return 4;
                        }
                        return 2;
                    },
                },
            },
            {
                selector: 'edge',
                style: {
                    'line-style':function (ele) {
                        return 'solid';
                        /*if(ele.data('data').obj.type == 'INVEST'){
                            return 'solid';
                        } else {
                            return 'dashed'
                        }*/
                    },
                    'curve-style': 'bezier',
                    'control-point-step-size':20,
                    'target-arrow-shape': 'triangle-backcurve',
                    'target-arrow-color': function (ele) {
                        return ele.data("color");
                    },
                    'arrow-scale':0.5,
                    'line-color': function (ele) {
                        //return '#aaaaaa';
                        return ele.data("color");
                    },
                    label: function (ele) {
                        return '';
                    },
                    'text-opacity':0.8,
                    'font-size':12,
                    'background-color':function (ele) {
                        return '#ccc';
                        return ele.data("color");
                    },
                    'width': 0.3,
                    'overlay-color':'#fff',
                    'overlay-opacity':0,
                    'font-family':'microsoft yahei',
                }
            },
            {
                "selector": ".autorotate",
                "style": {
                    "edge-text-rotation": "autorotate"
                }
            },
            {
                selector:'.nodeActive',
                style:{
                    /*'background-color':function (ele) {
                        if(ele.data("category")==1){
                            return "#5c8ce4"
                        }
                        return "#d97a3a";
                    },*/
                    //'z-index':300,
                    'border-color': function (ele) {
                        return ele.data("color");
                    },
                    'border-width': 10,
                    'border-opacity': 0.5
                }
            },
            {
                selector:'.edgeShow',
                style:{
                    'color':'#999',
                    'text-opacity':1,
                    'font-weight':400,
                    label: function (ele) {
                        return ele.data("label");
                    },
                    'font-size':10,
                }
            },
            {
                selector:'.edgeActive',
                style:{
                    'arrow-scale':0.8,
                    'width': 1.5,
                    'color':'#330',
                    'text-opacity':1,
                    'font-size':12,
                    'text-background-color':'#fff',
                    'text-background-opacity':0.8,
                    'text-background-padding':0,
                    'source-text-margin-y':20,
                    'target-text-margin-y':20,
                    //'text-margin-y':3,
                    'z-index-compare':'manual',
                    'z-index':1,
                    'line-color': function (ele) {
                        return ele.data("color");
                    },
                    'target-arrow-color': function (ele) {
                        return ele.data("color");
                    },
                    label: function (ele) {

                        /*if(ele.data('data').obj.type == 'INVEST'){
                            return 'solid';
                        } else {
                            return 'dashed'
                        }*/
                        return ele.data("label");
                    }
                }

            },
            {
                selector:'.hidetext',
                style:{
                    'text-opacity':0,
                }
            },
            {
                selector:'.dull',
                style:{
                    'z-index':1,
                    opacity:0.2,
                }
            },
            {
                selector: '.nodeHover',
                style: {
                    shape: 'ellipse',
                    'background-opacity':0.9,
                }
            },
            {
                selector: '.edgeLevel1',
                style: {
                    label: function (ele) {
                        return ele.data("label");
                    },
                }
            },
            {
                selector: '.edgeShowText',
                style: {
                    label: function (ele) {
                        return ele.data("label");
                    },
                }
            },
            {
                selector: '.lineFixed',// 鍔犺浇瀹屾垚鍚庯紝鍔犺浇璇ョ被锛屼慨澶嶇嚎鏈夐敮榻跨殑闂
                style: {
                    'overlay-opacity':0,
                }
            },
        ],
    });

    cy.on('click', 'node', function(evt){
        if(evt.target._private.style['z-index'].value == 20) { // 闈炴殫娣＄姸鎬�
            _isFocus = true;
            var node = evt.target;

            highLight([node._private.data.id],cy);

            if(node.hasClass("nodeActive")){
                activeNode = null;
                $('#company-detail').hide();
                node.removeClass("nodeActive");
                cy.collection("edge").removeClass("edgeActive");
            }else{
                var nodeData = node._private.data;
                if(nodeData.type == 'Company'){
                    showDetail2(nodeData.keyNo,'company_muhou3');
                    cy.collection("node").addClass('nodeDull');
                } else {
                    showDetail2(nodeData.keyNo,'company_muhou3','person');
                    cy.collection("node").addClass('nodeDull');
                }

                activeNode = node;
                cy.collection("node").removeClass("nodeActive");

                cy.collection("edge").removeClass("edgeActive");
                node.addClass("nodeActive");
                node.neighborhood("edge").removeClass("opacity");
                node.neighborhood("edge").addClass("edgeActive");
                node.neighborhood("edge").connectedNodes().removeClass("opacity");
            }
            //_firstTab = false;
        } else {
            _isFocus = false;
            activeNode = null;
            cy.collection("node").removeClass("nodeActive");
            $('.tp-detail').fadeOut();
            cancelHighLight();
        }
    });
    var showTipsTime = null;
    cy.on('mouseover', 'node', function(evt){
        if(evt.target._private.style['z-index'].value == 20){ // 闈炴殫娣＄姸鎬�
            //
            $("#Main").css("cursor","pointer");

            //
            var node = evt.target;
            node.addClass('nodeHover');
            if(!_isFocus){
                cy.collection("edge").removeClass("edgeShow");
                cy.collection("edge").removeClass("edgeActive");
                node.neighborhood("edge").addClass("edgeActive");
            }

            // 鎻愮ず
            clearTimeout(showTipsTime);
            //if(node._private.data.name.length > 13 || (node._private.data.keyNo[0] == 'p' && node._private.data.name.length > 3) || node._private.data.layout.revel > 2){
            if(node._private.data.name.length > 13 || (node._private.data.keyNo && node._private.data.keyNo[0] == 'p' && node._private.data.name.length > 3)){
                showTipsTime = setTimeout(function () {
                    var name = node._private.data.name;


                    // 鏄剧ず鍦ㄨ妭鐐逛綅缃�
                    /*var tipWidth = name.length * 12 + 16;
                    var x = node._private.data.d3x + 655 - (tipWidth / 2);
                    var y = node._private.data.d3y + 598;
                    if(node._private.data.type == 'Person'){
                        y = node._private.data.d3y + 590;
                    }*/


                    // 鏄剧ず鍦ㄩ紶鏍囦綅缃�
                    var event = evt.originalEvent ||window.event;
                    var x = event.clientX + 10;
                    var y = event.clientY + 10;

                    var html = "<div class='tips' style='font-size:12px;background:white;box-shadow:0px 0px 3px #999;border-radius:1px;opacity:1;padding:1px;padding-left:8px;padding-right:8px;display:none;position: absolute;left:"+ x +"px;top:"+ y +"px;'>"+ name +"</div>";
                    $('body').append($(html));
                    $('.tips').fadeIn();
                },600);
            }
        }
    });
    cy.on('mouseout', 'node', function(evt){
        $("#Main").css("cursor","default");

        // 鎻愮ず
        $('.tips').fadeOut(function () {
            $('.tips').remove();
        });

        clearTimeout(showTipsTime);

        //
        var node = evt.target;
        node.removeClass('nodeHover');
        if(!_isFocus){
            cy.collection("edge").removeClass("edgeActive");
            /*if(moveTimeer){
                clearTimeout(moveTimeer);
            }*/
            /*moveTimeer = setTimeout(function() {
                cy.collection("edge").addClass("edgeActive");
                //cy.collection("edge").addClass("edgeShow");
            }, 300);
            if(activeNode){
                activeNode.neighborhood("edge").addClass("edgeActive");
            }*/
        }
    });
    cy.on('mouseover', 'edge', function(evt){
        if(!_isFocus){
            var edge = evt.target;
            /*if(moveTimeer){
                clearTimeout(moveTimeer);
            }*/
            cy.collection("edge").removeClass("edgeActive");
            edge.addClass("edgeActive");
            /*if(activeNode){
                activeNode.neighborhood("edge").addClass("edgeActive");
            }*/
        }

    });
    cy.on('mouseout', 'edge', function(evt){
        if(!_isFocus){
            var edge = evt.target;
            edge.removeClass("edgeActive");
            // moveTimeer = setTimeout(function() {
            //     cy.collection("edge").addClass("edgeActive");
            //     //cy.collection("edge").addClass("edgeShow");
            // }, 400);
            if(activeNode){
                activeNode.neighborhood("edge").addClass("edgeActive");
            }
        }

    });
    cy.on('vmousedown', 'node', function(evt){
        var node = evt.target;
        if(!_isFocus){
            highLight([node._private.data.id],cy);
        }
    });
    cy.on('tapend', 'node', function(evt){
        if(!_isFocus){
            cancelHighLight();
        }
    });

    cy.on('click', 'edge', function(evt){
        _isFocus = false;
        activeNode = null;
        cy.collection("node").removeClass("nodeActive");
        $('.tp-detail').fadeOut();
        cancelHighLight();
    });
    cy.on('click', function(event){
        var evtTarget = event.target;

        if( evtTarget === cy ){
            _isFocus = false;
            activeNode = null;
            cy.collection("node").removeClass("nodeActive");
            $('.tp-detail').fadeOut();
            cancelHighLight();
            focusCancel();
            filterReset();

            //cy.collection("edge").addClass("edgeActive");
        } else {
            //console.log('tap on some element');
        }
    });

    cy.on('zoom',function(){
        if(cy.zoom()<0.5){
            cy.collection("node").addClass("hidetext");
            cy.collection("edge").addClass("hidetext");
        }else{
            cy.collection("node").removeClass("hidetext");
            cy.collection("edge").removeClass("hidetext");
        }

        // 鍔犺浇瀹屾垚鍚庯紝鍔犺浇璇ョ被锛屼慨澶嶇嚎鏈夐敮榻跨殑闂
        setTimeout(function () {
            cy.collection("edge").removeClass("lineFixed");
            cy.collection("edge").addClass("lineFixed");
        },200);
    })

    cy.on('pan',function () {
        // 鍔犺浇瀹屾垚鍚庯紝鍔犺浇璇ョ被锛屼慨澶嶇嚎鏈夐敮榻跨殑闂
        setTimeout(function () {
            cy.collection("edge").removeClass("lineFixed");
            cy.collection("edge").addClass("lineFixed");
        },200);
    });

    // 瀹氫綅
    cy.nodes().positions(function( node, i ){

        // 淇濇寔灞呬腑
        if(node._private.data.keyNo == _currentKeyNo){
            var position= cy.pan();
            cy.pan({
                x: position.x-node._private.data.d3x,
                y: position.y-node._private.data.d3y
            });
        }

        //
        return {
            x: node._private.data.d3x,
            y: node._private.data.d3y
        };
    });

    cy.ready(function () {


        if(!$('#TrTxt').hasClass('active')){
            $('#TrTxt').click();
        }

        cy.zoom({
            level: 1.0000095043745896, // the zoom level
        });
        $("#load_data").hide();
        //cy.$('#'+id).emit('tap');
        //cy.center(cy.$('#'+id));
        //cy.collection("edge").addClass("edgeActive");

        // 鍔犺浇瀹屾垚鍚庯紝鍔犺浇璇ョ被锛屼慨澶嶇嚎鏈夐敮榻跨殑闂
        setTimeout(function () {
            cy.collection("edge").addClass("lineFixed");
        },400);

        // 棣栭〉鐨勬彃鍏ュ浘璋遍粯璁ら珮浜涓€灞�
        if(_rootData && _rootData.nodes.length > 30 && typeof _INSERT_URL != 'undefined' && _INSERT_URL){
            highLight([_rootNode.nodeId],cy);
        }
    });

    cy.nodes(function (node) {

        /*
        // 褰撳墠鏌ヨ鑺傜偣鍏崇郴鏂囧瓧鏄剧ず
        if(node._private.data.nodeId == _rootNode.nodeId){
            node.neighborhood("edge").addClass("edgeLevel1");
        }*/
    });
}
function highLight(nodeIds,cy) {
    cy.collection("node").removeClass("nodeActive");
    cy.collection("edge").removeClass("edgeActive");
    cy.collection("node").addClass('dull');
    cy.collection("edge").addClass('dull');

    for(var i = 0; i < nodeIds.length; i++){
        var nodeId = nodeIds[i];
        cy.nodes(function (node) {
            var nodeData  = node._private.data;
            if(nodeData.id == nodeId){
                node.removeClass('dull');
                //node.addClass('nodeActive');
                node.neighborhood("edge").removeClass("dull");
                node.neighborhood("edge").addClass("edgeActive");
                node.neighborhood("edge").connectedNodes().removeClass("dull");
                //node.neighborhood("edge").connectedNodes().addClass("nodeActive");
            }
        });
    }
}
function highLightFilter(nodeIds,cy) {
    function isInNodeIds(nodeId) {
        for(var i = 0; i < nodeIds.length; i++){
            if(nodeId == nodeIds[i]){
                return true;
                break;
            }
        }
        return false;
    }

    cy.collection("node").removeClass("nodeActive");
    cy.collection("edge").removeClass("edgeActive");
    cy.collection("node").addClass('dull');
    cy.collection("edge").addClass('dull');


    for(var i = 0; i < nodeIds.length; i++){
        var nodeId = nodeIds[i];
        cy.nodes(function (node) {
            var nodeData  = node._private.data;
            if(nodeData.id == nodeId){
                node.removeClass('dull');
                //node.addClass('nodeActive');
                /*node.neighborhood("edge").removeClass("dull");
                node.neighborhood("edge").addClass("edgeActive");
                node.neighborhood("edge").connectedNodes().removeClass("dull");*/
                //node.neighborhood("edge").connectedNodes().addClass("nodeActive");
            }
        });
    }

    cy.edges(function (edge) {
        var data = edge._private.data;
        if(isInNodeIds(data.target) && isInNodeIds(data.source)){
            edge.removeClass('dull');
            edge.addClass('edgeActive');
        }
    });
}
function cancelHighLight() {
    cy.collection("node").removeClass("nodeActive");
    cy.collection("edge").removeClass("edgeActive");
    cy.collection("node").removeClass('dull');
    cy.collection("edge").removeClass('dull');
}

/**鍏朵粬*/

function getD3Position(graph) {
    getLayoutNode(graph);

    function filterLinks1(graph) {
        // 绛涢€夌敤浜庡竷灞€鐨刲inks
        var layoutLinks = [];
        for(var i = 0; i < graph.links.length; i++){
            var link = graph.links[i];
            var sourceLevel = link.sourceNode.layout.level;
            var targetLevel = link.targetNode.layout.level;
            var sourceNode = link.sourceNode;
            var targetNode = link.targetNode;
//            sourceNode.layout.isSetLink = false;
//            targetNode.layout.isSetLink = false;


//            if(!sourceNode.layout.isSetLink && !targetNode.layout.isSetLink){
            if((sourceLevel == 1 && targetLevel == 2) || (sourceLevel == 2 && targetLevel == 1) ){
//                    sourceNode.layout.isSetLink = true;
//                    targetNode.layout.isSetLink = true;
                layoutLinks.push(link);
            }
            if((sourceLevel == 2 && targetLevel == 3) || (sourceLevel == 3 && targetLevel == 2) ){
//                    sourceNode.layout.isSetLink = true;
//                    targetNode.layout.isSetLink = true;
                layoutLinks.push(link);
            }
//            }

        }

        layoutLinks.forEach(function (link,i) {

            if(link.targetNode.layout.level == 3){
                layoutLinks.forEach(function (alink,j) {
                    if(alink.linkId != link.linkId &&
                        (alink.targetNode.nodeId == link.targetNode.nodeId || alink.sourceNode.nodeId == link.targetNode.nodeId)){
                        layoutLinks.splice(j,1);
                    }
                })
            }

            if(link.sourceNode.layout.level == 3){
                layoutLinks.forEach(function (alink,j) {
                    if(alink.linkId != link.linkId &&
                        (alink.targetNode.nodeId == link.sourceNode.nodeId || alink.sourceNode.nodeId == link.sourceNode.nodeId)){
                        layoutLinks.splice(j,1);
                    }
                })
            }
        })

        return layoutLinks;
    }

    function filterLinks2(graph) {
        // 绛涢€夌敤浜庡竷灞€鐨刲inks
        var layoutLinks = [];
        for(var i = 0; i < graph.links.length; i++){
            var link = graph.links[i];
            var sourceLevel = link.sourceNode.layout.level;
            var targetLevel = link.targetNode.layout.level;
            var sourceNode = link.sourceNode;
            var targetNode = link.targetNode;


            if((sourceLevel == 1 && targetLevel == 2) || (sourceLevel == 2 && targetLevel == 1) ){
                layoutLinks.push(link);
            }
            if((sourceLevel == 2 && targetLevel == 3) || (sourceLevel == 3 && targetLevel == 2) ){
                layoutLinks.push(link);
            }

        }

        return layoutLinks;
    }

    function initD3Data(graph) { //
        function getIndex(val,arr) {
            var index = 0;
            for(var i = 0; i < arr.length; i++){
                var obj = arr[i];
                if(val == obj.nodeId){
                    index = i;
                    break;
                }
            }
            return index;
        }

        /*灏佽绗﹀悎d3鐨勬暟鎹�*/
        for(var i = 0; i < graph.nodes.length; i++){
            var node = graph.nodes[i];
            node.id = node.nodeId;
        }

        for(var i = 0; i < graph.links.length; i++){
            var link = graph.links[i];
            link.source = getIndex(link.sourceNode.nodeId, graph.nodes) ;
            link.target = getIndex(link.targetNode.nodeId, graph.nodes) ;
            link.index = i; //
        }

        graph.layoutLinks = filterLinks1(graph);

        // 鍥寸粫鑺傜偣鏈€澶ф暟鍊�
        setSingleLinkNodes(graph.layoutLinks);
        graph.nodes.forEach(function(node,i){
            if(node.layout.singleLinkChildren.length && _maxChildrenLength < node.layout.singleLinkChildren.length){
                _maxChildrenLength = node.layout.singleLinkChildren.length
            }
        })
        //console.log('鍥寸粫鑺傜偣鏈€澶ф暟鍊�:' + _maxChildrenLength);
    }

    initD3Data(graph); //

    var width = $("#MainD3 svg").width();
    var height = $("#MainD3 svg").height();

    var strength = -600,distanceMax = 330,theta = 0,distance = 130,colideRadius = 35,distanceMin = 400;
    // 鏍规嵁鑺傜偣鏁伴噺璋冭妭
    if(graph.nodes.length < 50 ){
        strength = -800;distanceMax = 400;
    } else if( graph.nodes.length > 50 && graph.nodes.length < 100 ){
        strength = -800;distanceMax = 350;distance = 130;colideRadius = 35;
    } else if(graph.nodes.length > 100 && graph.nodes.length < 150){
        strength = -900;distanceMax = 450;
    } else if (graph.nodes.length > 150 && graph.nodes.length < 200) {
        strength = -1000; distanceMax = 500;
    } else if (graph.nodes.length > 200) {
        strength = -1600; distanceMax = 500;theta = 0.6,distance = 100,colideRadius = 35;
    }
    // 鏍规嵁鍥寸粫鏁伴噺璋冭妭
    if(_maxChildrenLength > 50 && _maxChildrenLength < 100){
        strength = -2000; distanceMax = 500;
    } else if(_maxChildrenLength > 1000 && _maxChildrenLength < 2000) {
        strength = -4000; distanceMax = 1500;
    }

    d3.forceSimulation(graph.nodes)
        .force('charge', d3.forceManyBody().strength(strength).distanceMax(distanceMax).theta(theta))
        .force('link', d3.forceLink(graph.layoutLinks).distance(distance))
        .force('center', d3.forceCenter(width / 2, height / 2))
        .force('collide', d3.forceCollide().radius(function () { return colideRadius;}))
    //.on('tick',ticked);
}

/** d3 svg */
/*var svg = d3.select('svg');
svg.selectAll('g').remove();// 娓呯┖
var svg_g = svg.append("g")

// 缁撶偣
var svg_nodes = svg_g.selectAll('circle')
    .enter().append('circle')
    .attr('r', function (d) {
        if(d.data.obj.labels[0] == 'Company'){
            return 33;
        } else {
            return 24;
        }
    })
    .attr('fill', function(d, i) {
        return d.data.color;
    })
    .style('opacity',1)*/
/** end d3 svg */


/*function ticked() {
    svg_nodes.attr("cx", function(d) {  return d.x; })
        .attr("cy", function(d) { return d.y; });
}*/

//璁剧疆绗﹀悎Layout鐨刵ode
function getLayoutNode(graphData) {
    var layoutNode = { current : _rootNode, level1 : [], level2 : [], level3 : [], level4 : [], level5 : [],other:[]};

    graphData.nodes.forEach(function (node,i) {
        switch (node.layout.level) {
            case 1: layoutNode.level1.push(node);break;
            case 2: layoutNode.level2.push(node);break;
            case 3: layoutNode.level3.push(node);break;
            case 4: layoutNode.level4.push(node);break;
            case 5: layoutNode.level5.push(node);break;
            default:layoutNode.other.push(node);break;
        }
    });

    _layoutNode = layoutNode;

    return layoutNode;
}
//灏唕ootData杞崲鎴恈y鍥捐氨妗嗘灦鎵€闇€瑕佺殑鏁版嵁缁撴瀯
function transformData(graphData) {
    function getLinkColor(type) {
        if(type == 'INVEST'){
            return _COLOR.line.invest;
        } else if(type == 'EMPLOY') {
            return _COLOR.line.employ;
        } else if(type == 'LEGAL') {
            return _COLOR.line.legal;
        }
    }
    function getLinkLabel(link) {
        var type = link.data.obj.type, role = link.data.obj.properties.role;
        if(type == 'INVEST'){
            return '鎶曡祫';
        } else if(type == 'EMPLOY') {
            return (role ? role : '浠昏亴');
        } else if(type == 'LEGAL') {
            return '娉曞畾浠ｈ〃浜�';
        }
    }
    //getLayoutNode(graphData);

    //
    id = graphData.nodes[0].nodeId;
    var els = {};
    els.nodes = [];
    els.edges = [];

    graphData.links.forEach(function (link,i) {
        var color = getLinkColor(link.data.obj.type);
        var label = getLinkLabel(link);

        els.edges.push({
            data:{
                data:link.data,
                color: color,
                id:link.linkId,
                label:label,
                source:link.sourceNode.nodeId,
                target:link.targetNode.nodeId
            },
            classes:'autorotate'
        });
    });

    graphData.nodes.forEach(function (node) {
        els.nodes.push({
            data:{
                nodeId:node.nodeId,
                type:node.data.obj.labels[0],
                keyNo:node.data.obj.properties.keyNo,
                data:node.data,
                id:node.nodeId,
                name:node.data.obj.properties.name,
                category:node.data.category,
                color:node.data.color,
                borderColor:node.data.strokeColor,
                layout:node.layout,
                d3x:node.x,
                d3y:node.y,
                hasImage:node.data.obj.properties.hasImage,
                //labelLine:1 // 瑙ｅ喅鏂囧瓧琛岃窛闂锛岀1琛�
            }
        });
    });

    return els;
}
// 鍥捐氨銆佺瓫閫夐潰鏉挎洿鏂�
function domUpdate(graphData) {
    getD3Position(graphData);

    setTimeout(function () {
        drawGraph(transformData(graphData));
    },500);

    selPanelUpdateList(graphData.nodes,graphData.links,true);
}

//鎴浘2
function downImg(imgdata){
    var type = 'png';
    //灏唌ime-type鏀逛负image/octet-stream,寮哄埗璁╂祻瑙堝櫒涓嬭浇
    var fixtype = function (type) {
        type = type.toLocaleLowerCase().replace(/jpg/i, 'jpeg');
        var r = type.match(/png|jpeg|bmp|gif/)[0];
        return 'image/' + r;
    }
    imgdata = imgdata.replace(fixtype(type), 'image/octet-stream')
    //灏嗗浘鐗囦繚瀛樺埌鏈湴
    var saveFile = function (data, filename) {
        var link = document.createElement('a');
        link.href = data;
        link.download = filename;
        var event = document.createEvent('MouseEvents');
        event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
        link.dispatchEvent(event);
    }
    var filename = new Date().toLocaleDateString() + '.' + type;
    saveFile(imgdata, filename);
}
//鎴浘2 IE
function downloadimgIE(canvas) {
    function post(URL, PARAMS) {
        var temp = document.createElement("form");
        temp.action = URL;
        temp.enctype = "multipart/form-data";
        temp.method = "post";
        temp.style.display = "none";
        for (var x in PARAMS) {
            var opt = document.createElement("textarea");
            opt.name = x;
            opt.value = PARAMS[x];
            temp.appendChild(opt);
        }
        document.body.appendChild(temp);
        temp.submit();
        return temp;
    }

    var qual = 1;
    if(canvas.width>3000){
        qual = 0.5;
    }else if(canvas.width>5000){
        qual = 0.4;
    }
    //璁剧疆淇濆瓨鍥剧墖鐨勭被鍨�
    var imgdata = canvas.toDataURL('image/jpeg',qual);
    //var filename = '{{$smarty.get.name}}鐨勫叧鑱斿浘璋盻'+new Date().toLocaleDateString() + '.jpeg';
    // var filename = _FILENAME + '鐨勫叧鑱斿浘璋�.png';
    var filename = '浼佹煡鏌鍥捐氨.png';
    post(INDEX_URL+'cms_downloadimg?filename='+filename, {img:imgdata});
}
//鎴浘1
function canvasImg(imgData){
    var img = new Image();

    img.onload = function(e){

        var canvas = document.createElement('canvas');  //鍑嗗绌虹敾甯�
        canvas.width = img.width;
        canvas.height = img.height;
        var context = canvas.getContext('2d');  //鍙栧緱鐢诲竷鐨�2d缁樺浘涓婁笅鏂�
        context.fillStyle = "#fff";
        context.fillRect(0,0,canvas.width,canvas.height);

        //鐢绘按鍗�
        // var shuiying = new Image();
        // shuiying.src="/material/theme/chacha/cms/v2/images/shuiying2.png";
        // if(canvas.width>320){
        //     context.drawImage(shuiying, canvas.width/2-160, canvas.height/2-80,320,160);
        // }else{
        //     context.drawImage(shuiying, canvas.width/2-80, canvas.height/2-40,160,80);
        // }

        var shuiying = new Image();
        shuiying.src="/material/theme/chacha/cms/v2/images/shuiying2.png";
        for(var i=0;i < canvas.width+100; i+=600){
            for(var j=0; j< canvas.height+100; j+=456){
                context.drawImage(shuiying, i, j);
            }
        }

        //鐢诲浘璋�
        context.drawImage(img, 0, 0);

        if(canvas.width>400){
            var marker = '鍏宠仈鍥捐氨鐢变紒鏌ユ煡鍩轰簬鍏紑淇℃伅鍒╃敤澶ф暟鎹垎鏋愬紩鎿庣嫭瀹剁敓鎴�';
            context.font = "28px 寰蒋闆呴粦";
            context.fillStyle = "#aaaaaa";
            context.fillText(marker, canvas.width/2-context.measureText(marker).width/2, canvas.height-30);
        }

        downloadimgIE(canvas);

        /*if(!!window.ActiveXObject || "ActiveXObject" in window){ // ie
            context.drawImage(shuiying, canvas.width/2-160, canvas.height/2-80,320,160);
            downloadimgIE(canvas);
        } else {
            downImg(canvas.toDataURL('image/jpeg',1));
        }*/
    }

    img.src = imgData;
}


function getData(keyNo,param){
    var defaultParam = {
        keyNo:keyNo,
    }

    if( keyNo.substr(0, 1) == "p"){
        defaultParam.startLabel = 'Person';
    }

    param = $.extend(defaultParam,param);

    $("#load_data").show();

    var url = INDEX_URL + '/company_muhouPersonAction';
    if(_TUPU_URL){
        url = _TUPU_URL;
    }
    if(typeof _INSERT_URL != 'undefined' && _INSERT_URL){
        url = _INSERT_URL;
    }

    $.ajax({
        url:"./1.json",
        //url:'http://10.0.0.38:9600/api/Graph/GetCompanyGraph',
        type: 'GET',
        data:param,
        dataType: 'JSON',
        success: function (re){

            /*澶у挅鎼滅储鍦板潃鏍忚ˉ瓒砶eyNo*/
            if(window.location.pathname == '/company_muhouperson'){
                history.pushState('','','company_muhouperson?keyNo='+_currentKeyNo);
            }

            re = re.success;

            if( !re || re.results == undefined || !re.results[0] || !re.results[0].data.length || re.results[0].data[0].graph.nodes.length == 0){
                $("#load_data").hide();
                $(".printLogo").hide();
                $(".tp-foot").hide();
                $("#Main").hide();
                $('#no_data').show();
                return;
            } else {
                $(".printLogo").show();
                $(".tp-foot").show();
                $("#Main").show();
                $('#no_data').hide();
            }

            _rootData = getRootData(re.results[0].data);

            domUpdate(_rootData);
        },error:function(data){
            $("#load_data").hide();
            $(".printLogo").hide();
            $(".tp-foot").hide();
            $("#Main").hide();
            $('#no_data').show();
        }
    });
}

function refresh(keyNo) {
    $('.company-detail').fadeOut();
    $('#MainCy').html('');
    _currentKeyNo = keyNo;
    $('#TrTxt').removeClass('active');
    getData(_currentKeyNo);
    focusCancel();
    filterReset();

    //椤甸潰
    try{
        hideSearchBoxHide();
    }catch (e){}
}

/**璇︽儏寮圭獥*/
function showPerTupu(type){
    var canshow = $("#ShowPerTupu").attr('canshow');
    if(canshow){
        var keyNo = $("#ShowPerTupu").attr('keyno');
        refresh(keyNo);
    }
}
/*鍏抽棴璇︽儏*/
function popclose(dom){
    $(dom).parent().parent().fadeOut();
}
/*浜虹墿澶村儚娌℃湁鏃跺鐞�*/
function personImgErr() {
    var name = $(".ea_name").text();
    $('#face_oss').hide();
    $('.ea_defaultImg').show();
    $('.ea_defaultImg').text(name[0]);
}
/**END 璇︽儏寮圭獥*/


window.onresize=function(){
    resizeScreen();
    printLogoFixed();
}
$(document).ready(function () {


    printLogoFixed();

    _currentKeyNo = getQueryString('keyNo');
    if(!_currentKeyNo){
        if(typeof _HOTPERSON != 'undefined' && _HOTPERSON){
            _currentKeyNo = _HOTPERSON;
        } else {
            _currentKeyNo = '';
        }
    }

    getData(_currentKeyNo);


    /**绛涢€夐潰鏉�*/

    // 灞傜骇绛涢€�
    $("#ShowLevel > a").click(function () {
        $('#ShowLevel > a').removeClass('active');
        $(this).addClass('active');

        var level = parseInt($(this).attr('level'));
        $('#SelPanel').attr('param-level',level);
        filter(_rootData);
    });//#ShowLevel
    // 鐘舵€佺瓫閫�
    $("#ShowStatus > a").click(function () {
        $('#ShowStatus > a').removeClass('active');
        $(this).addClass('active');

        var status = $(this).attr('status');
        $('#SelPanel').attr('param-status',status);
        filter(_rootData);
    });//#ShowLevel
    // 鎸佽偂绛涢€�
    var inputEvent = (!!window.ActiveXObject || "ActiveXObject" in window) ? 'change' : 'input';
    $('#inputRange').bind(inputEvent, function(e){

        var value = $('#inputRange').val();
        $('#rangeValue').text(value);
        $('#inputRange').css('background-size', value + '% 100%' );
        $('#RangeLabel span').text(value + '%');

        $('#SelPanel').attr('param-num',value);
        filter(_rootData);
    });
    // 鎶曡祫绛涢€�
    $("#ShowInvest > a").click(function () {
        $('#ShowInvest > a').removeClass('active');
        $(this).addClass('active');

        var invest = $(this).attr('invest');
        $('#SelPanel').attr('param-invest',invest);
        filter(_rootData);
    });//#ShowLevel
    // 鍏抽棴
    $('.tp-sel-close span').click(function () {
        selPanelHide();
    });
    // 鑱氱劍
    $('#FocusBt').click(function () {
        var status = $('#FocusBt').text();
        if(!$(this).hasClass('focusDisable')){
            if(status == '鑱氱劍'){
                if(!$('#FocusInput').val()){
                    faldia({content:'璇风偣鍑婚€夊彇缁撶偣'});
                    return;
                }

                var nodeId = $('#FocusInput').attr('node_id')
                if(!nodeId){
                    return;
                } else {
                    $('#FocusBt').text('鍙栨秷');
                    highLight([nodeId],cy);
                }
            } else if (status == '鍙栨秷'){
                focusCancel();
            }
        }

    });
    // 杈撳叆妗�
    $('#FocusInput').keyup(function () {
        $('.tp-list').html('');
        var _this = $(this);
        var keyword = _this.val();

        if(keyword){
            $('#ClearInput').show();
        } else {
            $('#ClearInput').hide();
        }

        setTimeout(function () {
            var selNodes = [];
            _rootData.nodes.forEach(function (node) {
                var name = node.data.obj.properties.name;
                if(name.match(keyword)){
                    selNodes.push(node);
                }
            });

            selPanelUpdateList(selNodes,_rootData.links,false);
        },500);
    });
    $('#ClearInput').click(function () {
        focusCancel();
    });

    /**璇︽儏闈㈡澘*/

    $('.tp-detail-close span').click(function () {
        //cancelHighLight();
        $('.tp-detail').fadeOut();
    });
    /*$('#ViewTupu').click(function () {
        var guid = $(this).attr('guid');
        init(guid);
    });*/

    /**渚ц竟鏍�*/

    $('#TrSel').click(function () {
        var _this = $(this);
        if(_this.hasClass('active')){
            selPanelHide();
        } else {
            selPanelShow();
        }
    });
    $('#TrFullScreen').click(function () {
        var old = cy.pan();
        var distance = 60;
        if(isFullScreen()){
            cy.pan({
                x:old.x,
                y:old.y - distance
            });
            exitFullScreen();
        } else {
            cy.pan({
                x:old.x,
                y:old.y + distance
            });
            launchFullScreen($('#Main')[0]);
        }
    });
    $('#TrRefresh').click(function () {
        refresh(_currentKeyNo);
    });
    $('#TrSave').click(function () {
        if(!$('#TrTxt').hasClass('active')){
            $('#TrTxt').click();
        }

        canvasImg(cy.png({full:true,bg:'#0000',scale:1.8}));
    });
});

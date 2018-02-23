package bin.leblanc.workflow;

import bin.leblanc.workflow.enums.DealType;
import bin.leblanc.workflow.exception.*;
import bin.leblanc.workflow.field.InformationField;
import bin.leblanc.workflow.metadata.ILogicExpression;
import bin.leblanc.workflow.node.EndNode;
import bin.leblanc.workflow.node.InformationNode;
import bin.leblanc.workflow.node.LogicNode;
import bin.leblanc.workflow.node.ReviewNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class WorkflowTask implements Serializable{

    private static final long serialVersionUID = 1L;


    @AllArgsConstructor
    @Getter
    @Setter
    public static class Holder{
        Integer index;
        Object data;
    }


    @Getter
    @Setter
    private Object starter;

    private WorkflowModel workflowModel;

    @Getter
    private int currentIndex = -1;

    @Getter
    private boolean finished = false;

    @Getter
    private boolean stoped = false;

    //节点处理循序
    @Getter
    private List<Holder> moveList = new ArrayList<>();

    @Getter
    private Map<Integer,Object> data = new LinkedHashMap<>();

    public WorkflowTask(WorkflowModel model){
        workflowModel = model;
        currentIndex = 0;
//        moveList.add(currentIndex);
    }

    public void submitData(Object role, Object object) throws NoRequiredFieldException, NoPermissionException, ReviewFailedException, NoElseExpressionException, NoWorkflowNodeException {
        if(finished || stoped){
            throw new NoPermissionException();
        }
        //检查是否可以提交这个节点
        if(!canSubmit(role)){
            throw new NoPermissionException();
        }

        WorkflowNode currentNode = workflowModel.getNodeList().get(currentIndex);

        //资料节点的处理
        if(currentNode instanceof InformationNode){
            Map<String,Object> mapData = (Map<String, Object>) object;
            Set<InformationField> fields = ((InformationNode) currentNode).getFields();
            for (InformationField field : fields) {
                //如果这个字段是必填且没有传值，那么抛出异常
                if(field.isRequired() && !mapData.containsKey(field.getName())){
                    throw new NoRequiredFieldException();
                }
                //校验类型，暂且略过
                //TODO: 需要根据field类型校验每个提交的值
            }
            //删除不需要的提交资料
            Set<String> removeKeys = mapData
                    .keySet()
                    .stream()
                    .filter(key -> {
                        return !fields.stream().anyMatch(field -> field.getName().equals(key));
                    })
                    .collect(Collectors.toSet());
            removeKeys.forEach(key -> {
                mapData.remove(key);
            });

            //放入资料池
            this.data.put(currentIndex,object);
            //运行路线
            moveList.add(new Holder(currentIndex,object));

            this.goNextNode();
        }
        //审核节点的处理
        else if(currentNode instanceof ReviewNode){
            DealType blData = (DealType) object;
            if(!this.data.containsKey(currentIndex)){
                Map<?,?> map = new LinkedHashMap<>();
                this.data.put(currentIndex,map);
                moveList.add(new Holder(currentIndex,map));
            }
            Map<Object,DealType> reviewData = (Map<Object, DealType>) this.data.get(currentIndex);
            int dealedNum = reviewData.size();
            if(dealedNum + 1 > ((ReviewNode) currentNode).getMaxNum()){
                throw new ReviewFailedException();
            }
            reviewData.put(role,blData);

            //如果已经审核通过
            long agreeNum = reviewData.keySet().stream().filter(key -> reviewData.get(key).equals(DealType.SUCCESS)).count();
            long disagreeNum = dealedNum + 1 - agreeNum;

            //如果审核已经失败（超过设定的人选择了驳回）
            if(disagreeNum >= ((ReviewNode) currentNode).getPassNum()){
                //如果选择驳回的人多
                long backToLastNum = reviewData.keySet().stream().filter(key -> reviewData.get(key).equals(DealType.BACK_TO_LAST)).count();
                long stopNum = disagreeNum - backToLastNum;
                //如果选择退回上一级的人多
                if(stopNum > backToLastNum){
                    destroy();
                }
                else{
                    backToLastNode();
                }
            }
            else if(agreeNum >= ((ReviewNode) currentNode).getPassNum()){
                this.goNextNode();
            }


            //继续等待下一个人操作
        }
    }

    /**
     * 判断某个人是否可以操作当前的节点
     * @return
     */
    public boolean canSubmit(Object role){
        if(finished || stoped){
            return false;
        }
        WorkflowNode currentNode = workflowModel.getNodeList().get(currentIndex);
        if(!currentNode.getDealers().contains(role)){
            return false;
        }
        //判断这个人是否已经操作过这个节点
        if(currentNode instanceof InformationNode){
            return !this.data.containsKey(role);
        }
        else if(currentNode instanceof ReviewNode){
            Map<Object,Boolean> reviewData = (Map<Object, Boolean>) this.data.get(currentIndex);
            return reviewData == null || !reviewData.containsKey(role);
        }
        return true;
    }


    public void complete(){
        finished = true;

        //这里需要回调结束的行为


    }

    public void goNextNode() throws NoElseExpressionException {
        log.info("开始下一个节点");

        currentIndex ++;

        //如果已经没有下一个节点了，那么结束任务
        WorkflowNode node = workflowModel.getNodeList().get(currentIndex);
        if(node == null){
            complete();
            return;
        }

        //如果下一个节点是结束节点
        if(node instanceof EndNode){
            log.info("工作流程结束");
            complete();
            return;
        }

        //如果是逻辑判断节点
        if(node instanceof LogicNode){
            log.info("执行逻辑节点");

            moveList.add(new Holder(currentIndex,new Object()));

            /**
             * 逻辑节点是指针对于当前节点的偏移，而不是真正的节点位置
             */
            //从第一个表达式开始判断
            logic : do{
                for (Map.Entry<ILogicExpression, Integer> iLogicExpressionIntegerEntry : ((LogicNode) node).getLogicMap().entrySet()) {
                      ILogicExpression logicExpression = iLogicExpressionIntegerEntry.getKey();
                      int successOffset = iLogicExpressionIntegerEntry.getValue();
                      if(logicExpression.call()){
                          currentIndex = currentIndex + successOffset - 1;
                          this.goNextNode();
                          break logic;
                      }
                }
                //如果一个判断都没有命中，那么走else
                if(((LogicNode) node).getElseOffset() == -1){
                    throw new NoElseExpressionException();
                }
                currentIndex = currentIndex + ((LogicNode) node).getElseOffset() - 1;
                this.goNextNode();

            }while(false);

        }

    }

    public void destroy(){
        stoped = true;
        //中止任务线程
    }

    /**
     * 退回上一级需要略过逻辑节点，否则会造成死循环
     */
    public void backToLastNode() throws NoWorkflowNodeException, NoElseExpressionException {
        //从movelist开始回溯
        //modelist的最后是当前的节点
        int ptr = moveList.size() - 1;
        while(ptr-- > 0){
            WorkflowNode node = workflowModel.getNodeList().get(ptr);
            if(node == null){
                throw new NoWorkflowNodeException();
            }
            //抹除路线上的数据
            if(data.containsKey(ptr)){
                data.remove(ptr);
            }
            if(node instanceof LogicNode){
                continue;
            }
            else{
                currentIndex = ptr - 1;
                goNextNode();
                return;
            }
        }

        //到了这里肯定要报错的
        throw new NoWorkflowNodeException();
    }


}

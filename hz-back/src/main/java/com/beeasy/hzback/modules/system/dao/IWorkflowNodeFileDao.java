package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.WorkflowNodeFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface IWorkflowNodeFileDao extends JpaRepository<WorkflowNodeFile,Long>{

//    @Query(value = "select count(f) from WorkflowNodeFile f where f.userId = :uid and f.id = :id")
//    int checkAuth(@Param("uid") long uid, @Param("id") long id);

    @Modifying
    @Query(value = "update WorkflowNodeFile f set f.tags = :tags where f.id = :id and f.userId = :uid")
    int updateNodeFileTags(@Param("uid") long uid, @Param("id") long id, @Param("tags") String tags);

    @Modifying
    @Query(value = "update WorkflowNodeFile f set f.fileName = :name where f.id = :id and f.userId = :uid")
    int updateNodeFileName(@Param("uid") long uid, @Param("id") long id, @Param("name") String name);
}

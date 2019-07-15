package com.beeasy.hzbpm.ctrl;

import com.github.llyb120.nami.core.MultipartFile;
import com.github.llyb120.nami.core.Obj;
import com.github.llyb120.nami.core.R;
import com.mongodb.gridfs.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.BufferedInputStream;

public class MongoControlller {
    private GridFsTemplate gridFsTemplate;

    public R ff(Obj query){
        MultipartFile file = null;

//        ObjectId savedFile = gridFsTemplate.store(null, "", "", null);

        return R.ok();
    }
}

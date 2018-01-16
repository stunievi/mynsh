package com.beeasy.hzback.modules.setting.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.setting.dao.IWorkDao;
import com.beeasy.hzback.modules.setting.entity.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin/setting/work")
public class WorkController {

    public static String failedUrl = "redirect:list";


    @Autowired
    IWorkDao workDao;

    @GetMapping("/list")
    public String list(
            Model model,
            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {

        model.addAttribute("list", workDao.findAll(pageable));

        return "setting/work_list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("item", new Work());
        return "setting/work_edit";
    }

    @GetMapping("/edit")
    public String edit(Model model, Integer id) {
        if (id == null) return failedUrl;
        Work work = workDao.findOne(id);
        if (work == null) return failedUrl;
        model.addAttribute("item", work);
        return "setting/work_edit";
    }


    /**
     * 编辑
     */
    @PostMapping("/edit")
    @ResponseBody
    public Result edit(
            @Valid Work work,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return Result.error(bindingResult);
        }

        return Result.ok();
    }

    /**
     * 修改
     */


    /**
     * 删除
     */
    @PostMapping("/delete")
    @ResponseBody
    public Result delete(Integer id) {
        if (id == null) return Result.error();

        return Result.ok();
    }
}

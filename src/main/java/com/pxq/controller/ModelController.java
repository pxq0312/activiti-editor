package com.pxq.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pxq.constant.ModelDataJsonConstants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/model")
public class ModelController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/list")
    public Map<String, Object> list() {
        List<Model> list = repositoryService.createModelQuery().orderByLastUpdateTime().desc().list();
        Map<String, Object> map = new HashMap<>();
        map.put("data", list);
        map.put("code", 0);
        map.put("msg", "success");
        return map;
    }

    @GetMapping("/create")
    public Map<String, Object> create() {
        String name = "新建流程";
        String key = "newProcess";
        String description = "请输入流程描述";

        try {
            Model model = repositoryService.newModel();
            ObjectNode modelNode = objectMapper.createObjectNode();
            modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
            modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            modelNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
            model.setName(name);
            model.setKey(key);
            model.setMetaInfo(modelNode.toString());
            repositoryService.saveModel(model);
            String id = model.getId();
            ObjectNode editorNode = objectMapper.createObjectNode();
            editorNode.put("id", "canvas");
            editorNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.putPOJO("stencilset", stencilSetNode);
            repositoryService.addModelEditorSource(id, editorNode.toString().getBytes("utf-8"));

            Map<String, Object> map = new HashMap<>();
            map.put("data", id);
            map.put("code", 0);
            map.put("msg", "success");
            return map;
        } catch (IOException e) {
            e.printStackTrace();
            Map<String, Object> map = new HashMap<>();
            map.put("code", -1);
            map.put("msg", "error");
            return map;
        }

    }

    @GetMapping("/delete")
    public Map<String, Object> delete(String id) {
        repositoryService.deleteModel(id);
        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "success");
        return map;
    }

}

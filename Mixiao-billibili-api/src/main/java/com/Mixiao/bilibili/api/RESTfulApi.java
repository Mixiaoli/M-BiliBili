package com.Mixiao.bilibili.api;

import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RESTfulApi {
    private final Map<Integer, Map<String ,Object>> dateMap;

    public RESTfulApi(){
        dateMap =  new HashMap<>();
        for (int i = 1;i<3;i++){
            Map<String,Object> date = new HashMap<>();
            date.put("id",i);
            date.put("name","name"+i);
            dateMap.put(i,date);
        }
    }
    //@PathVariable是用来关联路径上的参数 下面为id
    @GetMapping("/objects/{id}")
    public Map<String,Object> getDate(@PathVariable Integer id){
        return dateMap.get(id);
    }
    @DeleteMapping("/objects/{id}")
    public String deleteDate(@PathVariable Integer id){
        dateMap.remove(id);
        return  "delete success";
    }
    //RequestBody注解的作用是:把前端传来的参数自动封装到后端的javabean对象中。
    @PostMapping("/objects")
    public String postDate(@RequestBody Map<String,Object> date){
        Integer[] idArray = dateMap.keySet().toArray(new Integer[0]);
        Arrays.sort(idArray);
        int nextId = idArray[idArray.length -1] +1;
        dateMap.put(nextId,date);
        return "post success";
    }
    @PutMapping("/objects")
    public String putData(@RequestBody Map<String, Object> data){
        Integer id = Integer.valueOf(String.valueOf(data.get("id")));
        Map<String, Object> containedData = dateMap.get(id);
        if(containedData == null){
            Integer[] idArray = dateMap.keySet().toArray(new Integer[0]);
            Arrays.sort(idArray);
            int nextId = idArray[idArray.length - 1] + 1;
            dateMap.put(nextId, data);
        }else{
            dateMap.put(id, data);
        }
        return "put success";
    }
}

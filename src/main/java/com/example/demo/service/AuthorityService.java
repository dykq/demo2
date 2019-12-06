package com.example.demo.service;

import com.example.demo.entity.Authority;
import com.example.demo.entity.Menu;
import com.example.demo.mapper.AuthorityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorityService {
    @Autowired
    private AuthorityMapper authorityMapper;
    @Cacheable(value = "users",key = "#limited")
    public  List<Menu> getMenu(int limited){
        List<Authority> authorities=authorityMapper.selectByRole(limited);
      //  System.out.println(authorities);
        List<Menu> menu=new ArrayList<>(3);
        for(int i=0;i<authorities.size();i++){
            if(authorities.get(i).getPid()==null){
                menu.add(authorities.get(i));
            }
        }
        for (Menu menus:menu){
            menus.setChildMenus(getChild(menus.getJId(),authorities));
        }
        return menu;
    }
    public List<Menu> getChild(int id,List<Authority> authorities){
        List<Menu> childList = new ArrayList<>();
        for (Menu menu : authorities) {
            // 遍历所有节点，将父菜单id与传过来的id比较
            if (menu.getPid()!=null) {
                if (menu.getPid()==id) {
                    childList.add(menu);
                }
            }
        }
      //  System.out.println(childList);
        // 把子菜单的子菜单再循环一遍
        for (Menu menu : childList) {// 没有url子菜单还有子菜单
          // if (menu.getJShow()!=null) {
                // 递归
                menu.setChildMenus(getChild(menu.getJId(), authorities));
           // }
        } // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }
    @Cacheable(value = "users",key = "1024")
    public List<Menu> selectAll(){
        List<Authority> authorityList=authorityMapper.selectAll();
        List<Menu> menu=new ArrayList<>(3);
        for(int i=0;i<authorityList.size();i++){
            if(authorityList.get(i).getPid()==null){
                menu.add(authorityList.get(i));
            }
        }
        for (Menu menus:menu){
            menus.setChildMenus(getChild(menus.getJId(),authorityList));
        }
        return menu;
    }

}

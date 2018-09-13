package com.neo.drools.controller;

import com.neo.drools.KieSessionUtils;
import com.neo.drools.model.Address;
import com.neo.drools.model.fact.AddressCheckResult;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;


@RequestMapping("/test")
@Controller
public class TestController {

    //测试循环的
    @ResponseBody
    @RequestMapping("/address")
    public void test(int num) throws Exception {
        Address address = new Address();
        address.setPostcode(generateRandom(num));
        KieSession kieSession = KieSessionUtils.newKieSession("address.drl");

        AddressCheckResult result = new AddressCheckResult();
        kieSession.insert(address);
        kieSession.insert(result);
        int ruleFiredCount = kieSession.fireAllRules();
        kieSession.destroy();
        System.out.println("触发了" + ruleFiredCount + "条规则");

        if(result.isPostCodeResult()){
            System.out.println("规则校验通过");
        }

    }
    @ResponseBody
    @RequestMapping("/group")
    public void testGroup() throws Exception {

        KieSession kieSession = KieSessionUtils.newKieSession("group.drl");
        kieSession.getAgenda().getAgendaGroup("testFlow").setFocus();
        AddressCheckResult result2 = new AddressCheckResult(11);
        AddressCheckResult result1 = new AddressCheckResult(9);
        kieSession.insert(result1);
        kieSession.insert(result2);
        int ruleFiredCount = kieSession.fireAllRules();
        kieSession.destroy();
        System.out.println("触发了" + ruleFiredCount + "条规则");
    }
    //测试全局变量
    @ResponseBody
    @RequestMapping("/global")
    public void global() throws Exception {
        KieSession kieSession = KieSessionUtils.newKieSession("global.drl");
        AddressCheckResult result1 = new AddressCheckResult(5);
        kieSession.setGlobal("map",new HashMap<String,String>());
        kieSession.insert(result1);
        int ruleFiredCount = kieSession.fireAllRules();
        kieSession.destroy();
        System.out.println("触发了" + ruleFiredCount + "条规则");
    }

    /**
     * 生成随机数
     * @param num
     * @return
     */
    public String generateRandom(int num) {
        String chars = "0123456789";
        StringBuffer number=new StringBuffer();
        for (int i = 0; i < num; i++) {
            int rand = (int) (Math.random() * 10);
            number=number.append(chars.charAt(rand));
        }
        return number.toString();
    }
}

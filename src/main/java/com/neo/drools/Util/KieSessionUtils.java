package com.neo.drools.Util;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 *
 * 1.维护了有状态和无状态的两种Session对象，不推荐使用get。
 * 2.全局的Session已经在config下的Configuration中自动装配了。
 * 3.无并发的情况下，使用newKieSession()既可。
 * @author Hong Wen 2018/04/04
 *
 */
public class KieSessionUtils {

    private static KieSession kieSession;
    private static final String RULES_PATH = "rules/";

    private KieSessionUtils() {

    }
    /**
     * @description TODO(创建包含所有规则的对象)
     * @throws Exception
     * @return KieSession
     */
    public static KieSession getAllRules() throws Exception {
        try {
            disposeKieSession();
            KieServices kieServices = KieServices.Factory.get();
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
            for (org.springframework.core.io.Resource file : new PathMatchingResourcePatternResolver().getResources("classpath*:" + RULES_PATH + "**/*.*")) {
                kieFileSystem.write(org.kie.internal.io.ResourceFactory.newClassPathResource(RULES_PATH + file.getFilename(), "UTF-8"));
            }
            final KieRepository kieRepository = KieServices.Factory.get().getRepository();
            kieRepository.addKieModule(new KieModule() {
                public ReleaseId getReleaseId() {
                    return kieRepository.getDefaultReleaseId();
                }
            });
            KieBuilder kieBuilder = KieServices.Factory.get().newKieBuilder(kieFileSystem);
            kieBuilder.buildAll();
            kieSession = KieServices.Factory.get().newKieContainer(kieRepository.getDefaultReleaseId()).newKieSession().getKieBase().newKieSession();
            return kieSession;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * @description TODO (快速新建KieSession)
     * @param classPath 绝对路径
     * @return KieSession 有状态
     */
    public static KieSession newKieSession(String classPath) throws Exception {
        KieSession kieSession = getKieBase(RULES_PATH + classPath).newKieSession();
        kieSession.addEventListener(new DebugRuleRuntimeEventListener());
        return kieSession;

    }

    /**
     * @description TODO (快速新建StatelessKieSession)
     * @param classPath 绝对路径
     * @return StatelessKieSession 无状态
     */
    public static StatelessKieSession newStatelessKieSession(String classPath) throws Exception {
        StatelessKieSession kiesession = getKieBase(RULES_PATH + classPath).newStatelessKieSession();
        return kiesession;

    }

    /**
     * @description TODO (清空对象)
     * @title disposeKieSession 重置KieSession
     * @return void
     */
    public static void disposeKieSession() {
        if (kieSession != null) {
            kieSession.dispose();
            kieSession = null;
        }
    }

    protected static KieBase getKieBase(String classPath) throws Exception {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        Resource resource = kieServices.getResources().newClassPathResource(classPath);
        resource.setResourceType(ResourceType.DRL);
        kfs.write(resource);
        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
        if (kieBuilder.getResults().getMessages(Message.Level.ERROR).size() > 0) {
            throw new Exception();
        }
        KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        KieBase kBase = kieContainer.getKieBase();
        return kBase;
    }



}
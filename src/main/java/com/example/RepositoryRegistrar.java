package com.example;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Created by PRASEN on 2/6/2016.
 */
public class RepositoryRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware, ResourceLoaderAware{
    private ConfigurableListableBeanFactory beanFactory;
    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(resourceLoader);
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        Map<String, Object> data = importingClassMetadata.getAnnotationAttributes(EnableRepo.class.getName());
        String basePackage = (String) data.get("basePackage");
        if (!StringUtils.hasText(basePackage)){
            basePackage = ClassUtils.getPackageName(importingClassMetadata.getClassName());
        }
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(basePackage) + "/" + "**/*.class";

        InvocationHandler handler = (proxy, method, args) -> {
            GreetingMeta greeting = AnnotationUtils.getAnnotation(method, GreetingMeta.class);
            if (greeting != null) {
                AnnotationAttributes attr = AnnotationUtils.getAnnotationAttributes(greeting, false, true);
                System.out.println(attr);
            }
            return null;
        };

        try {
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    Class repoClass = Class.forName(metadataReader.getClassMetadata().getClassName());
                    if (repoClass.isInterface() && AnnotationUtils.getAnnotation(repoClass, Repository.class) != null) {
                        Object bean = Proxy.newProxyInstance(repoClass.getClassLoader(), new Class[]{repoClass}, handler);
                        beanFactory.registerSingleton(Foo.class.getSimpleName(), bean);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}

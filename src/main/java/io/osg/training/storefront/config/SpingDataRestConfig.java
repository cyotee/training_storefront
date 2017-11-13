package io.osg.training.storefront.config;

import io.osg.training.storefront.model.entities.*;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class SpingDataRestConfig extends RepositoryRestConfigurerAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        List<Class<?>> classNameSet = getAllManagedEntityTypes(entityManagerFactory);
        for (Class<?> classToExpose : classNameSet) {
            config.exposeIdsFor(classToExpose);
        }

        logger.info("exposeIdsFor : " + classNameSet);
    }

    private List<Class<?>> getAllManagedEntityTypes(EntityManagerFactory entityManagerFactory) {
        List<Class<?>> entityClasses = new ArrayList<>();
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        for (ManagedType<?> managedType : metamodel.getManagedTypes()) {
            Class<?> javaType = managedType.getJavaType();
            if (javaType.isAnnotationPresent(Entity.class)) {
                entityClasses.add(managedType.getJavaType());
            }
        }
        return entityClasses;
    }
}

package ru.scheduler.bpmn;

import org.activiti.engine.cfg.ProcessEngineConfigurator;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.variable.JPAEntityListVariableType;
import org.activiti.engine.impl.variable.JPAEntityVariableType;
import org.activiti.engine.impl.variable.VariableTypes;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class BpmnConfig {

    @Bean
    public ProcessEngineConfigurationConfigurer processEngineConfigurationConfigurer() throws IOException {
        return processEngineConfiguration -> {

            ProcessEngineConfigurator processEngineConfigurator = new ProcessEngineConfigurator() {
                @Override
                public void beforeInit(ProcessEngineConfigurationImpl processEngineConfiguration) {

                }

                @Override
                public void configure(ProcessEngineConfigurationImpl processEngineConfiguration) {
                    VariableTypes variableTypes = processEngineConfiguration.getVariableTypes();
                    variableTypes.removeType(variableTypes.getVariableType(JPAEntityListVariableType.TYPE_NAME));
                    variableTypes.removeType(variableTypes.getVariableType(JPAEntityVariableType.TYPE_NAME));
                }

                @Override
                public int getPriority() {
                    return 0;
                }

            };

            processEngineConfiguration.addConfigurator(processEngineConfigurator);
        };
    }
}

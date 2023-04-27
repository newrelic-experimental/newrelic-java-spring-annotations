package com.nr.agent.fit.instrumentation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.instrumentation.classmatchers.ChildClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.DefaultClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.AllMethodsMatcher;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.WeaveIntoAllMethods;
import com.newrelic.api.agent.weaver.WeaveWithAnnotation;
import com.newrelic.api.agent.weaver.Weaver;

@WeaveWithAnnotation(annotationClasses = { "org.springframework.context.annotation.Configuration" },type=MatchType.Interface)
public class Configuration_Instrumentation {

	@WeaveWithAnnotation(annotationClasses= {"org.springframework.context.annotation.Bean"})
	@WeaveIntoAllMethods
	private static void instrumentation() {
		String name = null;

		Configuration configuration = Weaver.getClassAnnotation(Configuration.class);
		if(configuration != null) {
			name = configuration.value();
		}

		if(name == null || name.isEmpty()) {
			Class<?> thisClass = Configuration_Instrumentation.class;
			name = thisClass.getSimpleName();
		}

		Bean bean = Weaver.getClassAnnotation(Bean.class);
		String[] beanName = null;
		if(bean != null) {
			beanName = bean.name();
		}

		if(beanName == null || beanName.length == 0) {
			beanName = new String[] {"UnknownBean"};
		}

		StackTraceElement[] traces = Thread.currentThread().getStackTrace();
		StackTraceElement first = traces[1];
		String classname = first.getClassName();

		String methodName = first.getMethodName();
		Logger logger = NewRelic.getAgent().getLogger();


		try {
			Class<?> thisClass = Class.forName(classname);
			Method[] methods = thisClass.getDeclaredMethods();
			Method thisMethod = null;
			for(int i=0;i<methods.length && thisMethod == null;i++) {
				if(methods[i].getName().equalsIgnoreCase(methodName)) {
					thisMethod = methods[i];
				}
			}
			if(thisMethod != null) {
				Class<?> returnType = thisMethod.getReturnType();
				String returnClass = returnType.getName();
				if (!NRUtils.isInstrumented(returnClass)) {
					if (returnType.isInterface()) {
						InterfaceMatcher imatcher = new InterfaceMatcher(returnClass);
						AllMethodsMatcher mMatcher = new AllMethodsMatcher();
						DefaultClassAndMethodMatcher matcher = new DefaultClassAndMethodMatcher(imatcher, mMatcher);
						boolean success = ServiceFactory.getClassTransformerService().addTraceMatcher(matcher, "SpringBean");
						if (success) {
							NRUtils.addClass(returnClass);
							logger.log(Level.FINER, "Instrumented Spring Bean Interface: {0}", returnClass);
						} else {
							logger.log(Level.FINER, "Failed to Instrument Spring Bean Interface: {0}", returnClass);
						}
					} else {
						if (Modifier.isAbstract(returnType.getModifiers())) {
							ChildClassMatcher aMatcher = new ChildClassMatcher(classname);
							AllMethodsMatcher mMatcher = new AllMethodsMatcher();
							DefaultClassAndMethodMatcher matcher = new DefaultClassAndMethodMatcher(aMatcher, mMatcher);
							boolean success = ServiceFactory.getClassTransformerService().addTraceMatcher(matcher, "SpringBean");
							if(success) {
								NRUtils.addClass(returnClass);
								logger.log(Level.FINER, "Instrumented Spring Bean Abstract Class: {0}", returnClass);
							} else {
								logger.log(Level.FINER, "Failed to Instrument Spring Bean Abstract class: {0}", returnClass);
							}
						} else {
							AgentBridge.instrumentation.instrument(returnClass, "SpringBean");
							NRUtils.addClass(returnClass);
							logger.log(Level.FINER, "Instrumented Spring Bean Class: {0}", returnClass);
						}
					}
				}
			}
		} catch (ClassNotFoundException e) {
			logger.log(Level.FINEST, e, "Class not found: {0}", classname);
		} catch (SecurityException e) {
			logger.log(Level.FINEST, e, "Security problem with: {0}", classname);
		}
	}
}

package com.nr.agent.fit.instrumentation;

import org.springframework.stereotype.Repository;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.WeaveIntoAllMethods;
import com.newrelic.api.agent.weaver.WeaveWithAnnotation;
import com.newrelic.api.agent.weaver.Weaver;

@WeaveWithAnnotation(annotationClasses = { "org.springframework.stereotype.Repository" },type=MatchType.Interface)
public class SpringRepository_Instrumentation {

	@WeaveIntoAllMethods
	@Trace(dispatcher=true)
	private static void instrumentation() {
		String name = null;
		
		Repository serviceAnnotation = Weaver.getClassAnnotation(Repository.class);
		if(serviceAnnotation != null) {
			name = serviceAnnotation.value();
		}
		
		String methodName = "Unknown";
		if(name == null || name.isEmpty()) {
			Class<?> thisClass = SpringService_Instrumentation.class;
			name = thisClass.getSimpleName();
		}
		
		StackTraceElement[] traces = Thread.currentThread().getStackTrace();
		StackTraceElement first = traces[1];
		methodName = first.getMethodName();
		Transaction transaction = NewRelic.getAgent().getTransaction();
		if(transaction != null) {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","Repository",name,methodName});
			transaction.setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, false, "Spring-Repository", new String[] {name,methodName});
		}
	}
}

package com.nr.agent.fit.instrumentation;

import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;

@Weave(type=MatchType.Interface,originalName="org.springframework.context.ApplicationListener")
public class ApplicationListener_Instrumentation<E> {

}

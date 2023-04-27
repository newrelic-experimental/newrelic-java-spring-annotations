package com.nr.agent.fit.instrumentation;

import java.util.ArrayList;
import java.util.List;

public class NRUtils {

	
	private static List<String> instrumentedBeanClasses = new ArrayList<String>();
	
	public static boolean isInstrumented(String classname) {
		return instrumentedBeanClasses.contains(classname);
	}
	
	public static void addClass(String classname) {
		instrumentedBeanClasses.add(classname);
	}
}

/*******************************************************************************
 * Copyright (c) 2015 LG Electronics. All Rights Reserved. This software is the
 * confidential and proprietary information of LG Electronics. You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with LG Electronics.
 *******************************************************************************/
package br.ufc.great.caos.api.offload;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import br.ufc.great.caos.api.Caos;
import br.ufc.great.caos.api.config.Inject;
import br.ufc.great.caos.api.offload.Offloadable.Offload;
import br.ufc.great.caos.api.offloadingreasoner.OffloadingReasonerClient;
import br.ufc.great.caos.api.profile.ProfileMonitor;
import br.ufc.great.caos.api.util.ConnectionVerify;
import br.ufc.great.caos.api.util.Util;
import br.ufc.great.caos.api.util.device.Device;

/**
 * Offloading Monitor gets a annotated method and listen its execution sends
 * this invocable method to offloading controller
 */
public final class OffloadingMonitor implements InvocationHandler {

	public InvocableMethod invocableMethod;

	private OffloadingClient mOffloadingClient;

	private static final String CLASSNAME = OffloadingMonitor.class.getName();

	private Map<String, Offloadable> mMethodCache = new HashMap<String, Offloadable>(5);

	private Object mObjProxy;

	private OffloadingReasonerClient mOffloadingReasonerClient;

	private String mClassName;

	private Context mContext;

	private ConnectionVerify mConnectionVerify;

	private Device mDevice;

	private String mAppPackage;

	private static OffloadingMonitor sOffloadingMonitor = null;

	/**
	 * To create an instance of OffloadingMonitor, some arguments from the
	 * referred method and its class are needed in order to create an
	 * offloadable request.
	 *
	 * @param objProxy
	 *            - Proxy object from the class you want to offload
	 * @param interfaceType
	 *            - Interface for the offloadable class
	 * @param className
	 *            - Concrete class name
	 * @param context
	 *            - Android Application context
	 */
	private OffloadingMonitor(Object objProxy, Class<?> interfaceType, String className, Context context) {
		this.mContext = context;
		this.mObjProxy = objProxy;
		this.mClassName = className;

		this.mAppPackage = mContext.getPackageName();

		buildMethodCache(interfaceType);
		mOffloadingReasonerClient = OffloadingReasonerClient.getInstance();
		mDevice = new Device(context);
		mConnectionVerify = new ConnectionVerify(context);
		
		
		
	}

	public OffloadingMonitor() {

	}

	public static OffloadingMonitor getInstance() {
		if (sOffloadingMonitor == null) {
			sOffloadingMonitor = new OffloadingMonitor();
		}
		return sOffloadingMonitor;
	}
	
	
	

	/**
	 *
	 * This method ensures that the offloadable methods attend to the following
	 * rules:
	 *
	 * 1. Interface It must have an @Offloadable annotation in the methods which
	 * will be offloaded
	 *
	 * 2. Concrete class It must implement the Interface which contains the
	 * Offloadable methods
	 *
	 * 3. Class to offload It must have the @Inject annotation using the
	 * Interface.
	 *
	 * Example:
	 *
	 * interface InterfaceOffload {
	 *
	 * @Offloadable private int offloadableMethod();
	 *
	 *              }
	 *
	 *              public class ConcreteClass implements InterfaceOffload{
	 *
	 *              private int offloadableMethod(){ return 0; }
	 *
	 *              }
	 *
	 *              public MainClass{
	 *
	 * @Inject public InterfaceOffload offloadableClass;
	 *
	 *         public static void main(String[] args){
	 *
	 *         // This method will be offloadable
	 *         offloadableClass.offloadableMethod(); }
	 *
	 *         }
	 *
	 * @param objectToBeMonitored
	 *            - Object from the class to offload
	 * @param context
	 *            - Android Application Context
	 * @param offloadingServicePort
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public void injectObjects(Object objectToBeMonitored, Context context)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {

		Class<?> cls = objectToBeMonitored.getClass();

		Field fields[] = cls.getDeclaredFields();
		for (Field field : fields) {

			// Get all methods marked as "Inject"
			Inject methodsMarkedAsInject = field.getAnnotation(Inject.class);

			if (methodsMarkedAsInject != null) {
				field.setAccessible(true);

				// Those methods need to be inside an interface.
				if (field.getType().isInterface()) {
					Method methods[] = field.getType().getMethods();
					boolean remoteSupport = false;
					for (Method method : methods) {
						// Verify if there is some method marked as Offloadable
						if (method.getAnnotation(Offloadable.class) != null) {
							remoteSupport = true;
							break;
						}
					}
					if (remoteSupport) {
						field.set(objectToBeMonitored,
								OffloadingMonitor.newInstance(methodsMarkedAsInject.value(), field.getType(), context));
					} else {
						throw new InstantiationException(
								"The injection required a interface with remotable annotation!");
					}
				} else {
					throw new InstantiationException("The injection annotation required a object interface,"
							+ " not a concrete class or primitive type!");
				}
			}
		}
	}

	/**
	 * It creates an new instance from the offloading monitor, it also creates a
	 * Proxy Handler instances for the method.
	 * 
	 * @param offloadingServicePort
	 */
	public static Object newInstance(Class<?> cls, Class<?> interfaceType, Context context)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String className = cls.getCanonicalName();

		Log.i(CLASSNAME,
				"Creating proxy with interface: " + interfaceType.getSimpleName() + ", for class: " + className);

		Object objectInstance = Class.forName(cls.getName()).newInstance();
		return Proxy.newProxyInstance(cls.getClassLoader(), new Class<?>[] { interfaceType },
				new OffloadingMonitor(objectInstance, interfaceType, className, context));
	}
	
	

	/**
	 * It overrides the method invoke. During the method invocation, it checks
	 * if it can make an offloding request. If the offloading request is
	 * possible, it makes that request.
	 */
	@Override
	public Object invoke(Object original, Method method, Object[] params) throws Throwable {

		Offloadable offloadable = mMethodCache.get(generateKeyMethod(method));

		this.invocableMethod = new InvocableMethod(mAppPackage, mDevice.getApplicationVersion(), method.getName(),
				mClassName, params, Util.getIp(), String.valueOf(Util.getAppOffloading(mContext).length()));

		if (Caos.coapForOffloading) {
			mOffloadingClient = OffloadingClient.getInstance();
			if (offloadable != null && mConnectionVerify.hasConnection() == true && Caos.sCloudletIp != null) {
				if (offloadable.value() == Offload.STATIC) {
					return mOffloadingClient.sendOffloadingRequest(invocableMethod);
				} else {
					if (mOffloadingReasonerClient.makeDecision(invocableMethod)) {

						Object returnedObject = mOffloadingClient.sendOffloadingRequest(invocableMethod);

						// If there is some error, the execution must be local
						if (returnedObject instanceof OffloadingError) {
							Log.e("caos-api", "We got some error during the offloading, the execution will be local");
							return executeLocal(method, params);
						} else {
							return returnedObject;
						}
					}
				}
			}
		} else {

			if (offloadable != null && mConnectionVerify.hasConnection() == true && Caos.sCloudletIp != null) {
				if (offloadable.value() == Offload.STATIC) {
					Object returnedObject = OffloadingClientNoCoap.sendObject(invocableMethod);
					// If there is some error, the execution must be local
					if (returnedObject instanceof OffloadingError) {
						Log.e("caos-api",
								"We got some error during the offloading, " + "the execution will be local");
						return executeLocal(method, params);
					} else {
						return returnedObject;
					}
				} else {
					if (mOffloadingReasonerClient.makeDecision(invocableMethod)) {
						Object returnedObject = OffloadingClientNoCoap.sendObject(invocableMethod);

						// If there is some error, the execution must be local
						if (returnedObject instanceof OffloadingError) {
							Log.e("caos-api",
									"We got some error during the offloading, " + "the execution will be local");
							return executeLocal(method, params);
						} else {
							return returnedObject;
						}
					}
				}
			}
		}

		return executeLocal(method, params);
	}

	/*
	 * It executes the method locally and also send some informations for
	 * profile monitor
	 */
	private Object executeLocal(Method method, Object[] params)
			throws IllegalAccessException, InvocationTargetException, IOException {

		long initTime = System.currentTimeMillis();
		Object stuff = method.invoke(mObjProxy, params);
		long execTime = System.currentTimeMillis() - initTime;
		ProfileMonitor.registerLocalExecution(invocableMethod, execTime);

		return stuff;
	}

	private String generateKeyMethod(Method method) {
		StringBuilder key = new StringBuilder();
		key.append(method.getName()).append("-");
		for (Class<?> cls : method.getParameterTypes()) {
			key.append(":").append(cls.getName());
		}
		return key.toString();
	}

	private void buildMethodCache(Class<?> interfaceType) {
		Method methods[] = interfaceType.getDeclaredMethods();
		for (Method method : methods) {
			Offloadable remotable = method.getAnnotation(Offloadable.class);
			if (remotable != null) {
				mMethodCache.put(generateKeyMethod(method), remotable);
			}
		}
	}

}
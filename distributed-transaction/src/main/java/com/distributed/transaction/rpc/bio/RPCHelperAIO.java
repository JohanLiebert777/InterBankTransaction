package com.distributed.transaction.rpc.bio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import javassist.bytecode.ByteArray;

public class RPCHelperAIO {
	private static class Input implements Serializable {

		private static final long serialVersionUID = 3391752684024861342L;
		private String className;
		private String methodName;
		private Class<?>[] paramTypes;
		private Object[] parameters;

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public String getMethodName() {
			return methodName;
		}

		public void setMethodName(String methodName) {
			this.methodName = methodName;
		}

		public Class<?>[] getParamTypes() {
			return paramTypes;
		}

		public void setParamTypes(Class<?>[] paramTypes) {
			this.paramTypes = paramTypes;
		}

		public Object[] getParameters() {
			return parameters;
		}

		public void setParameters(Object[] parameters) {
			this.parameters = parameters;
		}

	}

	private static class ConnectHandler implements CompletionHandler<Void, AsynchronousSocketChannel> {

		private Input input;

		public ConnectHandler(Input input) {
			this.input = input;
		}

		@Override
		public void completed(Void result, AsynchronousSocketChannel attachment) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(input);
			} catch (IOException e) {
				e.printStackTrace();
			}

			ByteBuffer bb = ByteBuffer.allocate(1024);
			bb.put(baos.toByteArray());
			attachment.write(bb, bb, new Writerhandler());
		}

		@Override
		public void failed(Throwable exc, AsynchronousSocketChannel attachment) {

		}
	}

	private static class Writerhandler implements CompletionHandler<Integer, ByteBuffer> {
		@Override
		public void completed(Integer result, ByteBuffer attachment) {
			attachment.flip();
			attachment.clear();
		}

		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
		}
	}

	public Object callRemote(Class<?> clazz) {
		return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

				final String className = clazz.getName();
				final String methodName = method.getName();
				final Class<?>[] paramTypes = method.getParameterTypes();
				final Object[] parameters = args;

				Input input = new Input();
				input.setClassName(className);
				input.setMethodName(methodName);
				input.setParamTypes(paramTypes);
				input.setParameters(parameters);

				AsynchronousSocketChannel asc = AsynchronousSocketChannel.open();
				asc.connect(new InetSocketAddress("IP", 8888), asc, new ConnectHandler(input));
				// Wait until connected

				return null;
			}
		});
	}

}

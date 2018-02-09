package com.distributed.transaction.rpc.bio;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

import org.springframework.stereotype.Component;

public class RPCHelperNIO {

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

				Selector selector = Selector.open();
				SocketChannel sc = SocketChannel.open();
				sc.bind(new InetSocketAddress("IP", 8888));
				sc.register(selector, SelectionKey.OP_CONNECT, input);

				selector.select();
				Set<SelectionKey> keys = selector.selectedKeys();
				for (SelectionKey key : keys) {
					if (key.isValid()) {
						if (key.isConnectable()) {
							SocketChannel connectedSC = (SocketChannel) key.channel();
							connectedSC.finishConnect();
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ObjectOutputStream oos = new ObjectOutputStream(baos);
							oos.writeObject(input);
							ByteBuffer bb = ByteBuffer.allocate(1024);
							bb.put(baos.toByteArray());
							connectedSC.write(bb);
							bb.flip();
							bb.clear();
						} else if (key.isReadable()) {
							SocketChannel connectedSC = (SocketChannel) key.channel();
							ByteBuffer bb = ByteBuffer.allocate(1024);
							connectedSC.read(bb);
							bb.flip();
						}
						keys.remove(key);
					}
				}
				return null;
			}
		});
	}

	public void answerCall() throws IOException {
		Selector selector = Selector.open();
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.bind(new InetSocketAddress(8888));
		ssc.register(selector, SelectionKey.OP_ACCEPT);
		selector.select();
		Set<SelectionKey> keys = selector.selectedKeys();
		for (SelectionKey key : keys) {
			if (key.isValid()) {
				if (key.isAcceptable()) {
					ServerSocketChannel acceptSSC = (ServerSocketChannel) key.channel();
					SocketChannel sc = acceptSSC.accept();
					sc.register(selector, SelectionKey.OP_READ);
				} else if (key.isReadable()) {
					SocketChannel sc = (SocketChannel) key.channel();
					ByteBuffer bb = ByteBuffer.allocate(1024);
					sc.read(bb);
					bb.flip();
				}
				keys.remove(key);
			}
		}
	}
}

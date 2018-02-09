package com.distributed.transaction.rpc.bio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.stereotype.Component;

import com.distributed.transaction.service.BankAAccountService;

public class RPCHelperBIO {

	public Object callRemote(final Class<?> clazz) {
		return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Socket socket = null;
				ObjectOutputStream objOS = null;
				ObjectInputStream objIS = null;
				try {
					socket = new Socket("", 8888);
					String apiClassName = clazz.getName();
					String methodName = method.getName();
					Class<?>[] parameterTypes = method.getParameterTypes();
					objOS = new ObjectOutputStream(socket.getOutputStream());
					objOS.writeUTF(apiClassName);
					objOS.writeUTF(methodName);
					objOS.writeObject(parameterTypes);
					objOS.writeObject(args);
					objOS.flush();

					objIS = new ObjectInputStream(socket.getInputStream());
					Object o = objIS.readObject();
					return o;
				} catch (Exception e) {
					System.err.println(e.getStackTrace());
				} finally {
					objIS.close();
					objOS.close();
					socket.close();
				}
				return null;
			}
		});
	}

	public void answerRemote() throws IOException {
		ServerSocket ss = null;
		Socket socket = null;
		ObjectInputStream objIS = null;
		ObjectOutputStream objOS = null;
		try {
			ss = new ServerSocket(8888);
			while (true) {
				socket = ss.accept();
				objIS = new ObjectInputStream(socket.getInputStream());
				String apiClassName = objIS.readUTF();
				String methodName = objIS.readUTF();
				Class<?>[] parameterTypes = (Class[]) objIS.readObject();
				Object[] parameters = (Object[]) objIS.readObject();

				Class<?> clazz = null;
				if (apiClassName.equalsIgnoreCase(BankAAccountService.class.getName())) {
					clazz = BankAAccountService.class;
				}

				Method method = clazz.getMethod(methodName, parameterTypes);
				Object result = method.invoke(clazz.newInstance(), parameters);

				objOS = new ObjectOutputStream(socket.getOutputStream());
				objOS.writeObject(result);
				objOS.flush();
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		} finally {
			objOS.close();
			ss.close();
			socket.close();
			objIS.close();
		}
	}

}

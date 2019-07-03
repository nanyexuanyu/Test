package com.pki.system;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.pki.bean.RSA;
import com.pki.utils.Xdom;

public class Init extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public Init() {
		super();
	}

	public void init() throws ServletException {
		String PATH = getServletContext().getRealPath("/WEB-INF/") + "/key.xml";
		System.out.println(PATH);
		Map<String, String> map = Xdom.getDomNodeAttribute(PATH);
		if (map.size() < 2) {
			System.out.println("读取文件成功，没有找到密钥信息，重新初始化");
			Xdom.setDomNodeAttribute(PATH, RSA.getInstance().initKey());
		} else {
			RSA.getInstance().setPUBLIC_KEY(map.get("pubkey"));
			RSA.getInstance().setPRIVATE_KEY(map.get("prikey"));
			System.out.println("私钥》》》》》》 " + RSA.getInstance().getPRIVATE_KEY());
			System.out.println("公钥 》》》》》》 " + RSA.getInstance().getPUBLIC_KEY());
		}
	}
}

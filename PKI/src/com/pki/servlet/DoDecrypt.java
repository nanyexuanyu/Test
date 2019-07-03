package com.pki.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pki.bean.RSA;

@WebServlet("/DoDecrypt")
public class DoDecrypt extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public DoDecrypt() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String data = request.getParameter("text");
		System.out.println("data:"+data);
		if (data != null && !"".equals(data)) {
			out.print(RSA.getInstance().decryptByPrivateKey(data, RSA.getInstance().getPRIVATE_KEY()));
		}
		out.flush();
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}

package com.restkeeper.utils;
import com.alibaba.fastjson.support.hsf.HSFJSONUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


public class MD5CryptUtil
{

	public static String getSalts(String password) {
		String[] salts = password.split("\\$");
		if (salts.length < 1) {
			return "";
		}
		String mysalt = "";
		for (int i = 1; i < 3; i++) {
			mysalt += "$" + salts[i];
		}
		mysalt += "$";
		return mysalt;
	}
    
	public static void main(String[] args) {
		//摘要
		System.out.println(DigestUtils.md5Hex("admin"+System.currentTimeMillis()));
		
		System.out.println(DigestUtils.md5Hex("admin"+System.currentTimeMillis()));
		
		System.out.println("---------"+ UUID.randomUUID());
		
		System.out.println(Md5Crypt.md5Crypt("admin".getBytes()));

		System.out.println(Md5Crypt.md5Crypt("admin".getBytes()));

		System.out.println("0-0-0-0-0-0-0-0-0-0-0-0-0-0-");


		String clearPwd = "lishi";
		//这种方式实际上就只是对
		String pwd = Md5Crypt.md5Crypt(clearPwd.getBytes(StandardCharsets.UTF_8));
		System.out.println("数据库中实际上存储的密文 "+pwd);
		String salts = getSalts(pwd);
		System.out.println("盐值 "+salts);
		//通过明文和盐值获取到密文
		String md5Crypt = Md5Crypt.md5Crypt(clearPwd.getBytes(StandardCharsets.UTF_8), salts);
		System.out.println("明文和盐值 "+md5Crypt);

		//为什么要获取盐值，你不能在对该明文再次进行md5加密，然后比较两个值是否相等，因为每次获取的md5值不一样
		//1.md5是没办法通过解密的，你永远页不知道密文
		//2.md5算法每次得到的结果都不一样
		//3.md5提供了salt，你可以获取salt，假如黑客获取到了md5密文，黑客也能获取到salt
		//4.salt的作用是什么？ salt配合明文能计算出密文
		//5.黑客没办法获取到明文，即使有salt也没用，因为黑客没办法通过虽然密码的方式来将某个字符串转换成他想要的md5密文
		String newPwd = Md5Crypt.md5Crypt(clearPwd.getBytes(StandardCharsets.UTF_8));
		System.out.println("再次进行md5加密 " +newPwd);


	}


}

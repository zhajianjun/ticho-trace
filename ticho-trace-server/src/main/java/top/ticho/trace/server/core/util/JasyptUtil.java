package top.ticho.trace.server.core.util;

import org.jasypt.util.text.StrongTextEncryptor;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-04-04 09:03
 */
public class JasyptUtil {

    public static String encrypt(String str, String password) {
        StrongTextEncryptor textEncryptor = getStrongTextEncryptor(password);
        return textEncryptor.encrypt(str);
    }

    public static String decrypt(String str, String password) {
        StrongTextEncryptor textEncryptor = getStrongTextEncryptor(password);
        return textEncryptor.decrypt(str);
    }

    private static StrongTextEncryptor getStrongTextEncryptor(String password) {
        StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
        textEncryptor.setPassword(password);
        return textEncryptor;
    }


    public static void main(String[] args) {
        String salt = "123456";
        StrongTextEncryptor textEncryptor = getStrongTextEncryptor(salt);
        //要加密的数据（数据库的用户名或密码）
        String host = textEncryptor.encrypt("192.168.243.138:10090");
        String username = textEncryptor.encrypt("admin");
        String password = textEncryptor.encrypt("admin");
        System.out.println("host: " + host);
        System.out.println("username：" + username);
        System.out.println("password：" + password);
    }

}

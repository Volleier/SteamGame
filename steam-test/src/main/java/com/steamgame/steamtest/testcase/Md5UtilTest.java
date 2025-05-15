package com.steamgame.steamtest.testcase;

import com.SteamGame.util.Md5Util;
import org.testng.annotations.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.testng.AssertJUnit.assertEquals;


@SpringBootTest
public class Md5UtilTest {

    @Test
    public void testEncryptWithRegularString() {
        // 测试普通字符串
        String input = "hello";
        String expectedMd5 = "5d41402abc4b2a76b9719d911017c592";
        
        String result = Md5Util.encrypt(input);
        
        assertEquals(expectedMd5, result);
    }
    
    @Test
    public void testEncryptWithEmptyString() {
        // 测试空字符串
        String input = "";
        String expectedMd5 = "d41d8cd98f00b204e9800998ecf8427e";
        
        String result = Md5Util.encrypt(input);
        
        assertEquals(expectedMd5, result);
    }
    
    @Test
    public void testEncryptWithSpecialCharacters() {
        // 测试特殊字符
        String input = "!@#$%^&*()";
        String expectedMd5 = "3bad6af0fa4b8b42d46ef588e2535ae4";
        
        String result = Md5Util.encrypt(input);
        
        assertEquals(expectedMd5, result);
    }
    
    @Test
    public void testEncryptWithChineseCharacters() {
        // 测试中文字符
        String input = "你好";
        String expectedMd5 = "c50de6c887d23676fa37a4b174300120";
        
        String result = Md5Util.encrypt(input);
        
        assertEquals(expectedMd5, result);
    }
}
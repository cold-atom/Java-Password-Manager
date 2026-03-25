import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class VigenereCipher {
    private static final String KEY = loadKeyFromEnv();

    private static String loadKeyFromEnv() {
        String envPath = ".env";
        String parentEnvPath = "../.env";
        
        BufferedReader reader = null;
        try {
            try {
                reader = new BufferedReader(new FileReader(envPath));
            } catch (IOException e) {
                reader = new BufferedReader(new FileReader(parentEnvPath));
            }
            
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("VIGENERE_KEY=")) {
                    String key = line.substring("VIGENERE_KEY=".length()).trim();
                    if (!key.isEmpty()) {
                        if (reader != null) {
                            reader.close();
                        }
                        return key;
                    }
                }
            }
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not read .env file. Using default key.");
        }
        return "SECUREKEY";
    }

    private static char encryptChar(char ch, char keyCh) {
        if (ch < 32 || ch > 126) {
            return ch;
        }
        int base = 32;
        int range = 95;
        int p = ch - base;
        int k = keyCh % range;
        int c = (p + k) % range;
        return (char) (c + base);
    }

    private static char decryptChar(char ch, char keyCh) {
        if (ch < 32 || ch > 126) {
            return ch;
        }
        int base = 32;
        int range = 95;
        int c = ch - base;
        int k = keyCh % range;
        int p = (c - k + range) % range;
        return (char) (p + base);
    }

    public static String encrypt(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        StringBuilder sb = new StringBuilder();
        int keyLen = KEY.length();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            char keyCh = KEY.charAt(i % keyLen);
            sb.append(encryptChar(ch, keyCh));
        }
        return sb.toString();
    }

    public static String decrypt(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        StringBuilder sb = new StringBuilder();
        int keyLen = KEY.length();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            char keyCh = KEY.charAt(i % keyLen);
            sb.append(decryptChar(ch, keyCh));
        }
        return sb.toString();
    }
}
